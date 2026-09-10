package com.srfmolina.krocy.domain.usecase.stock

import com.srfmolina.krocy.domain.repository.StockRepository
import com.srfmolina.krocy.domain.usecase.base.ResultUseCaseNoParams

class LoadStockUseCase(
    private val repo: StockRepository
) : ResultUseCaseNoParams<Unit>() {
    override suspend fun execute() = repo.ensureLoaded()
}
