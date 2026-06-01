package com.srfmolina.krocy.data.datasource.remote.generic

import org.openapitools.client.apis.GenericEntityInteractionsApi
import org.openapitools.client.models.ExposedEntity
import org.openapitools.client.models.ObjectsEntityGet200ResponseInner

internal class GenericEntityDataSourceImpl(private val api: GenericEntityInteractionsApi): GenericEntityDataSource {
    override suspend fun getQuantityUnits(): Result<List<ObjectsEntityGet200ResponseInner>> = runCatching {
        api.objectsEntityGet(entity = ExposedEntity.quantity_units).body()
    }
}