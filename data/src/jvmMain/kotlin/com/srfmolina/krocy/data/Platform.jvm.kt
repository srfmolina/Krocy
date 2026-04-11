package com.srfmolina.krocy.data

actual fun platform(): String = "JVM"
actual val isDebugBuild: Boolean = System.getProperty("krocy.debug")?.toBooleanStrictOrNull()
    ?: System.getenv("KROCY_DEBUG")?.toBooleanStrictOrNull()
    ?: false