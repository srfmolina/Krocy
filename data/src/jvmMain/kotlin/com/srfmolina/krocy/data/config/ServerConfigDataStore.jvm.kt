package com.srfmolina.krocy.data.config

import java.io.File

internal actual fun serverConfigStoreDir(): String =
    File(System.getProperty("user.home"), ".krocy").apply { mkdirs() }.absolutePath
