package com.srfmolina.krocy.data.datasource.remote.stock.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductResponseDTO(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String?,
    @SerialName("location_id")
    val locationId: String?,
    @SerialName("qu_id_purchase")
    val quIdPurchase: String?,
    @SerialName("qu_id_stock")
    val quIdStock: String?,
    @SerialName("min_stock_amount")
    val minStockAmount: String?,
    @SerialName("default_best_before_days")
    val defaultBestBeforeDays: String?,
    @SerialName("row_created_timestamp")
    val rowCreatedTimestamp: String?,
    @SerialName("product_group_id")
    val productGroupId: String?,
    @SerialName("picture_file_name")
    val pictureFileName: String?,
    @SerialName("default_best_before_days_after_open")
    val defaultBestBeforeDaysAfterOpen: String?,
    @SerialName("enable_tare_weight_handling")
    val enableTareWeightHandling: String?,
    @SerialName("tare_weight")
    val tareWeight: String?,
    @SerialName("not_check_stock_fulfillment_for_recipes")
    val notCheckStockFulfillmentForRecipes: String?,
    @SerialName("shopping_location_id")
    val shoppingLocationId: String?,
    @SerialName("should_not_be_frozen")
    val shouldNotBeFrozen: String?,
    @SerialName("default_consume_location_id")
    val defaultConsumeLocationId: String?,
    @SerialName("move_on_open")
    val moveOnOpen: String?
)
