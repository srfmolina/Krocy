package com.srfmolina.krocy.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.srfmolina.krocy.data.entity.KrocyItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KrocyItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: KrocyItemEntity)

    @Query("SELECT * FROM krocy_items ORDER BY createdAt DESC")
    fun getAll(): Flow<List<KrocyItemEntity>>

    @Query("SELECT * FROM krocy_items WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): KrocyItemEntity?

    @Delete
    suspend fun delete(item: KrocyItemEntity)

    @Query("DELETE FROM krocy_items WHERE id = :id")
    suspend fun deleteById(id: Int)
}