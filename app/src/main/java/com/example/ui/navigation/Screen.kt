package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Type-safe immutable Screen route definitions.
 */
sealed class Screen(val route: String, val label: String = "", val icon: ImageVector? = null) {
    object Splash : Screen("splash")
    object MockAuth : Screen("mock_auth")
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Home)
    object Patterns : Screen("patterns", "Patterns", Icons.Default.List)
    object Milestones : Screen("milestones", "Badges", Icons.Default.Star)
    object Profile : Screen("profile", "Profile", Icons.Default.AccountCircle)
    object ProfileSetup : Screen("profile_setup")

    companion object {
        val bottomNavItems = listOf(Dashboard, Patterns, Milestones, Profile)
    }
}
