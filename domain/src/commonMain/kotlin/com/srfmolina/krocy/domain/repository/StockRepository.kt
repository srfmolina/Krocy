package com.srfmolina.krocy.domain.repository

import com.srfmolina.krocy.domain.model.stock.NewPurchase
import com.srfmolina.krocy.domain.model.stock.StockItem
import kotlinx.coroutines.flow.Flow

interface StockRepository {
    /**
     * The cached stock. Emits once it has been loaded and on every later change; it never
     * fails and never completes, so a single collector outlives any number of failed loads.
     * Loading is explicit: [ensureLoaded] or [forceRefresh].
     */
    fun getStock(): Flow<List<StockItem>>

    /** Loads the stock unless it already has been. Throws when the load fails. */
    suspend fun ensureLoaded()

    suspend fun consume(productId: Int, amount: Int)

    suspend fun open(productId: Int, amount: Int)

    suspend fun add(productId: Int, amount: Int)

    suspend fun purchase(purchase: NewPurchase)

    suspend fun forceRefresh()
}