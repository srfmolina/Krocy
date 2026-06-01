package com.srfmolina.krocy.domain.repository

import com.srfmolina.krocy.domain.model.stock.StockItem
import kotlinx.coroutines.flow.Flow

interface StockRepository {
    fun getStock(): Flow<List<StockItem>>

    suspend fun consume(productId: Int, amount: Int)

    suspend fun open(productId: Int, amount: Int)

    suspend fun add(productId: Int, amount: Int)
}