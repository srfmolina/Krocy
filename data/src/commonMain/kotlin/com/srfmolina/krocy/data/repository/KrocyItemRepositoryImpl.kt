package com.srfmolina.krocy.data.repository

import com.srfmolina.krocy.data.db.dao.KrocyItemDao
import com.srfmolina.krocy.data.mapper.toDomain
import com.srfmolina.krocy.data.mapper.toEntity
import com.srfmolina.krocy.domain.model.KrocyItem
import com.srfmolina.krocy.domain.repository.KrocyItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class KrocyItemRepositoryImpl(
    private val dao: KrocyItemDao
) : KrocyItemRepository {

    override fun getAll(): Flow<List<KrocyItem>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun save(item: KrocyItem) =
        dao.insert(item.toEntity())

    override suspend fun deleteById(id: Int) =
        dao.deleteById(id)
}