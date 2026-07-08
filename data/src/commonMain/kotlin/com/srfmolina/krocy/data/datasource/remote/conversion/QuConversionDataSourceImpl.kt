package com.srfmolina.krocy.data.datasource.remote.conversion

import org.openapitools.client.apis.QuantityUnitConversionsApi
import org.openapitools.client.models.QuantityUnitConversion

internal class QuConversionDataSourceImpl(
    private val api: QuantityUnitConversionsApi,
) : QuConversionDataSource {

    override suspend fun getQuConversions(): Result<List<QuantityUnitConversion>> = runCatching {
        api.objectsQuantityUnitConversionsGet().body()
    }

    override suspend fun getQuConversionsForProduct(productId: Int): Result<List<QuantityUnitConversion>> =
        runCatching {
            api.objectsQuantityUnitConversionsGet(query = listOf("product_id=$productId")).body()
        }

    override suspend fun createQuConversion(body: QuantityUnitConversion): Result<Int> = runCatching {
        api.objectsQuantityUnitConversionsPost(body).body().createdObjectId
            ?: error("Grocy did not return a created object id")
    }

    override suspend fun updateQuConversion(id: Int, body: QuantityUnitConversion): Result<Unit> =
        runCatching {
            val response = api.objectsQuantityUnitConversionsObjectIdPut(id, body)
            // PUT answers 204 with an empty body, so success must be checked via status.
            check(response.success) { "Grocy rejected the conversion update (HTTP ${response.status})" }
        }
}
