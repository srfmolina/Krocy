package com.srfmolina.krocy.data.datasource.remote.shoppinglist

import org.openapitools.client.models.CurrentVolatilStockResponseMissingProductsInner
import org.openapitools.client.models.ObjectsEntityGet200ResponseInner

internal interface ShoppingListDataSource {
    suspend fun getShoppingLists(): Result<List<ObjectsEntityGet200ResponseInner>>

    /** Creates a shopping list and returns its generated id. */
    suspend fun createShoppingList(name: String): Result<Int>

    /** Returns the rows of every shopping list; callers filter by `shopping_list_id`. */
    suspend fun getItems(): Result<List<ObjectsEntityGet200ResponseInner>>

    /** Products currently below their min. stock amount; `amountMissing` is in stock units. */
    suspend fun getMissingProducts(): Result<List<CurrentVolatilStockResponseMissingProductsInner>>

    suspend fun createItem(
        listId: Int,
        productId: Int,
        amount: Double,
        quId: Int?,
        note: String?,
    ): Result<Unit>

    /**
     * Partial update; null [quId]/[note]/[done] leave the row's current values untouched.
     * Note: because null is omitted from the body, a note can only be *cleared* by passing
     * an explicit empty string.
     */
    suspend fun updateItem(
        itemId: Int,
        amount: Double,
        quId: Int?,
        note: String? = null,
        done: Boolean? = null,
    ): Result<Unit>

    suspend fun setDone(itemId: Int, done: Boolean): Result<Unit>

    suspend fun deleteItem(itemId: Int): Result<Unit>
}
