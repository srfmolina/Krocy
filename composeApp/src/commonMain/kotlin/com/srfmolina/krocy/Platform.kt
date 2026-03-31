package com.srfmolina.krocy

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform