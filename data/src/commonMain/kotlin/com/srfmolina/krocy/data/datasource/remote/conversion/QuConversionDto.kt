package com.srfmolina.krocy.data.datasource.remote.conversion

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Body/row of /objects/quantity_unit_conversions. Hand-written because the
 * generated client's generic-entity DTO does not model these fields.
 */
@Serializable
internal data class QuConversionDto(
    @SerialName("id") val id: Int? = null,
    @SerialName("product_id") val productId: Int? = null,
    @SerialName("from_qu_id") val fromQuId: Int,
    @SerialName("to_qu_id") val toQuId: Int,
    @SerialName("factor") val factor: Double,
)

@Serializable
internal data class CreatedObjectDto(
    @SerialName("created_object_id") val createdObjectId: Int? = null,
)
