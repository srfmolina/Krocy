package com.srfmolina.krocy.domain.usecase.masterdata

import com.srfmolina.krocy.domain.model.masterdata.ShoppingLocation
import com.srfmolina.krocy.domain.repository.MasterRepository
import com.srfmolina.krocy.domain.usecase.base.ResultUseCaseNoParams

class GetShoppingLocationsUseCase(
    private val repo: MasterRepository
) : ResultUseCaseNoParams<List<ShoppingLocation>>() {

    override suspend fun execute() = repo.getShoppingLocations()
}
