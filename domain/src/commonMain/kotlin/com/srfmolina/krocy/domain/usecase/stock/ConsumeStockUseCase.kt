package com.srfmolina.krocy.domain.usecase.stock

import com.srfmolina.krocy.domain.repository.StockRepository
import com.srfmolina.krocy.domain.usecase.base.ResultUseCase
import com.srfmolina.krocy.domain.usecase.stock.model.BasicStockUCRequest

class ConsumeStockUseCase(
    private val repo: StockRepository
) : ResultUseCase<BasicStockUCRequest, Unit>() {

    override suspend fun execute(params: BasicStockUCRequest) = repo.consume(params.productId, params.amount)
}
