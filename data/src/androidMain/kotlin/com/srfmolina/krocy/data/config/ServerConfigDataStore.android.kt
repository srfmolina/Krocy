package com.srfmolina.krocy.data.config

import android.content.Context
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal actual fun serverConfigStoreDir(): String {
    val context = object : KoinComponent { val ctx: Context by inject() }.ctx
    return context.filesDir.absolutePath
}
