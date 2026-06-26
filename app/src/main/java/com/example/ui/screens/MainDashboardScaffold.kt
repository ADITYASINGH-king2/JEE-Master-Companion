package com.example.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import com.example.ui.viewmodel.BackupViewModel
import com.example.ui.viewmodel.XpViewModel
import com.example.ui.viewmodel.StreakViewModel

enum class DashboardTab(val label: String, val icon: ImageVector) {
    Home("Home", Icons.Default.Home),
    Syllabus("Syllabus", Icons.Default.List),
    Quizzes("Quizzes", Icons.Default.Star),
    Progress("Progress", Icons.Default.AccountCircle)
}

/**
 * MainDashboardScaffold - The master dashboard scaffold of the JEE preparation application.
 * Manages persistent Material 3 bottom navigation, haptic tap feedback, and fluid crossfade tab transitions.
 */
@Composable
fun MainDashboardScaffold(
    backupViewModel: BackupViewModel,
    xpViewModel: XpViewModel,
    streakViewModel: StreakViewModel,
    onNavigateToSetup: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTab by remember { mutableStateOf(DashboardTab.Home) }
    val haptic = LocalHapticFeedback.current

    Scaffold(
        modifier = modifier.testTag("main_dashboard_scaffold"),
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("main_bottom_nav_bar")
            ) {
                DashboardTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        label = { Text(tab.label) },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = "${tab.label} Tab Icon"
                            )
                        },
                        onClick = {
                            if (currentTab != tab) {
                                // 4. Haptic Feedback: Trigger subtle vibration upon tapping different tabs
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                currentTab = tab
                            }
                        },
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        // 3. Fluid Tab Switching: Crossfade switching between tabs to eliminate harsh screen cuts
        Crossfade(
            targetState = currentTab,
            animationSpec = tween(350),
            modifier = Modifier.padding(innerPadding),
            label = "TabCrossfade"
        ) { tab ->
            when (tab) {
                DashboardTab.Home -> {
                    // Home tab renders the core DashboardScreen with countdown timer and XP leveling
                    DashboardScreen(
                        backupViewModel = backupViewModel,
                        xpViewModel = xpViewModel,
                        streakViewModel = streakViewModel,
                        modifier = Modifier.testTag("tab_screen_home")
                    )
                }
                DashboardTab.Syllabus -> {
                    // Syllabus tab renders progress diagnostics per-subject
                    PatternsScreen(
                        modifier = Modifier.testTag("tab_screen_syllabus")
                    )
                }
                DashboardTab.Quizzes -> {
                    // Quizzes tab renders the custom dynamic mock JEE quizzes
                    QuizzesScreen(
                        modifier = Modifier.testTag("tab_screen_quizzes")
                    )
                }
                DashboardTab.Progress -> {
                    // Progress tab renders profile setups, avatar selections, and database backup controls
                    ProfileScreen(
                        backupViewModel = backupViewModel,
                        onNavigateToSetup = onNavigateToSetup,
                        modifier = Modifier.testTag("tab_screen_progress")
                    )
                }
            }
        }
    }
}
