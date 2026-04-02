package com.srfmolina.data.di

import com.srfmolina.data.db.KrocyDatabase
import com.srfmolina.data.db.createDatabase
import com.srfmolina.data.db.dao.KrocyItemDao
import com.srfmolina.data.repository.KrocyItemRepositoryImpl
import com.srfmolina.domain.repository.KrocyItemRepository
import org.koin.dsl.module

val dataModule = module {
    single<KrocyDatabase> { createDatabase() }
    single<KrocyItemDao>  { get<KrocyDatabase>().krocyItemDao() }
    single<KrocyItemRepository> { KrocyItemRepositoryImpl(get()) }
}