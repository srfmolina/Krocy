package com.srfmolina.krocy.domain.repository

import com.srfmolina.krocy.domain.model.product.NewProduct
import com.srfmolina.krocy.domain.model.product.NewQuConversion

interface ProductRepository {
    /** Creates a new product and returns its generated id. */
    suspend fun createProduct(product: NewProduct): Int

    /**
     * Ensures a product-specific quantity-unit conversion exists with the given factor,
     * creating or updating as needed, and returns the row id.
     */
    suspend fun createQuConversion(conversion: NewQuConversion): Int
}
