package com.srfmolina.krocy.data.datasource.remote.shoppinglist

import org.openapitools.client.models.ObjectsEntityGet200ResponseInner

internal interface ShoppingListDataSource {
    suspend fun getShoppingLists(): Result<List<ObjectsEntityGet200ResponseInner>>

    /** Creates a shopping list and returns its generated id. */
    suspend fun createShoppingList(name: String): Result<Int>

    /** Returns the rows of every shopping list; callers filter by `shopping_list_id`. */
    suspend fun getItems(): Result<List<ObjectsEntityGet200ResponseInner>>

    suspend fun setDone(itemId: Int, done: Boolean): Result<Unit>

    /** Adds products below their min. stock amount to the given list (idempotent). */
    suspend fun addMissingProducts(listId: Int): Result<Unit>

    suspend fun addProduct(listId: Int, productId: Int, amount: Double): Result<Unit>
}
