package com.srfmolina.krocy.domain.usecase.stock

import com.srfmolina.krocy.domain.model.stock.NewPurchase
import com.srfmolina.krocy.domain.repository.StockRepository
import com.srfmolina.krocy.domain.usecase.base.ResultUseCase

class PurchaseStockUseCase(
    private val repo: StockRepository
) : ResultUseCase<NewPurchase, Unit>() {

    override suspend fun execute(params: NewPurchase) = repo.purchase(params)
}
