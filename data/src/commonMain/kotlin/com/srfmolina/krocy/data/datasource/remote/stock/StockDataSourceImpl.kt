package com.srfmolina.krocy.data.datasource.remote.stock

import org.openapitools.client.apis.StockApi
import org.openapitools.client.models.CurrentStockResponse
import org.openapitools.client.models.StockLogEntry
import org.openapitools.client.models.StockProductsProductIdAddPostRequest
import org.openapitools.client.models.StockProductsProductIdConsumePostRequest
import org.openapitools.client.models.StockProductsProductIdOpenPostRequest

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

}
