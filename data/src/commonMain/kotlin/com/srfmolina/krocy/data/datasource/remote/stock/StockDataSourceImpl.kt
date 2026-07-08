package com.srfmolina.krocy.data.datasource.remote.stock

import kotlinx.datetime.LocalDate
import org.openapitools.client.apis.StockApi
import org.openapitools.client.models.CurrentStockResponse
import org.openapitools.client.models.ProductDetailsResponse
import org.openapitools.client.models.StockLogEntry
import org.openapitools.client.models.StockProductsProductIdAddPostRequest
import org.openapitools.client.models.StockProductsProductIdConsumePostRequest
import org.openapitools.client.models.StockProductsProductIdOpenPostRequest
import org.openapitools.client.models.StockTransactionType

internal class StockDataSourceImpl(private val api: StockApi): StockDataSource {
    override suspend fun getStock(): Result<List<CurrentStockResponse>> = runCatching {
        api.stockGet().body()
    }

    override suspend fun consume(productId: Int, amount: Double): Result<List<StockLogEntry>> = runCatching {
        api.stockProductsProductIdConsumePost(
            productId = productId,
            stockProductsProductIdConsumePostRequest = StockProductsProductIdConsumePostRequest(
                amount = amount
            )
        ).body()
    }

    override suspend fun open(productId: Int, amount: Double): Result<List<StockLogEntry>> = runCatching {
        api.stockProductsProductIdOpenPost(
            productId = productId,
            stockProductsProductIdOpenPostRequest = StockProductsProductIdOpenPostRequest(
                amount = amount
            )
        ).body()
    }

    override suspend fun add(productId: Int, amount: Double): Result<List<StockLogEntry>> = runCatching {
        api.stockProductsProductIdAddPost(
            productId = productId,
            stockProductsProductIdAddPostRequest = StockProductsProductIdAddPostRequest(
                amount = amount
            )
        ).body()
    }

    override suspend fun purchase(
        productId: Int,
        amount: Double,
        bestBeforeDate: LocalDate?,
        price: Double?,
        locationId: Int?,
        shoppingLocationId: Int?,
        note: String?,
    ): Result<List<StockLogEntry>> = runCatching {
        api.stockProductsProductIdAddPost(
            productId = productId,
            stockProductsProductIdAddPostRequest = StockProductsProductIdAddPostRequest(
                amount = amount,
                bestBeforeDate = bestBeforeDate,
                transactionType = StockTransactionType.purchase,
                price = price,
                locationId = locationId,
                shoppingLocationId = shoppingLocationId,
                note = note,
            )
        ).body()
    }

    override suspend fun getProductDetails(productId: Int): Result<ProductDetailsResponse> = runCatching {
        api.stockProductsProductIdGet(productId).body()
    }

}
