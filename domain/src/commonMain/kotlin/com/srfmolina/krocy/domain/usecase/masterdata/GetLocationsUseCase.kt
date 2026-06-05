package com.srfmolina.krocy.domain.usecase.masterdata

import com.srfmolina.krocy.domain.model.masterdata.Location
import com.srfmolina.krocy.domain.repository.MasterRepository
import com.srfmolina.krocy.domain.usecase.base.ResultUseCaseNoParams

class GetLocationsUseCase(
    private val repo: MasterRepository
) : ResultUseCaseNoParams<List<Location>>() {

    override suspend fun execute() = repo.getLocations()
}
