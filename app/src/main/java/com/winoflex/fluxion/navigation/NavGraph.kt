package com.winoflex.fluxion.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.winoflex.fluxion.ui.screens.HomeScreen
import com.winoflex.fluxion.ui.screens.LoginScreen
import com.winoflex.fluxion.ui.screens.SplashScreen

@Composable
fun FluxionNavGraph() {
    val backStack = rememberNavBackStack(Route.Splash)
    val entryProvider = remember {
        entryProvider<NavKey> {
            addEntryProvider(Route.Splash) {
                SplashScreen(onAnimationComplete = {
                    backStack.clear()
                    backStack.add(Route.Login)
                })
            }
            addEntryProvider(Route.Login) {
                LoginScreen(onLoginSuccess = {
                    backStack.clear()
                    backStack.add(Route.Home)
                })
            }
            addEntryProvider(Route.Home) { HomeScreen() }
        }
    }

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider
    )
}
