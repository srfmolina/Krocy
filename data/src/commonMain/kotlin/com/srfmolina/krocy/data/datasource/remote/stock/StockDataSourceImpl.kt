package com.srfmolina.krocy.data.datasource.remote.stock

import org.openapitools.client.apis.StockApi
import org.openapitools.client.models.CurrentStockResponse

internal class StockDataSourceImpl(private val api: StockApi) : StockDataSource {
    override suspend fun getStock(): Result<List<CurrentStockResponse>> = runCatching {
        api.stockGet().body()
    }
}
