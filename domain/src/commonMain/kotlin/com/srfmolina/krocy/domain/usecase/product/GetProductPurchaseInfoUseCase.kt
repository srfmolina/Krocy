package com.srfmolina.krocy.domain.usecase.product

import com.srfmolina.krocy.domain.model.product.ProductPurchaseInfo
import com.srfmolina.krocy.domain.repository.ProductRepository
import com.srfmolina.krocy.domain.usecase.base.ResultUseCase

class GetProductPurchaseInfoUseCase(
    private val repo: ProductRepository
) : ResultUseCase<Int, ProductPurchaseInfo>() {

    override suspend fun execute(params: Int) = repo.getProductPurchaseInfo(params)
}
