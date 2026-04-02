package com.srfmolina.krocy.di

import com.srfmolina.krocy.AppViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::AppViewModel)
}