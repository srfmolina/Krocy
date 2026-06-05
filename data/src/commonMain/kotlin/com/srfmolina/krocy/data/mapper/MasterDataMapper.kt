package com.srfmolina.krocy.data.mapper

import com.srfmolina.krocy.domain.model.masterdata.Location
import com.srfmolina.krocy.domain.model.masterdata.ProductGroup
import com.srfmolina.krocy.domain.model.masterdata.QuantityUnit
import org.openapitools.client.models.ObjectsEntityGet200ResponseInner

fun ObjectsEntityGet200ResponseInner.toQuantityUnit(): QuantityUnit {
    val singular = name.orEmpty()
    return QuantityUnit(
        id = id ?: 0,
        name = singular,
        namePlural = namePlural?.takeIf { it.isNotBlank() } ?: singular,
    )
}

fun ObjectsEntityGet200ResponseInner.toLocation(): Location =
    Location(
        id = id ?: 0,
        name = name.orEmpty(),
    )

fun ObjectsEntityGet200ResponseInner.toProductGroup(): ProductGroup =
    ProductGroup(
        id = id ?: 0,
        name = name.orEmpty(),
    )
