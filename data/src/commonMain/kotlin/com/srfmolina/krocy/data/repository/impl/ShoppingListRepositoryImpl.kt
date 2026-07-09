package com.srfmolina.krocy.data.repository.impl

import com.srfmolina.krocy.data.datasource.remote.generic.GenericEntityDataSource
import com.srfmolina.krocy.data.datasource.remote.shoppinglist.ShoppingListDataSource
import com.srfmolina.krocy.data.mapper.toShoppingListEntry
import com.srfmolina.krocy.data.repository.impl.ShoppingListRepositoryImpl.Companion.MANUAL_NOTE
import com.srfmolina.krocy.domain.model.shoppinglist.ShoppingListEntry
import com.srfmolina.krocy.domain.repository.ShoppingListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.openapitools.client.models.ObjectsEntityGet200ResponseInner

internal class ShoppingListRepositoryImpl(
    private val shoppingListDataSource: ShoppingListDataSource,
    private val genericEntityDataSource: GenericEntityDataSource,
) : ShoppingListRepository {

    private val _cache = MutableStateFlow<List<ShoppingListEntry>>(emptyList())

    private val refreshMutex = Mutex()
    @Volatile private var loaded = false
    @Volatile private var listId: Int? = null

    override fun getShoppingList(): Flow<List<ShoppingListEntry>> = _cache
        .onSubscription { ensureLoaded() }

    /** Syncs min-stock deficits and loads the list once; later subscribers reuse the cached value. */
    private suspend fun ensureLoaded() {
        if (loaded) return
        refreshMutex.withLock {
            if (loaded) return
            syncAndRefresh()
        }
    }

    override suspend fun setDone(entryId: Int, done: Boolean) {
        shoppingListDataSource.setDone(entryId, done).getOrThrow()
        // Patch the cache in place: a full refetch here would defeat the instant cross-off.
        _cache.update { entries ->
            entries.map { if (it.id == entryId) it.copy(done = done) else it }
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
                // Merge into the existing row and claim it as manual, so the sync stops
                // managing it: the user explicitly asked for more than the deficit.
                shoppingListDataSource.updateItem(
                    itemId = existing.id ?: error("Shopping list row without id"),
                    amount = (existing.amount ?: 0.0) + amount,
                    quId = existing.quId,
                    note = MANUAL_NOTE,
                ).getOrThrow()
            } else {
                shoppingListDataSource.createItem(
                    listId = id,
                    productId = productId,
                    amount = amount,
                    quId = products.firstOrNull { it.id == productId }?.quIdStock,
                    note = MANUAL_NOTE,
                ).getOrThrow()
            }
            refreshItems(products)
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
     * Rows added by hand carry [MANUAL_NOTE] and are never synced; a crossed-off manual row
     * is considered bought and cleared on the next sync.
     */
    private suspend fun reconcile(products: List<ObjectsEntityGet200ResponseInner>) {
        val id = ensureListId()
        val missingByProduct = shoppingListDataSource.getMissingProducts().getOrThrow()
            .mapNotNull { missing -> missing.id?.let { it to (missing.amountMissing ?: 0.0) } }
            .toMap()
        val productsById = products.associateBy { it.id }
        val rows = shoppingListDataSource.getItems().getOrThrow()
            .filter { it.shoppingListId == id }

        val (manualRows, autoRows) = rows.partition { it.note == MANUAL_NOTE }

        val (doneManualRows, keptManualRows) = manualRows.partition { it.done == 1 }
        doneManualRows.forEach { row ->
            row.id?.let { shoppingListDataSource.deleteItem(it).getOrThrow() }
        }

        val keptAutoRows = mutableListOf<ObjectsEntityGet200ResponseInner>()
        autoRows.forEach { row ->
            val rowId = row.id ?: return@forEach
            val productId = row.productId
            if (productId == null) {
                // Note-only rows (other clients allow them): not ours to manage.
                keptAutoRows += row
                return@forEach
            }
            val deficit = missingByProduct[productId] ?: 0.0
            val stockQuId = productsById[productId]?.quIdStock
            if (deficit <= 0.0) {
                shoppingListDataSource.deleteItem(rowId).getOrThrow()
            } else {
                if (row.amount != deficit || row.quId != stockQuId) {
                    shoppingListDataSource.updateItem(rowId, deficit, stockQuId).getOrThrow()
                }
                keptAutoRows += row
            }
        }

        // Deficits with no row yet. A manual row counts as listed: the user already decided
        // how much of that product to buy.
        val listedProducts = (keptManualRows + keptAutoRows).mapNotNull { it.productId }.toSet()
        missingByProduct.forEach { (productId, deficit) ->
            if (deficit > 0.0 && productId !in listedProducts) {
                shoppingListDataSource.createItem(
                    listId = id,
                    productId = productId,
                    amount = deficit,
                    quId = productsById[productId]?.quIdStock,
                    note = null,
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

        _cache.update {
            rows.mapNotNull { row ->
                // Note-only rows (no product) are possible in grocy; the list shows products only.
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
    }

    private companion object {
        const val DEFAULT_LIST_NAME = "Lista de la compra"

        /** Note marker on rows the user added by hand, which the deficit sync must not touch. */
        const val MANUAL_NOTE = "krocy:manual"
    }
}
