package com.srfmolina.krocy.data.datasource.remote.shoppinglist

import org.openapitools.client.apis.GenericEntityInteractionsApi
import org.openapitools.client.apis.StockApi
import org.openapitools.client.models.ExposedEntity
import org.openapitools.client.models.ObjectsEntityGet200ResponseInner
import org.openapitools.client.models.StockShoppinglistAddMissingProductsPostRequest
import org.openapitools.client.models.StockShoppinglistAddProductPostRequest

internal class ShoppingListDataSourceImpl(
    private val stockApi: StockApi,
    private val genericEntityApi: GenericEntityInteractionsApi,
) : ShoppingListDataSource {

    override suspend fun getShoppingLists(): Result<List<ObjectsEntityGet200ResponseInner>> = runCatching {
        genericEntityApi.objectsEntityGet(entity = ExposedEntity.shopping_lists).body()
    }

    override suspend fun createShoppingList(name: String): Result<Int> = runCatching {
        genericEntityApi.objectsEntityPost(
            entity = ExposedEntity.shopping_lists,
            objectsEntityGet200ResponseInner = ObjectsEntityGet200ResponseInner(name = name)
        ).body().createdObjectId ?: error("Grocy did not return a created object id")
    }

    override suspend fun getItems(): Result<List<ObjectsEntityGet200ResponseInner>> = runCatching {
        genericEntityApi.objectsEntityGet(entity = ExposedEntity.shopping_list).body()
    }

    override suspend fun setDone(itemId: Int, done: Boolean): Result<Unit> = runCatching {
        // Partial body: encodeDefaults is off, so only `done` is serialized (see SPEC-DEVIATIONS.md #7).
        genericEntityApi.objectsEntityObjectIdPut(
            entity = ExposedEntity.shopping_list,
            objectId = itemId,
            objectsEntityGet200ResponseInner = ObjectsEntityGet200ResponseInner(done = if (done) 1 else 0)
        ).body()
    }

    override suspend fun addMissingProducts(listId: Int): Result<Unit> = runCatching {
        stockApi.stockShoppinglistAddMissingProductsPost(
            StockShoppinglistAddMissingProductsPostRequest(listId = listId)
        ).body()
    }

    override suspend fun addProduct(listId: Int, productId: Int, amount: Double): Result<Unit> = runCatching {
        stockApi.stockShoppinglistAddProductPost(
            StockShoppinglistAddProductPostRequest(
                productId = productId,
                listId = listId,
                productAmount = amount,
            )
        ).body()
    }
}
