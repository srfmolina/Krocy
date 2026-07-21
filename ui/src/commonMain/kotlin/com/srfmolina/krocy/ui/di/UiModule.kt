package com.srfmolina.krocy.ui.di

import com.srfmolina.krocy.domain.usecase.login.CompleteLoginUseCase
import com.srfmolina.krocy.domain.usecase.login.GetServerConfigUseCase
import com.srfmolina.krocy.domain.usecase.login.LogoutUseCase
import com.srfmolina.krocy.domain.usecase.login.ObserveSessionExpiredUseCase
import com.srfmolina.krocy.domain.usecase.login.OpenSessionUseCase
import com.srfmolina.krocy.domain.usecase.login.ValidateServerUseCase
import com.srfmolina.krocy.domain.usecase.masterdata.GetLocationsUseCase
import com.srfmolina.krocy.domain.usecase.masterdata.GetProductGroupsUseCase
import com.srfmolina.krocy.domain.usecase.masterdata.GetQuConversionsUseCase
import com.srfmolina.krocy.domain.usecase.masterdata.GetQuantityUnitsUseCase
import com.srfmolina.krocy.domain.usecase.masterdata.GetShoppingLocationsUseCase
import com.srfmolina.krocy.domain.usecase.product.CreateProductUseCase
import com.srfmolina.krocy.domain.usecase.product.CreateQuConversionUseCase
import com.srfmolina.krocy.domain.usecase.product.GetProductPurchaseInfoUseCase
import com.srfmolina.krocy.domain.usecase.product.GetProductsUseCase
import com.srfmolina.krocy.domain.usecase.shoppinglist.AddToShoppingListUseCase
import com.srfmolina.krocy.domain.usecase.shoppinglist.ObserveShoppingListUseCase
import com.srfmolina.krocy.domain.usecase.shoppinglist.RefreshShoppingListUseCase
import com.srfmolina.krocy.domain.usecase.shoppinglist.SetEntryDoneUseCase
import com.srfmolina.krocy.domain.usecase.stock.AddStockUseCase
import com.srfmolina.krocy.domain.usecase.stock.ConsumeStockUseCase
import com.srfmolina.krocy.domain.usecase.stock.ObserveStockUseCase
import com.srfmolina.krocy.domain.usecase.stock.OpenStockUseCase
import com.srfmolina.krocy.domain.usecase.stock.PurchaseStockUseCase
import com.srfmolina.krocy.domain.usecase.stock.RefreshStockUseCase
import com.srfmolina.krocy.ui.AppViewModel
import com.srfmolina.krocy.ui.presentation.feature.creation.CreateProductViewModel
import com.srfmolina.krocy.ui.presentation.feature.login.LoginViewModel
import com.srfmolina.krocy.ui.presentation.feature.purchase.PurchaseViewModel
import com.srfmolina.krocy.ui.presentation.feature.shoppinglist.ShoppingListViewModel
import com.srfmolina.krocy.ui.presentation.feature.stock.StockViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val uiModule = module {
    factoryOf(::ObserveStockUseCase)
    factoryOf(::ConsumeStockUseCase)
    factoryOf(::AddStockUseCase)
    factoryOf(::OpenStockUseCase)
    factoryOf(::RefreshStockUseCase)
    factoryOf(::CreateProductUseCase)
    factoryOf(::CreateQuConversionUseCase)
    factoryOf(::GetQuantityUnitsUseCase)
    factoryOf(::GetLocationsUseCase)
    factoryOf(::GetProductGroupsUseCase)
    factoryOf(::GetQuConversionsUseCase)
    factoryOf(::PurchaseStockUseCase)
    factoryOf(::GetProductsUseCase)
    factoryOf(::GetProductPurchaseInfoUseCase)
    factoryOf(::GetShoppingLocationsUseCase)
    factoryOf(::ObserveShoppingListUseCase)
    factoryOf(::RefreshShoppingListUseCase)
    factoryOf(::SetEntryDoneUseCase)
    factoryOf(::AddToShoppingListUseCase)
    factoryOf(::ValidateServerUseCase)
    factoryOf(::CompleteLoginUseCase)
    factoryOf(::OpenSessionUseCase)
    factoryOf(::GetServerConfigUseCase)
    factoryOf(::LogoutUseCase)
    factoryOf(::ObserveSessionExpiredUseCase)

    viewModelOf(::AppViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::StockViewModel)
    viewModelOf(::CreateProductViewModel)
    viewModelOf(::PurchaseViewModel)
    viewModelOf(::ShoppingListViewModel)
}