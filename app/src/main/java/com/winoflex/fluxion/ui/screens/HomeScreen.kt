package com.winoflex.fluxion.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.winoflex.fluxion.navigation.Route

@Composable
fun HomeScreen() {
    val innerBackStack = rememberNavBackStack(Route.Dashboard)
    val entryProvider = remember {
        entryProvider<NavKey> {
            addEntryProvider(Route.Dashboard) { DashboardScreen() }
            addEntryProvider(Route.Analytics) { AnalyticsScreen() }
            addEntryProvider(Route.Recharge) { RechargeScreen() }
            addEntryProvider(Route.Notifications) { NotificationsScreen() }
            addEntryProvider(Route.Profile) {
                ProfileScreen(
                    onNavigateToSettings = { innerBackStack.add(Route.Settings) },
                    onNavigateToAbout = { innerBackStack.add(Route.About) }
                )
            }
            addEntryProvider(Route.Settings) {
                SettingsScreen(onBack = { innerBackStack.removeLastOrNull() })
            }
            addEntryProvider(Route.About) {
                AboutScreen(onBack = { innerBackStack.removeLastOrNull() })
            }
        }
    }

    val currentRoute = innerBackStack.last() as Route
    val showBottomBar = currentRoute in listOf(
        Route.Dashboard,
        Route.Analytics,
        Route.Recharge,
        Route.Notifications,
        Route.Profile
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                FluxionBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        innerBackStack.clear()
                        innerBackStack.add(route)
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavDisplay(
                backStack = innerBackStack,
                entryProvider = entryProvider
            )
        }
    }
}

@Composable
fun FluxionBottomBar(
    currentRoute: Route,
    onNavigate: (Route) -> Unit
) {
    val items = listOf(
        BottomNavItem("Dashboard", Route.Dashboard, Icons.Rounded.Dashboard),
        BottomNavItem("Analytics", Route.Analytics, Icons.Rounded.Analytics),
        BottomNavItem("Recharge", Route.Recharge, Icons.Rounded.Payments),
        BottomNavItem("Notifications", Route.Notifications, Icons.Rounded.Notifications),
        BottomNavItem("Profile", Route.Profile, Icons.Rounded.Person)
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

data class BottomNavItem(
    val label: String,
    val route: Route,
    val icon: ImageVector
)
