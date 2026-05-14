package com.srfmolina.krocy.domain.repository

import com.srfmolina.krocy.domain.model.example.KrocyItem
import kotlinx.coroutines.flow.Flow

interface KrocyItemRepository { // An example
    fun getAll(): Flow<List<KrocyItem>>
    suspend fun save(item: KrocyItem)
    suspend fun deleteById(id: Int)
}