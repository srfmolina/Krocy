package com.srfmolina.krocy.ui.di

import com.srfmolina.krocy.domain.usecase.stock.ObserveStockUseCase
import com.srfmolina.krocy.ui.AppViewModel
import com.srfmolina.krocy.ui.presentation.feature.stock.StockViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val uiModule = module {
    singleOf(::ObserveStockUseCase)

    viewModelOf(::AppViewModel)
    viewModelOf(::StockViewModel)
}