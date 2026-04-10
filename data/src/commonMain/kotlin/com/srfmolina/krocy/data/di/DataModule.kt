package com.srfmolina.krocy.data.di

import com.srfmolina.krocy.data.api.stock.createStockApi
import com.srfmolina.krocy.data.datasource.remote.createHttpClient
import com.srfmolina.krocy.data.datasource.remote.stock.StockDataSourceImpl
import com.srfmolina.krocy.data.db.KrocyDatabase
import com.srfmolina.krocy.data.db.createDatabase
import com.srfmolina.krocy.data.db.dao.KrocyItemDao
import com.srfmolina.krocy.data.repository.KrocyItemRepositoryImpl
import com.srfmolina.krocy.domain.repository.KrocyItemRepository
import org.koin.dsl.module

val dataModule = module {

    // La baseUrl vendrá de un parámetro o configuración — por ahora como propiedad
    val baseUrl = "https://en.demo.grocy.info/api"

    single<KrocyDatabase> { createDatabase() }
    single<KrocyItemDao>  { get<KrocyDatabase>().krocyItemDao() }
    single<KrocyItemRepository> { KrocyItemRepositoryImpl(get()) }
    single { StockDataSourceImpl(get()) }
    single { createHttpClient() }
    single { createStockApi(get(), baseUrl) }
}