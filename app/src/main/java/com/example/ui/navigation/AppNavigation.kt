package com.example.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.MainDashboardScaffold
import com.example.ui.screens.MockAuthScreen
import com.example.ui.screens.ProfileSetupScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.viewmodel.BackupViewModel
import com.example.ui.viewmodel.XpViewModel
import com.example.ui.viewmodel.StreakViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Core Jetpack Compose Navigation System.
 * Coordinates smooth motion transitions, type-safe destinations, stack protection,
 * and smart layout nesting for bottom navigation.
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val backupViewModel: BackupViewModel = viewModel()
    val xpViewModel: XpViewModel = viewModel()
    val streakViewModel: StreakViewModel = viewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. Splash Screen
            composable(
                route = Screen.Splash.route,
                enterTransition = { fadeIn(animationSpec = tween(400)) },
                exitTransition = { fadeOut(animationSpec = tween(400)) }
            ) {
                SplashScreen(
                    onNavigationComplete = { isLoggedIn ->
                        val nextDest = if (isLoggedIn) Screen.Dashboard.route else Screen.MockAuth.route
                        navController.navigate(nextDest) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            // 2. Mock Authentication Screen
            composable(
                route = Screen.MockAuth.route,
                enterTransition = { slideInToLeft(this) },
                exitTransition = { slideOutToLeft(this) }
            ) {
                MockAuthScreen(
                    onAuthSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.MockAuth.route) { inclusive = true }
                        }
                    }
                )
            }

            // 3. Main Dashboard Scaffold (Contains the 4 navigation tabs: Home, Syllabus, Quizzes, Progress with fluid Crossfade)
            composable(
                route = Screen.Dashboard.route,
                enterTransition = { fadeIn(animationSpec = tween(300)) },
                exitTransition = { fadeOut(animationSpec = tween(300)) }
            ) {
                MainDashboardScaffold(
                    backupViewModel = backupViewModel,
                    xpViewModel = xpViewModel,
                    streakViewModel = streakViewModel,
                    onNavigateToSetup = {
                        navController.navigate(Screen.ProfileSetup.route)
                    }
                )
            }

            // 4. Profile Setup Screen (Onboarding/Config deep link)
            composable(
                route = Screen.ProfileSetup.route,
                enterTransition = { slideInToLeft(this) },
                exitTransition = { slideOutToLeft(this) }
            ) {
                ProfileSetupScreen(
                    backupViewModel = backupViewModel,
                    onSaveSuccess = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

/**
 * Slide slideIn transition helper from Right to Left for deep navigation entry.
 */
private fun slideInToLeft(scope: AnimatedContentTransitionScope<NavBackStackEntry>): EnterTransition {
    return scope.slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(400)
    )
}

/**
 * Slide slideOut transition helper from Right to Left for deep navigation exit.
 */
private fun slideOutToLeft(scope: AnimatedContentTransitionScope<NavBackStackEntry>): ExitTransition {
    return scope.slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(400)
    )
}
