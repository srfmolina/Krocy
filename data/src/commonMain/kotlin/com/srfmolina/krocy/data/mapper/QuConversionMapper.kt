package com.srfmolina.krocy.data.mapper

import com.srfmolina.krocy.domain.model.masterdata.QuConversion
import com.srfmolina.krocy.domain.model.product.NewQuConversion
import org.openapitools.client.models.QuantityUnitConversion

internal fun QuantityUnitConversion.toQuConversion(): QuConversion =
    QuConversion(
        id = id ?: 0,
        productId = productId,
        fromQuId = fromQuId,
        toQuId = toQuId,
        factor = factor,
    )

internal fun NewQuConversion.toDto(): QuantityUnitConversion =
    QuantityUnitConversion(
        productId = productId,
        fromQuId = fromQuId,
        toQuId = toQuId,
        factor = factor,
    )
