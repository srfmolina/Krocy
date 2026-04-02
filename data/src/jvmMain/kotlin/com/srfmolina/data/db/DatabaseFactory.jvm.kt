package com.srfmolina.data.db

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual fun getDatabaseBuilder(): RoomDatabase.Builder<KrocyDatabase> {
    val dbFile = File(System.getProperty("user.home"), ".krocy/krocy.db")
    dbFile.parentFile?.mkdirs()
    return Room.databaseBuilder<KrocyDatabase>(
        name = dbFile.absolutePath
    )
}