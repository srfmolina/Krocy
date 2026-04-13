package com.srfmolina.krocy.data.repository.impl

import com.srfmolina.krocy.data.datasource.local.example.KrocyItemDataSource
import com.srfmolina.krocy.data.mapper.toDomain
import com.srfmolina.krocy.data.mapper.toEntity
import com.srfmolina.krocy.domain.model.example.KrocyItem
import com.srfmolina.krocy.domain.repository.KrocyItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class KrocyItemRepositoryImpl( // An example
    private val datasource: KrocyItemDataSource
) : KrocyItemRepository {

    override fun getAll(): Flow<List<KrocyItem>> =
        datasource.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun save(item: KrocyItem) =
        datasource.save(item.toEntity())

    override suspend fun deleteById(id: Int) =
        datasource.deleteById(id)
}