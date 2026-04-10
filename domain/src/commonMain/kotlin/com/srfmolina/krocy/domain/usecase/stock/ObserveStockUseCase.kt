package com.srfmolina.krocy.domain.usecase.stock

import com.srfmolina.krocy.domain.model.stock.StockItem
import com.srfmolina.krocy.domain.repository.StockRepository
import com.srfmolina.krocy.domain.usecase.base.ResultFlowUseCaseNoParams
import kotlinx.coroutines.flow.Flow

class ObserveStockUseCase(
    private val repo: StockRepository
) : ResultFlowUseCaseNoParams<List<StockItem>>() {

    override fun execute(): Flow<List<StockItem>> = repo.getStock()
}