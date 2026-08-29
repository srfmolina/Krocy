package com.srfmolina.krocy.data.di

import com.srfmolina.krocy.data.config.HassSessionStore
import com.srfmolina.krocy.data.datasource.remote.conversion.QuConversionDataSource
import com.srfmolina.krocy.data.datasource.remote.conversion.QuConversionDataSourceImpl
import com.srfmolina.krocy.data.datasource.remote.generic.GenericEntityDataSource
import com.srfmolina.krocy.data.datasource.remote.generic.GenericEntityDataSourceImpl
import com.srfmolina.krocy.data.datasource.remote.shoppinglist.ShoppingListDataSource
import com.srfmolina.krocy.data.datasource.remote.shoppinglist.ShoppingListDataSourceImpl
import com.srfmolina.krocy.data.datasource.remote.stock.StockDataSource
import com.srfmolina.krocy.data.datasource.remote.stock.StockDataSourceImpl
import com.srfmolina.krocy.data.network.HassSessionSource
import com.srfmolina.krocy.data.network.buildSessionHttpClient
import com.srfmolina.krocy.data.repository.impl.MasterRepositoryImpl
import com.srfmolina.krocy.data.repository.impl.ProductRepositoryImpl
import com.srfmolina.krocy.data.repository.impl.ShoppingListRepositoryImpl
import com.srfmolina.krocy.data.repository.impl.StockRepositoryImpl
import com.srfmolina.krocy.domain.model.server.ServerConfig
import com.srfmolina.krocy.domain.repository.MasterRepository
import com.srfmolina.krocy.domain.repository.ProductRepository
import com.srfmolina.krocy.domain.repository.ShoppingListRepository
import com.srfmolina.krocy.domain.repository.StockRepository
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.dsl.module
import org.openapitools.client.apis.GenericEntityInteractionsApi
import org.openapitools.client.apis.QuantityUnitConversionsApi
import org.openapitools.client.apis.StockApi

/**
 * Server-bound definitions, loaded by SessionManagerImpl once a ServerConfig is
 * known and unloaded on logout. This realizes the "session scope" of the design.
 */
internal fun sessionModule(config: ServerConfig, onSessionExpired: () -> Unit): Module = module {

    single<HttpClient> {
        buildSessionHttpClient(
            config = config,
            sessionStore = get<HassSessionStore>(),
            sessionSource = get<HassSessionSource>(),
            onSessionExpired = onSessionExpired
        )
    }

    single { StockApi(config.apiBaseUrl, get<HttpClient>()) }
    single { GenericEntityInteractionsApi(config.apiBaseUrl, get<HttpClient>()) }
    single { QuantityUnitConversionsApi(config.apiBaseUrl, get<HttpClient>()) }

    single<StockDataSource> { StockDataSourceImpl(get()) }
    single<ShoppingListDataSource> { ShoppingListDataSourceImpl(get(), get()) }
    single<GenericEntityDataSource> { GenericEntityDataSourceImpl(get()) }
    single<QuConversionDataSource> { QuConversionDataSourceImpl(get()) }

    single<StockRepository> { StockRepositoryImpl(get(), get(), config.apiBaseUrl) }
    single<ShoppingListRepository> { ShoppingListRepositoryImpl(get(), get()) }
    single<MasterRepository> { MasterRepositoryImpl(get(), get()) }
    single<ProductRepository> { ProductRepositoryImpl(get(), get(), get()) }
}
