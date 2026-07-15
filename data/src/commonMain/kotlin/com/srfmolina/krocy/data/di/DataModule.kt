package com.srfmolina.krocy.data.di

import com.srfmolina.krocy.data.datasource.local.example.KrocyItemDataSource
import com.srfmolina.krocy.data.datasource.local.example.KrocyItemDataSourceImpl
import com.srfmolina.krocy.data.datasource.remote.conversion.QuConversionDataSource
import com.srfmolina.krocy.data.datasource.remote.conversion.QuConversionDataSourceImpl
import com.srfmolina.krocy.data.datasource.remote.generic.GenericEntityDataSource
import com.srfmolina.krocy.data.datasource.remote.generic.GenericEntityDataSourceImpl
import com.srfmolina.krocy.data.datasource.remote.shoppinglist.ShoppingListDataSource
import com.srfmolina.krocy.data.datasource.remote.shoppinglist.ShoppingListDataSourceImpl
import com.srfmolina.krocy.data.datasource.remote.stock.StockDataSource
import com.srfmolina.krocy.data.datasource.remote.stock.StockDataSourceImpl
import com.srfmolina.krocy.data.db.KrocyDatabase
import com.srfmolina.krocy.data.db.createDatabase
import com.srfmolina.krocy.data.db.dao.KrocyItemDao
import com.srfmolina.krocy.data.repository.impl.KrocyItemRepositoryImpl
import com.srfmolina.krocy.data.repository.impl.MasterRepositoryImpl
import com.srfmolina.krocy.data.repository.impl.ProductRepositoryImpl
import com.srfmolina.krocy.data.repository.impl.ShoppingListRepositoryImpl
import com.srfmolina.krocy.data.repository.impl.StockRepositoryImpl
import com.srfmolina.krocy.domain.repository.KrocyItemRepository
import com.srfmolina.krocy.domain.repository.MasterRepository
import com.srfmolina.krocy.domain.repository.ProductRepository
import com.srfmolina.krocy.domain.repository.ShoppingListRepository
import com.srfmolina.krocy.domain.repository.StockRepository
import org.koin.dsl.module
import org.openapitools.client.apis.GenericEntityInteractionsApi
import org.openapitools.client.apis.QuantityUnitConversionsApi
import org.openapitools.client.apis.StockApi

val dataModule = module {

    // La baseUrl vendrá de un parámetro o configuración — por ahora como propiedad
    val baseUrl = "https://en.demo.grocy.info/api"

    single { StockApi(baseUrl = baseUrl) }
    single { GenericEntityInteractionsApi(baseUrl = baseUrl) }
    single { QuantityUnitConversionsApi(baseUrl = baseUrl) }

    single<KrocyDatabase> { createDatabase() }
    single<KrocyItemDao>  { get<KrocyDatabase>().krocyItemDao() }

    single<KrocyItemDataSource> { KrocyItemDataSourceImpl(get()) }
    single<StockDataSource> { StockDataSourceImpl(get()) }
    single<ShoppingListDataSource> { ShoppingListDataSourceImpl(get(), get()) }
    single<GenericEntityDataSource> { GenericEntityDataSourceImpl(get()) }
    single<QuConversionDataSource> { QuConversionDataSourceImpl(get()) }

    single<KrocyItemRepository> { KrocyItemRepositoryImpl(get()) }
    single<StockRepository> { StockRepositoryImpl(get(), get(), baseUrl) }
    single<ShoppingListRepository> { ShoppingListRepositoryImpl(get(), get()) }
    single<MasterRepository> { MasterRepositoryImpl(get(), get()) }
    single<ProductRepository> { ProductRepositoryImpl(get(), get(), get()) }

}
