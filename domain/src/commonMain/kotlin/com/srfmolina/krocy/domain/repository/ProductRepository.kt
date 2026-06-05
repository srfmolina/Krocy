package com.srfmolina.krocy.domain.repository

import com.srfmolina.krocy.domain.model.product.NewProduct

interface ProductRepository {
    /** Creates a new product and returns its generated id. */
    suspend fun createProduct(product: NewProduct): Int
}
