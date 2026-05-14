package com.srfmolina.krocy.data.datasource.local.example

import com.srfmolina.krocy.data.db.dao.KrocyItemDao
import com.srfmolina.krocy.data.entity.example.KrocyItemEntity
import kotlinx.coroutines.flow.Flow

internal class KrocyItemDataSourceImpl(private val dao: KrocyItemDao): KrocyItemDataSource { // An example
    override fun getAll(): Flow<List<KrocyItemEntity>> =
        dao.getAll()

    override suspend fun save(entity: KrocyItemEntity) =
        dao.insert(entity)

    override suspend fun deleteById(id: Int) =
        dao.deleteById(id)
}