package com.srfmolina.krocy.domain.usecase.shoppinglist.model

data class AddToShoppingListUCRequest(
    val productId: Int,
    val amount: Double
)
