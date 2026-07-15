package com.srfmolina.krocy.domain.repository

import com.srfmolina.krocy.domain.model.shoppinglist.ShoppingListEntry
import kotlinx.coroutines.flow.Flow

interface ShoppingListRepository {
    fun getShoppingList(): Flow<List<ShoppingListEntry>>

    suspend fun setDone(entryId: Int, done: Boolean)

    suspend fun addProduct(productId: Int, amount: Double)

    suspend fun forceRefresh()
}
