package com.srfmolina.krocy.data.repository.impl

import com.srfmolina.krocy.domain.model.stock.StockItem
import com.srfmolina.krocy.domain.repository.StockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onSubscription

internal class StockRepositoryImpl(): StockRepository {

    private val _cache: MutableStateFlow<List<StockItem>> = MutableStateFlow(emptyList())
    private val cache = _cache.asSharedFlow()

    override fun getStock(): Flow<List<StockItem>> = cache
        .onSubscription {
            runCatching {
                //TODO: calls datasource
            }.onFailure {
                //TODO: maps from network exception
                // throw it.fromNetworkException()
            }.onSuccess { response ->
                //TODO
//                _cache.update {
//                    response.map { it.toDomain() }
//                }
            }
        }

}