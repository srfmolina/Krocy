package com.srfmolina.krocy.data.repository.impl

import com.srfmolina.krocy.data.datasource.remote.conversion.QuConversionDataSource
import com.srfmolina.krocy.data.datasource.remote.generic.GenericEntityDataSource
import com.srfmolina.krocy.data.mapper.toCreateDto
import com.srfmolina.krocy.data.mapper.toDto
import com.srfmolina.krocy.domain.model.product.NewProduct
import com.srfmolina.krocy.domain.model.product.NewQuConversion
import com.srfmolina.krocy.domain.repository.ProductRepository

internal class ProductRepositoryImpl(
    private val genericEntityDataSource: GenericEntityDataSource,
    private val quConversionDataSource: QuConversionDataSource,
) : ProductRepository {

    override suspend fun createProduct(product: NewProduct): Int =
        genericEntityDataSource.createProduct(product.toCreateDto()).getOrThrow()

    // Grocy auto-creates 1:1 conversions between purchase and stock unit when a product
    // is created and no conversion resolves the pair, so a blind POST hits its unique
    // constraint. Update the existing row when present; Grocy maintains the inverse row.
    override suspend fun createQuConversion(conversion: NewQuConversion): Int {
        val existingId = quConversionDataSource.getQuConversionsForProduct(conversion.productId)
            .getOrThrow()
            .firstOrNull {
                it.productId == conversion.productId &&
                    it.fromQuId == conversion.fromQuId &&
                    it.toQuId == conversion.toQuId
            }
            ?.id

        return if (existingId != null) {
            quConversionDataSource.updateQuConversion(existingId, conversion.toDto()).getOrThrow()
            existingId
        } else {
            quConversionDataSource.createQuConversion(conversion.toDto()).getOrThrow()
        }
    }
}
