package com.srfmolina.krocy.data.datasource.local.example

import com.srfmolina.krocy.data.entity.example.KrocyItemEntity
import kotlinx.coroutines.flow.Flow

internal interface KrocyItemDataSource { // An example
    fun getAll(): Flow<List<KrocyItemEntity>>
    suspend fun save(entity: KrocyItemEntity)
    suspend fun deleteById(id: Int)
}