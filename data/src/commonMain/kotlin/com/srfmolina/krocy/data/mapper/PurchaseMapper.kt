package com.srfmolina.krocy.data.mapper

import com.srfmolina.krocy.domain.model.masterdata.ShoppingLocation
import com.srfmolina.krocy.domain.model.product.ProductOption
import com.srfmolina.krocy.domain.model.product.ProductPurchaseInfo
import org.openapitools.client.models.ObjectsEntityGet200ResponseInner
import org.openapitools.client.models.ProductDetailsResponse

fun ObjectsEntityGet200ResponseInner.toProductOption(): ProductOption =
    ProductOption(
        id = id ?: 0,
        name = name.orEmpty(),
    )

fun ObjectsEntityGet200ResponseInner.toShoppingLocation(): ShoppingLocation =
    ShoppingLocation(
        id = id ?: 0,
        name = name.orEmpty(),
    )

fun ProductDetailsResponse.toProductPurchaseInfo(): ProductPurchaseInfo {
    val purchaseSingular = defaultQuantityUnitPurchase?.name.orEmpty()
    val stockSingular = quantityUnitStock?.name.orEmpty()
    return ProductPurchaseInfo(
        productId = product?.id ?: 0,
        purchaseUnitName = purchaseSingular,
        purchaseUnitNamePlural = defaultQuantityUnitPurchase?.namePlural
            ?.takeIf { it.isNotBlank() } ?: purchaseSingular,
        stockUnitName = stockSingular,
        stockUnitNamePlural = quantityUnitStock?.namePlural
            ?.takeIf { it.isNotBlank() } ?: stockSingular,
        conversionFactorPurchaseToStock = quConversionFactorPurchaseToStock ?: 1.0,
        defaultBestBeforeDays = product?.defaultBestBeforeDays,
        defaultLocationId = product?.locationId,
        lastPrice = lastPrice,
        lastShoppingLocationId = lastShoppingLocationId,
    )
}
