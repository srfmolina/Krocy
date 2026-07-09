package com.srfmolina.krocy.data.datasource.remote.shoppinglist

import org.openapitools.client.apis.GenericEntityInteractionsApi
import org.openapitools.client.apis.StockApi
import org.openapitools.client.models.CurrentVolatilStockResponseMissingProductsInner
import org.openapitools.client.models.ExposedEntity
import org.openapitools.client.models.ObjectsEntityGet200ResponseInner

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

    override suspend fun getMissingProducts(): Result<List<CurrentVolatilStockResponseMissingProductsInner>> =
        runCatching {
            stockApi.stockVolatileGet().body().missingProducts.orEmpty()
        }

    override suspend fun createItem(
        listId: Int,
        productId: Int,
        amount: Double,
        quId: Int?,
        note: String?,
    ): Result<Unit> = runCatching {
        genericEntityApi.objectsEntityPost(
            entity = ExposedEntity.shopping_list,
            objectsEntityGet200ResponseInner = ObjectsEntityGet200ResponseInner(
                shoppingListId = listId,
                productId = productId,
                amount = amount,
                quId = quId,
                note = note,
            )
        ).body()
        Unit
    }

    override suspend fun updateItem(
        itemId: Int,
        amount: Double,
        quId: Int?,
        note: String?,
    ): Result<Unit> = runCatching {
        // Partial body: encodeDefaults is off, so unset fields are not serialized
        // and grocy leaves their columns untouched (see SPEC-DEVIATIONS.md #7).
        genericEntityApi.objectsEntityObjectIdPut(
            entity = ExposedEntity.shopping_list,
            objectId = itemId,
            objectsEntityGet200ResponseInner = ObjectsEntityGet200ResponseInner(
                amount = amount,
                quId = quId,
                note = note,
            )
        ).body()
    }

    override suspend fun setDone(itemId: Int, done: Boolean): Result<Unit> = runCatching {
        genericEntityApi.objectsEntityObjectIdPut(
            entity = ExposedEntity.shopping_list,
            objectId = itemId,
            objectsEntityGet200ResponseInner = ObjectsEntityGet200ResponseInner(done = if (done) 1 else 0)
        ).body()
    }

    override suspend fun deleteItem(itemId: Int): Result<Unit> = runCatching {
        genericEntityApi.objectsEntityObjectIdDelete(
            entity = ExposedEntity.shopping_list,
            objectId = itemId,
        ).body()
    }
}
