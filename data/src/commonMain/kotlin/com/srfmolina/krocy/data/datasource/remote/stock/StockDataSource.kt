package com.srfmolina.krocy.data.datasource.remote.stock

import org.openapitools.client.models.CurrentStockResponse

internal interface StockDataSource {
    suspend fun getStock(): Result<List<CurrentStockResponse>>
}
