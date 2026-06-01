package com.srfmolina.krocy.data.datasource.remote.generic

import org.openapitools.client.models.ObjectsEntityGet200ResponseInner

internal interface GenericEntityDataSource {
    suspend fun getQuantityUnits(): Result<List<ObjectsEntityGet200ResponseInner>>
}