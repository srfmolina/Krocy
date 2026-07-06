package com.srfmolina.krocy.data.mapper

import com.srfmolina.krocy.data.datasource.remote.conversion.QuConversionDto
import com.srfmolina.krocy.domain.model.masterdata.QuConversion
import com.srfmolina.krocy.domain.model.product.NewQuConversion

internal fun QuConversionDto.toQuConversion(): QuConversion =
    QuConversion(
        id = id ?: 0,
        productId = productId,
        fromQuId = fromQuId,
        toQuId = toQuId,
        factor = factor,
    )

internal fun NewQuConversion.toDto(): QuConversionDto =
    QuConversionDto(
        productId = productId,
        fromQuId = fromQuId,
        toQuId = toQuId,
        factor = factor,
    )
