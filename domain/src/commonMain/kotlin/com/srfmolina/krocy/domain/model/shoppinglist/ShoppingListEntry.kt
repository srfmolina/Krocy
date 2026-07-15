package com.srfmolina.krocy.domain.model.shoppinglist

/**
 * One row of the shopping list, already joined with its product master data.
 *
 * @param groupName the product's group, or null when the product has no group assigned.
 */
data class ShoppingListEntry(
    val id: Int,
    val productId: Int,
    val productName: String,
    val groupName: String?,
    val amount: Double,
    val unitName: String,
    val unitNamePlural: String,
    val done: Boolean,
)
