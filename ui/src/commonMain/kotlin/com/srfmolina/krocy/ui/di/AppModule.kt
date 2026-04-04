package com.srfmolina.krocy.ui.di

import com.srfmolina.krocy.ui.AppViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val uiModule = module {
    viewModelOf(::AppViewModel)
}