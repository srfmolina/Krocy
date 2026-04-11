package com.srfmolina.krocy.domain.repository

import com.srfmolina.krocy.domain.model.KrocyItem
import kotlinx.coroutines.flow.Flow

interface KrocyItemRepository {
    fun getAll(): Flow<List<KrocyItem>>
    suspend fun save(item: KrocyItem)
    suspend fun deleteById(id: Int)
}