package com.srfmolina.krocy.data.di

import com.srfmolina.krocy.data.db.KrocyDatabase
import com.srfmolina.krocy.data.db.createDatabase
import com.srfmolina.krocy.data.db.dao.KrocyItemDao
import com.srfmolina.krocy.data.repository.KrocyItemRepositoryImpl
import com.srfmolina.krocy.domain.repository.KrocyItemRepository
import org.koin.dsl.module

val dataModule = module {
    single<KrocyDatabase> { createDatabase() }
    single<KrocyItemDao>  { get<KrocyDatabase>().krocyItemDao() }
    single<KrocyItemRepository> { KrocyItemRepositoryImpl(get()) }
}