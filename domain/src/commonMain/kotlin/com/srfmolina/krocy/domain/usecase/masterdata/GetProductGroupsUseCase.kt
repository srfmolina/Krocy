package com.srfmolina.krocy.domain.usecase.masterdata

import com.srfmolina.krocy.domain.model.masterdata.ProductGroup
import com.srfmolina.krocy.domain.repository.MasterRepository
import com.srfmolina.krocy.domain.usecase.base.ResultUseCaseNoParams

class GetProductGroupsUseCase(
    private val repo: MasterRepository
) : ResultUseCaseNoParams<List<ProductGroup>>() {

    override suspend fun execute() = repo.getProductGroups()
}
