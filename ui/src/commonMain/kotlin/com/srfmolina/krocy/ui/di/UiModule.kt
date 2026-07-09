package com.srfmolina.krocy.ui.di

import com.srfmolina.krocy.domain.usecase.masterdata.GetLocationsUseCase
import com.srfmolina.krocy.domain.usecase.masterdata.GetProductGroupsUseCase
import com.srfmolina.krocy.domain.usecase.masterdata.GetQuConversionsUseCase
import com.srfmolina.krocy.domain.usecase.masterdata.GetQuantityUnitsUseCase
import com.srfmolina.krocy.domain.usecase.masterdata.GetShoppingLocationsUseCase
import com.srfmolina.krocy.domain.usecase.product.CreateProductUseCase
import com.srfmolina.krocy.domain.usecase.product.CreateQuConversionUseCase
import com.srfmolina.krocy.domain.usecase.product.GetProductPurchaseInfoUseCase
import com.srfmolina.krocy.domain.usecase.product.GetProductsUseCase
import com.srfmolina.krocy.domain.usecase.stock.AddStockUseCase
import com.srfmolina.krocy.domain.usecase.stock.ConsumeStockUseCase
import com.srfmolina.krocy.domain.usecase.stock.ObserveStockUseCase
import com.srfmolina.krocy.domain.usecase.stock.OpenStockUseCase
import com.srfmolina.krocy.domain.usecase.stock.PurchaseStockUseCase
import com.srfmolina.krocy.domain.usecase.stock.RefreshStockUseCase
import com.srfmolina.krocy.ui.AppViewModel
import com.srfmolina.krocy.ui.presentation.feature.creation.CreateProductViewModel
import com.srfmolina.krocy.ui.presentation.feature.purchase.PurchaseViewModel
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
    singleOf(::CreateQuConversionUseCase)
    singleOf(::GetQuantityUnitsUseCase)
    singleOf(::GetLocationsUseCase)
    singleOf(::GetProductGroupsUseCase)
    singleOf(::GetQuConversionsUseCase)
    singleOf(::PurchaseStockUseCase)
    singleOf(::GetProductsUseCase)
    singleOf(::GetProductPurchaseInfoUseCase)
    singleOf(::GetShoppingLocationsUseCase)

    viewModelOf(::AppViewModel)
    viewModelOf(::StockViewModel)
    viewModelOf(::CreateProductViewModel)
    viewModelOf(::PurchaseViewModel)
}