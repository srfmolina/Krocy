package com.srfmolina.krocy.ui.di

import com.srfmolina.krocy.domain.usecase.masterdata.GetLocationsUseCase
import com.srfmolina.krocy.domain.usecase.masterdata.GetProductGroupsUseCase
import com.srfmolina.krocy.domain.usecase.masterdata.GetQuantityUnitsUseCase
import com.srfmolina.krocy.domain.usecase.product.CreateProductUseCase
import com.srfmolina.krocy.domain.usecase.stock.AddStockUseCase
import com.srfmolina.krocy.domain.usecase.stock.ConsumeStockUseCase
import com.srfmolina.krocy.domain.usecase.stock.ObserveStockUseCase
import com.srfmolina.krocy.domain.usecase.stock.OpenStockUseCase
import com.srfmolina.krocy.domain.usecase.stock.RefreshStockUseCase
import com.srfmolina.krocy.ui.AppViewModel
import com.srfmolina.krocy.ui.presentation.common.selector.group.ProductGroupSelectorViewModel
import com.srfmolina.krocy.ui.presentation.common.selector.location.LocationSelectorViewModel
import com.srfmolina.krocy.ui.presentation.common.selector.stock.unit.StockUnitSelectorViewModel
import com.srfmolina.krocy.ui.presentation.feature.creation.CreateProductViewModel
import com.srfmolina.krocy.ui.presentation.feature.stock.StockViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val uiModule = module {
    singleOf(::ObserveStockUseCase)
    singleOf(::ConsumeStockUseCase)
    singleOf(::AddStockUseCase)
    singleOf(::OpenStockUseCase)
    singleOf(::RefreshStockUseCase)
    singleOf(::CreateProductUseCase)
    singleOf(::GetQuantityUnitsUseCase)
    singleOf(::GetLocationsUseCase)
    singleOf(::GetProductGroupsUseCase)

    viewModelOf(::AppViewModel)
    viewModelOf(::StockViewModel)
    viewModelOf(::CreateProductViewModel)
    viewModelOf(::StockUnitSelectorViewModel)
    viewModelOf(::LocationSelectorViewModel)
    viewModelOf(::ProductGroupSelectorViewModel)
}