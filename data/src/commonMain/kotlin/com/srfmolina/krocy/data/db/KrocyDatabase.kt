package com.srfmolina.krocy.data.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.srfmolina.krocy.data.db.dao.KrocyItemDao
import com.srfmolina.krocy.data.entity.example.KrocyItemEntity

@Database(entities = [KrocyItemEntity::class], version = 1)
@ConstructedBy(KrocyDatabaseConstructor::class)
abstract class KrocyDatabase : RoomDatabase() {
    abstract fun krocyItemDao(): KrocyItemDao
}

@Suppress("KotlinNoActualForExpect")
expect object KrocyDatabaseConstructor : RoomDatabaseConstructor<KrocyDatabase> {
    override fun initialize(): KrocyDatabase
}
