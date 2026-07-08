package com.srfmolina.krocy.data.datasource.remote.generic

import org.openapitools.client.apis.GenericEntityInteractionsApi
import org.openapitools.client.models.ExposedEntity
import org.openapitools.client.models.ObjectsEntityGet200ResponseInner

internal class GenericEntityDataSourceImpl(private val api: GenericEntityInteractionsApi): GenericEntityDataSource {
    override suspend fun getQuantityUnits(): Result<List<ObjectsEntityGet200ResponseInner>> = runCatching {
        api.objectsEntityGet(entity = ExposedEntity.quantity_units).body()
    }

    override suspend fun getLocations(): Result<List<ObjectsEntityGet200ResponseInner>> = runCatching {
        api.objectsEntityGet(entity = ExposedEntity.locations).body()
    }

    override suspend fun getProductGroups(): Result<List<ObjectsEntityGet200ResponseInner>> = runCatching {
        api.objectsEntityGet(entity = ExposedEntity.product_groups).body()
    }

    override suspend fun createProduct(body: ObjectsEntityGet200ResponseInner): Result<Int> = runCatching {
        api.objectsEntityPost(
            entity = ExposedEntity.products,
            objectsEntityGet200ResponseInner = body
        ).body().createdObjectId ?: error("Grocy did not return a created object id")
    }
}
