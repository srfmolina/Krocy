package com.srfmolina.krocy.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module

fun initKoin(
    additionalModules: List<Module> = emptyList(),
    appDeclaration: KoinApplication.() -> Unit = {}
) {
    startKoin {
        appDeclaration()
        modules(appModule + additionalModules)
    }
}