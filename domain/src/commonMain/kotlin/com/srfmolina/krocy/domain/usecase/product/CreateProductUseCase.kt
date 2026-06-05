package com.srfmolina.krocy.domain.usecase.product

import com.srfmolina.krocy.domain.model.product.NewProduct
import com.srfmolina.krocy.domain.repository.ProductRepository
import com.srfmolina.krocy.domain.usecase.base.ResultUseCase

class CreateProductUseCase(
    private val repo: ProductRepository
) : ResultUseCase<NewProduct, Int>() {

    override suspend fun execute(params: NewProduct) = repo.createProduct(params)
}
