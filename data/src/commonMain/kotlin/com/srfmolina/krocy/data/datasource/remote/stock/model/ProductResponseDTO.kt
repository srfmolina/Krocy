package com.srfmolina.krocy.data.datasource.remote.stock.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductResponseDTO(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String?,
    @SerialName("location_id")
    val locationId: Int?,
    @SerialName("qu_id_purchase")
    val quIdPurchase: Int?,
    @SerialName("qu_id_stock")
    val quIdStock: Int?,
    @SerialName("min_stock_amount")
    val minStockAmount: Double?,
    @SerialName("default_best_before_days")
    val defaultBestBeforeDays: Int?,
    @SerialName("row_created_timestamp")
    val rowCreatedTimestamp: String?,
    @SerialName("product_group_id")
    val productGroupId: Int?,
    @SerialName("picture_file_name")
    val pictureFileName: String?,
    @SerialName("default_best_before_days_after_open")
    val defaultBestBeforeDaysAfterOpen: Int?,
    @SerialName("enable_tare_weight_handling")
    val enableTareWeightHandling: Int?,
    @SerialName("tare_weight")
    val tareWeight: Double?,
    @SerialName("not_check_stock_fulfillment_for_recipes")
    val notCheckStockFulfillmentForRecipes: Int?,
    @SerialName("shopping_location_id")
    val shoppingLocationId: Int?,
    @SerialName("should_not_be_frozen")
    val shouldNotBeFrozen: Int?,
    @SerialName("default_consume_location_id")
    val defaultConsumeLocationId: Int?,
    @SerialName("move_on_open")
    val moveOnOpen: Int?
)
