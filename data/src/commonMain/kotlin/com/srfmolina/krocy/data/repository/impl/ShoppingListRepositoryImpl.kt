package com.srfmolina.krocy.data.repository.impl

import com.srfmolina.krocy.data.datasource.remote.generic.GenericEntityDataSource
import com.srfmolina.krocy.data.datasource.remote.shoppinglist.ShoppingListDataSource
import com.srfmolina.krocy.data.mapper.toShoppingListEntry
import com.srfmolina.krocy.domain.model.shoppinglist.ShoppingListEntry
import com.srfmolina.krocy.domain.repository.ShoppingListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

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
            shoppingListDataSource.addProduct(id, productId, amount).getOrThrow()
            refreshItems()
        }
    }

    override suspend fun forceRefresh() {
        refreshMutex.withLock {
            syncAndRefresh()
        }
    }

    private suspend fun syncAndRefresh() {
        shoppingListDataSource.addMissingProducts(ensureListId()).getOrThrow()
        refreshItems()
        loaded = true
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

    private suspend fun refreshItems() {
        val id = ensureListId()
        val rows = shoppingListDataSource.getItems().getOrThrow()
            .filter { it.shoppingListId == id }
        val products = genericEntityDataSource.getProducts().getOrThrow().associateBy { it.id }
        val groups = genericEntityDataSource.getProductGroups().getOrThrow().associateBy { it.id }
        val units = genericEntityDataSource.getQuantityUnits().getOrThrow().associateBy { it.id }

        _cache.update {
            rows.mapNotNull { row ->
                // Note-only rows (no product) are possible in grocy; the list shows products only.
                val product = products[row.productId] ?: return@mapNotNull null
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
    }
}
