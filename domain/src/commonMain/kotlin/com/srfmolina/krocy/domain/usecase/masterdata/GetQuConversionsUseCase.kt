package com.srfmolina.krocy.domain.usecase.masterdata

import com.srfmolina.krocy.domain.model.masterdata.QuConversion
import com.srfmolina.krocy.domain.repository.MasterRepository
import com.srfmolina.krocy.domain.usecase.base.ResultUseCaseNoParams

class GetQuConversionsUseCase(
    private val repo: MasterRepository
) : ResultUseCaseNoParams<List<QuConversion>>() {

    override suspend fun execute() = repo.getQuConversions()
}
