package com.srfmolina.krocy.data.repository.impl

import com.srfmolina.krocy.data.datasource.remote.generic.GenericEntityDataSource
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
    private val stockDataSource: StockDataSource,
    private val genericEntityDataSource: GenericEntityDataSource,
    private val baseUrl: String
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
            refreshStock()
        }
    }

    override suspend fun consume(productId: Int, amount: Int) {
        stockDataSource.consume(productId, amount.toDouble()).getOrThrow()
        refreshStock()
    }

    override suspend fun open(productId: Int, amount: Int) {
        stockDataSource.open(productId, amount.toDouble()).getOrThrow()
        refreshStock()
    }

    override suspend fun add(productId: Int, amount: Int) {
        stockDataSource.add(productId, amount.toDouble()).getOrThrow()
        refreshStock()
    }

    private suspend fun refreshStock() {
        val quantityUnitsDtos = genericEntityDataSource.getQuantityUnits().getOrThrow()
        stockDataSource.getStock()
            .getOrThrow()
            .also { stockDtos -> _cache.update { stockDtos.map { stockDto ->
                val quDto = quantityUnitsDtos.firstOrNull { quData ->
                    quData.id ==  stockDto.product?.quIdStock
                }
                val singularName = quDto?.name ?: "ud"
                val pluralName = quDto?.namePlural ?: "uds"
                stockDto.toDomain(quantityNames = Pair(singularName, pluralName), baseUrl = baseUrl)
            } } }
        loaded = true
    }
}
