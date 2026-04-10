package com.srfmolina.krocy.data.api.stock

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient

fun createStockApi(httpClient: HttpClient, baseUrl: String): StockApi {
    return Ktorfit.Builder()
        .baseUrl(baseUrl)
        .httpClient(httpClient)
        .build()
        .createStockApi()
}