package com.srfmolina.krocy.data.mapper

import com.srfmolina.krocy.domain.model.product.NewProduct
import org.openapitools.client.models.ObjectsEntityGet200ResponseInner

/** Builds the request body for POST /objects/products from a [NewProduct]. */
fun NewProduct.toCreateDto(): ObjectsEntityGet200ResponseInner =
    ObjectsEntityGet200ResponseInner(
        name = name,
        quIdStock = quIdStock,
        quIdPurchase = quIdPurchase,
        locationId = locationId,
        description = description,
        minStockAmount = minStockAmount,
        productGroupId = productGroupId,
        // Null means "use Grocy's default" — the DTO defaults are 0, and sending an
        // explicit null would be serialized (the field default is non-null 0).
        defaultBestBeforeDays = defaultBestBeforeDays ?: 0,
        defaultBestBeforeDaysAfterOpen = defaultBestBeforeDaysAfterOpen ?: 0,
        shouldNotBeFrozen = if (shouldNotBeFrozen) 1 else 0,
    )
