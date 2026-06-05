package com.srfmolina.krocy.domain.usecase.masterdata

import com.srfmolina.krocy.domain.model.masterdata.QuantityUnit
import com.srfmolina.krocy.domain.repository.MasterRepository
import com.srfmolina.krocy.domain.usecase.base.ResultUseCaseNoParams

class GetQuantityUnitsUseCase(
    private val repo: MasterRepository
) : ResultUseCaseNoParams<List<QuantityUnit>>() {

    override suspend fun execute() = repo.getQuantityUnits()
}
