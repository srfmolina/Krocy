package com.srfmolina.krocy.data.repository.impl

import com.srfmolina.krocy.data.datasource.remote.stock.StockDataSource
import com.srfmolina.krocy.data.mapper.toDomain
import com.srfmolina.krocy.domain.model.stock.StockItem
import com.srfmolina.krocy.domain.repository.StockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class StockRepositoryImpl(
    private val dataSource: StockDataSource
) : StockRepository {

    private val _cache = MutableStateFlow<List<StockItem>>(emptyList())

    private val refreshMutex = Mutex()
    private var loaded = false

    override fun getStock(): Flow<List<StockItem>> = _cache
        .onSubscription { ensureLoaded() }

    /** Loads the stock once; later subscribers reuse the cached value. */
    private suspend fun ensureLoaded() {
        if (loaded) return
        refreshMutex.withLock {
            if (loaded) return
            refresh()
        }
    }

    override suspend fun consume(productId: Int) {
        dataSource.consume(productId).getOrThrow()
        refresh()
    }

    private suspend fun refresh() {
        dataSource.getStock()
            .getOrThrow()
            .also { dtos -> _cache.update { dtos.map { it.toDomain() } } }
        loaded = true
    }
}
