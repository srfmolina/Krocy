package com.srfmolina.krocy.data.datasource.remote.generic

import org.openapitools.client.models.ObjectsEntityGet200ResponseInner

internal interface GenericEntityDataSource {
    suspend fun getQuantityUnits(): Result<List<ObjectsEntityGet200ResponseInner>>

    suspend fun getLocations(): Result<List<ObjectsEntityGet200ResponseInner>>

    suspend fun getProductGroups(): Result<List<ObjectsEntityGet200ResponseInner>>

    /** Creates a product object and returns its generated id. */
    suspend fun createProduct(body: ObjectsEntityGet200ResponseInner): Result<Int>
}
