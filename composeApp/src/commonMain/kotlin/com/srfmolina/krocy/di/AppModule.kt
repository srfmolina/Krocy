package com.srfmolina.krocy.di

import com.srfmolina.krocy.AppViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

// Aquí irán todos tus ViewModels y use cases compartidos
val appModule = module {
    viewModelOf(::AppViewModel)
}