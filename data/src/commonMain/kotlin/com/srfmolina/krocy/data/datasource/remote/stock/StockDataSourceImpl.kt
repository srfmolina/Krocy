package com.srfmolina.krocy.data.datasource.remote.stock

import org.openapitools.client.apis.StockApi
import org.openapitools.client.models.CurrentStockResponse
import org.openapitools.client.models.StockLogEntry
import org.openapitools.client.models.StockProductsProductIdConsumePostRequest

internal class StockDataSourceImpl(private val api: StockApi): StockDataSource {
    override suspend fun getStock(): Result<List<CurrentStockResponse>> = runCatching {
        api.stockGet().body()
    }

    override suspend fun consume(productId: Int): Result<List<StockLogEntry>> = runCatching {
        api.stockProductsProductIdConsumePost(
            productId = productId,
            stockProductsProductIdConsumePostRequest = StockProductsProductIdConsumePostRequest(
                amount = 1.0
            )
        ).body()
    }

}
