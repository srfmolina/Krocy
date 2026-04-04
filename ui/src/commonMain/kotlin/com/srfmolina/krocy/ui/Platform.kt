package com.srfmolina.krocy.ui

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform