package com.srfmolina.krocy.data.repository.impl

import com.srfmolina.krocy.data.datasource.remote.generic.GenericEntityDataSource
import com.srfmolina.krocy.data.datasource.remote.stock.StockDataSource
import com.srfmolina.krocy.data.mapper.toDomain
import com.srfmolina.krocy.domain.model.stock.NewPurchase
import com.srfmolina.krocy.domain.model.stock.StockItem
import com.srfmolina.krocy.domain.repository.StockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class StockRepositoryImpl(
    private val stockDataSource: StockDataSource,
    private val genericEntityDataSource: GenericEntityDataSource,
    private val baseUrl: String
) : StockRepository {

    // null until the first successful load: nothing is emitted before then, and a failed
    // load leaves it null without ending anyone's collection.
    private val _cache = MutableStateFlow<List<StockItem>?>(null)

    private val refreshMutex = Mutex()
    @Volatile private var loaded = false

    override fun getStock(): Flow<List<StockItem>> = _cache.filterNotNull()

    /** Loads the stock once; later calls are no-ops. */
    override suspend fun ensureLoaded() {
        if (loaded) return
        refreshMutex.withLock {
            if (loaded) return
            refreshStock()
        }
    }

    override suspend fun consume(productId: Int, amount: Int) {
        stockDataSource.consume(productId, amount.toDouble()).getOrThrow()
        forceRefreshWithMutex()
    }

    override suspend fun open(productId: Int, amount: Int) {
        stockDataSource.open(productId, amount.toDouble()).getOrThrow()
        forceRefreshWithMutex()
    }

    override suspend fun add(productId: Int, amount: Int) {
        stockDataSource.add(productId, amount.toDouble()).getOrThrow()
        forceRefreshWithMutex()
    }

    override suspend fun purchase(purchase: NewPurchase) {
        stockDataSource.purchase(
            productId = purchase.productId,
            amount = purchase.amountStockUnits,
            bestBeforeDate = purchase.dueDate,
            price = purchase.pricePerStockUnit,
            locationId = purchase.locationId,
            shoppingLocationId = purchase.shoppingLocationId,
            note = purchase.note,
        ).getOrThrow()
        forceRefreshWithMutex()
    }

    override suspend fun forceRefresh() {
        forceRefreshWithMutex()
    }

    private suspend fun forceRefreshWithMutex() {
        refreshMutex.withLock {
            refreshStock()
        }
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
