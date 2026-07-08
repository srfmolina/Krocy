package org.openapitools.client.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Workaround for broken Grocy spec: the quantity_unit_conversions entity is exposed via
// /objects/{entity} but the spec never defines a schema for it (from_qu_id/to_qu_id/factor
// appear nowhere in components/schemas), so the generator cannot produce this model.
// Hand-written and protected via .openapi-generator-ignore.

/**
 * Row/body of the `quantity_unit_conversions` entity.
 *
 * @param fromQuId
 * @param toQuId
 * @param factor
 * @param id
 * @param productId
 * @param rowCreatedTimestamp
 */
@Serializable
data class QuantityUnitConversion (

    @SerialName(value = "from_qu_id") val fromQuId: kotlin.Int,

    @SerialName(value = "to_qu_id") val toQuId: kotlin.Int,

    @SerialName(value = "factor") val factor: kotlin.Double,

    @SerialName(value = "id") val id: kotlin.Int? = null,

    @SerialName(value = "product_id") val productId: kotlin.Int? = null,

    @SerialName(value = "row_created_timestamp") val rowCreatedTimestamp: kotlin.String? = null

)
