package com.srfmolina.krocy.domain.usecase.product

import com.srfmolina.krocy.domain.model.product.ProductOption
import com.srfmolina.krocy.domain.repository.ProductRepository
import com.srfmolina.krocy.domain.usecase.base.ResultUseCaseNoParams

class GetProductsUseCase(
    private val repo: ProductRepository
) : ResultUseCaseNoParams<List<ProductOption>>() {

    override suspend fun execute() = repo.getProducts()
}
