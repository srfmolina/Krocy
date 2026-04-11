package com.srfmolina.krocy.data.datasource.remote.stock

import com.srfmolina.krocy.data.api.stock.StockApi
import com.srfmolina.krocy.data.datasource.remote.stock.model.StockResponseDTO

internal class StockDataSourceImpl(private val api: StockApi): StockDataSource {
    override suspend fun getStock(): Result<List<StockResponseDTO>> = runCatching {
        api.getStock()
    }
}