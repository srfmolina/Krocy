package com.srfmolina.krocy

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.srfmolina.krocy.di.initKoin

fun main() = application {

    initKoin()

    Window(
        onCloseRequest = ::exitApplication,
        title = "Krocy",
    ) {
        App()
    }
}