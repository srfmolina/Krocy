package com.srfmolina.krocy.domain.repository

import com.srfmolina.krocy.domain.model.shoppinglist.ShoppingListEntry
import kotlinx.coroutines.flow.Flow

interface ShoppingListRepository {
    /**
     * The cached list. Emits once it has been loaded and on every later change; it never
     * fails and never completes, so a single collector outlives any number of failed loads.
     * Loading is explicit: [ensureLoaded] or [forceRefresh].
     */
    fun getShoppingList(): Flow<List<ShoppingListEntry>>

    /** Loads the list unless it already has been. Throws when the load fails. */
    suspend fun ensureLoaded()

    suspend fun setDone(entryId: Int, done: Boolean)

    suspend fun addProduct(productId: Int, amount: Double)

    suspend fun forceRefresh()
}
