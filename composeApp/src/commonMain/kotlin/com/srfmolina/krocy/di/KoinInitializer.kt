package com.srfmolina.krocy.di

import com.srfmolina.krocy.data.di.dataModule
import com.srfmolina.krocy.ui.di.uiModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

fun initKoin(
    additionalModules: List<org.koin.core.module.Module> = emptyList(),
    appDeclaration: KoinApplication.() -> Unit = {}
) {
    startKoin {
        appDeclaration()
        modules(uiModule + additionalModules + dataModule)
    }
}