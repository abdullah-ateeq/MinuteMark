package com.example.minutemark.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String? = null, val icon: ImageVector? = null) {
    data object Splash : Screen("splash")
    data object Home : Screen("home", "Home", Icons.Default.Home)
    data object History : Screen("history", "History", Icons.Default.History)
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    data object About : Screen("about", "About", Icons.Default.Info)

    companion object {
        val bottomNavItems = listOf(Home, History, Settings, About)
    }
}
