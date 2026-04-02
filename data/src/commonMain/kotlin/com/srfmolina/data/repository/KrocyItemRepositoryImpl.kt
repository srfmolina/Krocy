package com.srfmolina.data.repository

import com.srfmolina.data.db.dao.KrocyItemDao
import com.srfmolina.data.mapper.toDomain
import com.srfmolina.data.mapper.toEntity
import com.srfmolina.domain.model.KrocyItem
import com.srfmolina.domain.repository.KrocyItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class KrocyItemRepositoryImpl(
    private val dao: KrocyItemDao
) : KrocyItemRepository {

    override fun getAll(): Flow<List<KrocyItem>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun save(item: KrocyItem) =
        dao.insert(item.toEntity())

    override suspend fun delete(item: KrocyItem) =
        dao.delete(item.toEntity())
}