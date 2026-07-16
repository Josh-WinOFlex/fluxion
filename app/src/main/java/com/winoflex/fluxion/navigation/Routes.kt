package com.winoflex.fluxion.navigation

import kotlinx.serialization.Serializable
import androidx.navigation3.runtime.NavKey

sealed interface Route : NavKey {
    @Serializable
    data object Splash : Route

    @Serializable
    data object Login : Route

    @Serializable
    data object Home : Route

    @Serializable
    data object Settings : Route

    @Serializable
    data object Dashboard : Route

    @Serializable
    data object Analytics : Route

    @Serializable
    data object Recharge : Route

    @Serializable
    data object Notifications : Route

    @Serializable
    data object Profile : Route

    @Serializable
    data object About : Route
}
