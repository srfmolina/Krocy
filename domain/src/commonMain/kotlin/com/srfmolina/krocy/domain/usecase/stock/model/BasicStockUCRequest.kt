package com.srfmolina.krocy.domain.usecase.stock.model

data class BasicStockUCRequest(
    val productId: Int,
    val amount: Int
)