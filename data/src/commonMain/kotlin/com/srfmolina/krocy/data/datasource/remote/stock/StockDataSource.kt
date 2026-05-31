package com.srfmolina.krocy.data.datasource.remote.stock

import org.openapitools.client.models.CurrentStockResponse
import org.openapitools.client.models.StockLogEntry

internal interface StockDataSource {
    suspend fun getStock(): Result<List<CurrentStockResponse>>

    suspend fun consume(productId: Int): Result<List<StockLogEntry>>
}
