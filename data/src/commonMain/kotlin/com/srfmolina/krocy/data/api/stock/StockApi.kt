package com.srfmolina.krocy.data.api.stock

import com.srfmolina.krocy.data.datasource.remote.stock.model.StockResponseDTO
import de.jensklingenberg.ktorfit.http.GET

interface StockApi {

    @GET("/stock")
    suspend fun getStock(): List<StockResponseDTO>
}