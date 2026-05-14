package com.srfmolina.krocy.ui.presentation.navigation

import kotlinx.serialization.Serializable

internal sealed interface KrocyRoute

@Serializable
data object SplashRoute: KrocyRoute

@Serializable
data object WelcomeRoute: KrocyRoute

@Serializable
data object LoginRoute: KrocyRoute

@Serializable
data object StockRoute: KrocyRoute
