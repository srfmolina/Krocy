package com.srfmolina.krocy.data.db

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

expect fun getDatabaseBuilder(): RoomDatabase.Builder<KrocyDatabase>

fun createDatabase(): KrocyDatabase =
    getDatabaseBuilder()
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()