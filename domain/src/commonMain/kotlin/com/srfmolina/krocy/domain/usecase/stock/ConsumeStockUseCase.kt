package com.srfmolina.krocy.domain.usecase.stock

import com.srfmolina.krocy.domain.repository.StockRepository
import com.srfmolina.krocy.domain.usecase.base.ResultUseCase

class ConsumeStockUseCase(
    private val repo: StockRepository
) : ResultUseCase<Int, Unit>() {

    override suspend fun execute(params: Int) = repo.consume(params)
}
