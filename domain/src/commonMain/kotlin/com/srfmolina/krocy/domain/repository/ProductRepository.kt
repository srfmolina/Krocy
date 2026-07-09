package com.srfmolina.krocy.domain.repository

import com.srfmolina.krocy.domain.model.product.NewProduct
import com.srfmolina.krocy.domain.model.product.NewQuConversion
import com.srfmolina.krocy.domain.model.product.ProductOption
import com.srfmolina.krocy.domain.model.product.ProductPurchaseInfo

interface ProductRepository {
    /** Creates a new product and returns its generated id. */
    suspend fun createProduct(product: NewProduct): Int

    /**
     * Ensures a product-specific quantity-unit conversion exists with the given factor,
     * creating or updating as needed, and returns the row id.
     */
    suspend fun createQuConversion(conversion: NewQuConversion): Int

    /** All products, for selection lists. */
    suspend fun getProducts(): List<ProductOption>

    /** Purchase-form defaults and unit info for one product. */
    suspend fun getProductPurchaseInfo(productId: Int): ProductPurchaseInfo
}
