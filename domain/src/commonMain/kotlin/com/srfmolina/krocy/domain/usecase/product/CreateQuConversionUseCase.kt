package com.srfmolina.krocy.domain.usecase.product

import com.srfmolina.krocy.domain.model.product.NewQuConversion
import com.srfmolina.krocy.domain.repository.ProductRepository
import com.srfmolina.krocy.domain.usecase.base.ResultUseCase

class CreateQuConversionUseCase(
    private val repo: ProductRepository
) : ResultUseCase<NewQuConversion, Int>() {

    override suspend fun execute(params: NewQuConversion) = repo.createQuConversion(params)
}
