package com.srfmolina.krocy.domain.usecase.stock

import com.srfmolina.krocy.domain.model.stock.StockItem
import com.srfmolina.krocy.domain.usecase.base.ResultFlowUseCaseNoParams
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ObserveStockUseCase(
//    private val repo: SettingsRepository
) : ResultFlowUseCaseNoParams<List<StockItem>>() {

    override fun execute(): Flow<List<StockItem>> = flow{}
//        repo.observeSettings()
}