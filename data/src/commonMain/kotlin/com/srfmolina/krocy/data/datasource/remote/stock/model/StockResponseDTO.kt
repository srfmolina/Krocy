package com.srfmolina.krocy.data.datasource.remote.stock.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StockResponseDTO(
    @SerialName("product_id")
    val productId: Int,
    @SerialName("amount")
    val amount: Double,
    @SerialName("amount_aggregated")
    val amountAggregated: Double,
    @SerialName("amount_opened")
    val amountOpened: Double,
    @SerialName("amount_opened_aggregated")
    val amountOpenedAggregated: Double,
    @SerialName("best_before_date")
    val bestBeforeDate: String?,
    @SerialName("is_aggregated_amount")
    val isAggregatedAmount: Boolean,
    @SerialName("product")
    val product: ProductResponseDTO
)
