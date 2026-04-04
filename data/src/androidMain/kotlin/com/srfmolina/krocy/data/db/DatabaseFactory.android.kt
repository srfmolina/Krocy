package com.srfmolina.krocy.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual fun getDatabaseBuilder(): RoomDatabase.Builder<KrocyDatabase> {
    val context: Context by lazy {
        // Koin provee el contexto Android
        object : KoinComponent { val ctx: Context by inject() }.ctx
    }
    val dbFile = context.getDatabasePath("krocy.db")
    return Room.databaseBuilder<KrocyDatabase>(
        context = context,
        name = dbFile.absolutePath
    )
}