package com.srfmolina.krocy.data.datasource.remote.conversion

import org.openapitools.client.models.QuantityUnitConversion

internal interface QuConversionDataSource {
    suspend fun getQuConversions(): Result<List<QuantityUnitConversion>>

    suspend fun getQuConversionsForProduct(productId: Int): Result<List<QuantityUnitConversion>>

    /** Creates a conversion object and returns its generated id. */
    suspend fun createQuConversion(body: QuantityUnitConversion): Result<Int>

    suspend fun updateQuConversion(id: Int, body: QuantityUnitConversion): Result<Unit>
}
