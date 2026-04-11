package com.srfmolina.krocy.data.datasource.remote.stock

import com.srfmolina.krocy.data.datasource.remote.stock.model.StockResponseDTO

internal interface StockDataSource {
    suspend fun getStock(): Result<List<StockResponseDTO>>
}