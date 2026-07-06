package com.srfmolina.krocy.data.datasource.remote.conversion

internal interface QuConversionDataSource {
    suspend fun getQuConversions(): Result<List<QuConversionDto>>

    suspend fun getQuConversionsForProduct(productId: Int): Result<List<QuConversionDto>>

    /** Creates a conversion object and returns its generated id. */
    suspend fun createQuConversion(body: QuConversionDto): Result<Int>

    suspend fun updateQuConversion(id: Int, body: QuConversionDto): Result<Unit>
}
