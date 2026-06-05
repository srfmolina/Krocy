package com.srfmolina.krocy.data.repository.impl

import com.srfmolina.krocy.data.datasource.remote.generic.GenericEntityDataSource
import com.srfmolina.krocy.data.mapper.toCreateDto
import com.srfmolina.krocy.domain.model.product.NewProduct
import com.srfmolina.krocy.domain.repository.ProductRepository

internal class ProductRepositoryImpl(
    private val genericEntityDataSource: GenericEntityDataSource
) : ProductRepository {

    override suspend fun createProduct(product: NewProduct): Int =
        genericEntityDataSource.createProduct(product.toCreateDto()).getOrThrow()
}
