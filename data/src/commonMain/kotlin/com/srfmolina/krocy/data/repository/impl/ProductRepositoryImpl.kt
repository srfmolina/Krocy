package com.srfmolina.krocy.data.repository.impl

import com.srfmolina.krocy.data.datasource.remote.conversion.QuConversionDataSource
import com.srfmolina.krocy.data.datasource.remote.generic.GenericEntityDataSource
import com.srfmolina.krocy.data.datasource.remote.stock.StockDataSource
import com.srfmolina.krocy.data.mapper.toCreateDto
import com.srfmolina.krocy.data.mapper.toDto
import com.srfmolina.krocy.data.mapper.toProductOption
import com.srfmolina.krocy.data.mapper.toProductPurchaseInfo
import com.srfmolina.krocy.domain.model.product.NewProduct
import com.srfmolina.krocy.domain.model.product.NewQuConversion
import com.srfmolina.krocy.domain.model.product.ProductOption
import com.srfmolina.krocy.domain.model.product.ProductPurchaseInfo
import com.srfmolina.krocy.domain.repository.ProductRepository

internal class ProductRepositoryImpl(
    private val genericEntityDataSource: GenericEntityDataSource,
    private val quConversionDataSource: QuConversionDataSource,
    private val stockDataSource: StockDataSource,
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

    override suspend fun getProducts(): List<ProductOption> =
        genericEntityDataSource.getProducts().getOrThrow().map { it.toProductOption() }

    override suspend fun getProductPurchaseInfo(productId: Int): ProductPurchaseInfo =
        stockDataSource.getProductDetails(productId).getOrThrow().toProductPurchaseInfo()
}
