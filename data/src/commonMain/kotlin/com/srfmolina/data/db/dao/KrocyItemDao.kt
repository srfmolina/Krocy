package com.srfmolina.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.srfmolina.data.entity.KrocyItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KrocyItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: KrocyItemEntity)

    @Query("SELECT * FROM krocy_items ORDER BY createdAt DESC")
    fun getAll(): Flow<List<KrocyItemEntity>>

    @Delete
    suspend fun delete(item: KrocyItemEntity)
}