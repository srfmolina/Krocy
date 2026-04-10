package com.srfmolina.krocy.data.repository.impl

import com.srfmolina.krocy.data.datasource.remote.stock.StockDataSource
import com.srfmolina.krocy.data.mapper.toDomain
import com.srfmolina.krocy.domain.model.stock.StockItem
import com.srfmolina.krocy.domain.repository.StockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.update

internal class StockRepositoryImpl(
    private val dataSource: StockDataSource
) : StockRepository {

    private val _cache = MutableStateFlow<List<StockItem>>(emptyList())

    override fun getStock(): Flow<List<StockItem>> = _cache
        .asStateFlow()
        .onSubscription { refresh() }

    private suspend fun refresh() {
        dataSource.getStock()
            .getOrThrow()
            .also { dtos -> _cache.update { dtos.map { it.toDomain() } } }
    }
}