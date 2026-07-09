package com.srfmolina.krocy.data.datasource.remote.stock

import kotlinx.datetime.LocalDate
import org.openapitools.client.models.CurrentStockResponse
import org.openapitools.client.models.ProductDetailsResponse
import org.openapitools.client.models.StockLogEntry

internal interface StockDataSource {
    suspend fun getStock(): Result<List<CurrentStockResponse>>

    suspend fun consume(productId: Int, amount: Double): Result<List<StockLogEntry>>

    suspend fun open(productId: Int, amount: Double): Result<List<StockLogEntry>>

    suspend fun add(productId: Int, amount: Double): Result<List<StockLogEntry>>

    suspend fun purchase(
        productId: Int,
        amount: Double,
        bestBeforeDate: LocalDate?,
        price: Double?,
        locationId: Int?,
        shoppingLocationId: Int?,
        note: String?,
    ): Result<List<StockLogEntry>>

    suspend fun getProductDetails(productId: Int): Result<ProductDetailsResponse>
}
