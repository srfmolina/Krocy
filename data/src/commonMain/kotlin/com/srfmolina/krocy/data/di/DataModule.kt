package com.srfmolina.krocy.data.di

import com.srfmolina.krocy.data.config.CredentialCipher
import com.srfmolina.krocy.data.config.HassSessionStore
import com.srfmolina.krocy.data.config.createCredentialCipher
import com.srfmolina.krocy.data.config.createServerConfigDataStore
import com.srfmolina.krocy.data.datasource.local.example.KrocyItemDataSource
import com.srfmolina.krocy.data.datasource.local.example.KrocyItemDataSourceImpl
import com.srfmolina.krocy.data.db.KrocyDatabase
import com.srfmolina.krocy.data.db.createDatabase
import com.srfmolina.krocy.data.db.dao.KrocyItemDao
import com.srfmolina.krocy.data.network.HassSessionSource
import com.srfmolina.krocy.data.network.KtorHassSessionSource
import com.srfmolina.krocy.data.network.buildImageHttpClient
import com.srfmolina.krocy.data.qr.QrDecoder
import com.srfmolina.krocy.data.qr.createQrDecoder
import com.srfmolina.krocy.data.repository.impl.KrocyItemRepositoryImpl
import com.srfmolina.krocy.data.repository.impl.LoginRepositoryImpl
import com.srfmolina.krocy.data.repository.impl.QrScannerRepositoryImpl
import com.srfmolina.krocy.data.repository.impl.ServerConfigRepositoryImpl
import com.srfmolina.krocy.data.session.SessionManagerImpl
import com.srfmolina.krocy.domain.repository.KrocyItemRepository
import com.srfmolina.krocy.domain.repository.LoginRepository
import com.srfmolina.krocy.domain.repository.QrScannerRepository
import com.srfmolina.krocy.domain.repository.ServerConfigRepository
import com.srfmolina.krocy.domain.session.SessionManager
import org.koin.core.qualifier.named
import org.koin.dsl.binds
import org.koin.dsl.module

val dataModule = module {

    // Local (server-independent)
    single<KrocyDatabase> { createDatabase() }
    single<KrocyItemDao> { get<KrocyDatabase>().krocyItemDao() }
    single<KrocyItemDataSource> { KrocyItemDataSourceImpl(get()) }
    single<KrocyItemRepository> { KrocyItemRepositoryImpl(get()) }

    // Server configuration & login
    single { createServerConfigDataStore() }
    single<CredentialCipher> { createCredentialCipher() }
    single { ServerConfigRepositoryImpl(get(), get()) } binds
        arrayOf(ServerConfigRepository::class, HassSessionStore::class)
    single<HassSessionSource> { KtorHassSessionSource() }
    single<LoginRepository> { LoginRepositoryImpl(get(), get()) }
    single<SessionManager> { SessionManagerImpl() }

    // QR scanning lives in the root module, not the session module: the user scans while
    // logged out, before any session exists.
    single<QrDecoder> { createQrDecoder() }
    single<QrScannerRepository> { QrScannerRepositoryImpl(get()) }

    // Authenticated client for Coil image loading (survives login/logout)
    single(named("imageHttpClient")) { buildImageHttpClient(get(), get()) }
}
