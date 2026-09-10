package com.srfmolina.krocy.data.repository.impl

import com.srfmolina.krocy.data.datasource.remote.generic.GenericEntityDataSource
import com.srfmolina.krocy.data.datasource.remote.shoppinglist.ShoppingListDataSource
import com.srfmolina.krocy.data.mapper.toShoppingListEntry
import com.srfmolina.krocy.domain.model.shoppinglist.ShoppingListEntry
import com.srfmolina.krocy.domain.repository.ShoppingListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.openapitools.client.models.ObjectsEntityGet200ResponseInner

internal class ShoppingListRepositoryImpl(
    private val shoppingListDataSource: ShoppingListDataSource,
    private val genericEntityDataSource: GenericEntityDataSource,
) : ShoppingListRepository {

    // null until the first successful load: nothing is emitted before then, and a failed
    // load leaves it null without ending anyone's collection.
    private val _cache = MutableStateFlow<List<ShoppingListEntry>?>(null)

    private val refreshMutex = Mutex()
    @Volatile private var loaded = false
    @Volatile private var listId: Int? = null

    override fun getShoppingList(): Flow<List<ShoppingListEntry>> = _cache.filterNotNull()

    /** Syncs min-stock deficits and loads the list once; later calls are no-ops. */
    override suspend fun ensureLoaded() {
        if (loaded) return
        refreshMutex.withLock {
            if (loaded) return
            syncAndRefresh()
        }
    }

    override suspend fun setDone(entryId: Int, done: Boolean) {
        // Serialized with refreshes: otherwise a refresh that fetched its rows before this
        // PUT would later overwrite the patch below with its stale snapshot.
        refreshMutex.withLock {
            shoppingListDataSource.setDone(entryId, done).getOrThrow()
            // Patch the cache in place: a full refetch here would defeat the instant cross-off.
            _cache.update { entries ->
                entries?.map { if (it.id == entryId) it.copy(done = done) else it }
            }
        }
    }

    override suspend fun addProduct(productId: Int, amount: Double) {
        refreshMutex.withLock {
            val id = ensureListId()
            val products = genericEntityDataSource.getProducts().getOrThrow()
            val existing = shoppingListDataSource.getItems().getOrThrow()
                .filter { it.shoppingListId == id }
                .firstOrNull { it.productId == productId }
            if (existing != null) {
                // Merge and hand the row over to the user: clear the auto marker (the user
                // overrode the deficit) and un-cross it (re-adding means it needs buying
                // again — a crossed-off row would be cleared by the next sync).
                shoppingListDataSource.updateItem(
                    itemId = existing.id ?: error("Shopping list row without id"),
                    amount = (existing.amount ?: 0.0) + amount,
                    quId = existing.quId,
                    note = if (existing.note == KROCY_AUTO) "" else null,
                    done = false,
                ).getOrThrow()
            } else {
                shoppingListDataSource.createItem(
                    listId = id,
                    productId = productId,
                    amount = amount,
                    quId = products.firstOrNull { it.id == productId }?.quIdStock,
                    note = null,
                ).getOrThrow()
            }
            // Before the first successful load a bare refresh would publish a list lacking
            // the deficit rows, and nothing retries the load later: sync them here instead.
            if (loaded) refreshItems(products) else syncAndRefresh()
        }
    }

    override suspend fun forceRefresh() {
        refreshMutex.withLock {
            syncAndRefresh()
        }
    }

    private suspend fun syncAndRefresh() {
        val products = genericEntityDataSource.getProducts().getOrThrow()
        reconcile(products)
        refreshItems(products)
        loaded = true
    }

    /**
     * Brings the list in line with the current min-stock deficits. Grocy's own
     * `add-missing-products` endpoint cannot do this: it never updates amounts, never removes
     * satisfied rows, and stamps rows with the purchase unit although the amount it writes is
     * in stock units (verified against the demo server, 2026-07-09). So the deficits come from
     * `/stock/volatile` and the rows are managed here, always in stock units.
     *
     * Only rows carrying [KROCY_AUTO] are owned by this sync (created, resized, removed).
     * Every other row — added here by hand, created in another grocy client, or note-only —
     * is a user row: its amount and note are never touched, and it is deleted only once
     * crossed off ("bought"), regardless of which client crossed it off.
     */
    private suspend fun reconcile(products: List<ObjectsEntityGet200ResponseInner>) {
        val id = ensureListId()
        val missingProducts = shoppingListDataSource.getMissingProducts().getOrThrow()
        // A null amountMissing is "unknown", not "no deficit": those products are skipped
        // entirely, so their rows are neither resized nor deleted and none are created.
        val unknownDeficits = missingProducts
            .mapNotNull { missing -> missing.id.takeIf { missing.amountMissing == null } }
            .toSet()
        val missingByProduct = missingProducts
            .mapNotNull { missing ->
                val productId = missing.id ?: return@mapNotNull null
                val amount = missing.amountMissing ?: return@mapNotNull null
                productId to amount
            }
            .toMap()
        val productsById = products.associateBy { it.id }
        val rows = shoppingListDataSource.getItems().getOrThrow()
            .filter { it.shoppingListId == id }

        val (autoRows, userRows) = rows.partition { it.note == KROCY_AUTO }

        // A crossed-off user row was bought: clear it.
        val (doneUserRows, keptUserRows) = userRows.partition { it.done == 1 }
        doneUserRows.forEach { row ->
            row.id?.let { shoppingListDataSource.deleteItem(it).getOrThrow() }
        }

        val keptAutoRows = mutableListOf<ObjectsEntityGet200ResponseInner>()
        autoRows.forEach { row ->
            val rowId = row.id ?: return@forEach
            val productId = row.productId
            if (productId == null || productId in unknownDeficits) {
                keptAutoRows += row
                return@forEach
            }
            val deficit = missingByProduct[productId] ?: 0.0
            val stockQuId = productsById[productId]?.quIdStock
            if (deficit <= 0.0) {
                shoppingListDataSource.deleteItem(rowId).getOrThrow()
            } else {
                // Only compare units when the stock unit is known: a null quId is omitted
                // from the PUT body, so that comparison could never converge.
                if (row.amount != deficit || (stockQuId != null && row.quId != stockQuId)) {
                    shoppingListDataSource.updateItem(rowId, deficit, stockQuId).getOrThrow()
                }
                keptAutoRows += row
            }
        }

        // Deficits with no row yet. A user row counts as listed: the user already decided
        // how much of that product to buy.
        val listedProducts = (keptUserRows + keptAutoRows).mapNotNull { it.productId }.toSet()
        missingByProduct.forEach { (productId, deficit) ->
            if (deficit > 0.0 && productId !in listedProducts) {
                shoppingListDataSource.createItem(
                    listId = id,
                    productId = productId,
                    amount = deficit,
                    quId = productsById[productId]?.quIdStock,
                    note = KROCY_AUTO,
                ).getOrThrow()
            }
        }
    }

    /** First existing shopping list, or a newly created one — grocy servers can have none at all. */
    private suspend fun ensureListId(): Int {
        listId?.let { return it }
        val lists = shoppingListDataSource.getShoppingLists().getOrThrow()
        val id = lists.firstOrNull()?.id
            ?: shoppingListDataSource.createShoppingList(DEFAULT_LIST_NAME).getOrThrow()
        listId = id
        return id
    }

    private suspend fun refreshItems(products: List<ObjectsEntityGet200ResponseInner>) {
        val id = ensureListId()
        val rows = shoppingListDataSource.getItems().getOrThrow()
            .filter { it.shoppingListId == id }
        val productsById = products.associateBy { it.id }
        val groups = genericEntityDataSource.getProductGroups().getOrThrow().associateBy { it.id }
        val units = genericEntityDataSource.getQuantityUnits().getOrThrow().associateBy { it.id }

        _cache.value = rows.mapNotNull { row ->
            // Rows that can't be resolved to a product aren't renderable: note-only rows
            // (other clients allow them) and rows whose product is missing from the fetch.
            val product = productsById[row.productId] ?: return@mapNotNull null
            val unit = units[row.quId ?: product.quIdStock]
            val singularName = unit?.name ?: "ud"
            val pluralName = unit?.namePlural?.takeIf { it.isNotBlank() } ?: singularName
            row.toShoppingListEntry(
                productName = product.name.orEmpty(),
                groupName = groups[product.productGroupId]?.name?.takeIf { it.isNotBlank() },
                unitNames = Pair(singularName, pluralName),
            )
        }
    }

    private companion object {
        const val DEFAULT_LIST_NAME = "Lista de la compra"

        /** Marker on rows this sync created for deficits; see [reconcile] for the ownership rules. */
        const val KROCY_AUTO = "krocy:auto"
    }
}
