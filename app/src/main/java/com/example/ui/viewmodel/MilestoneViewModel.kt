package com.example.ui.viewmodel

import android.app.Application
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.db.ClaimedMilestones
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * BadgeState - Holds the presentation layer info for milestones.
 * Includes both localized resource IDs and Compose ImageVectors.
 */
data class BadgeState(
    val id: String,
    val title: String,
    val subtitle: String,
    val isUnlocked: Boolean,
    val iconResId: Int, // Localized resource ID (such as android system star or placeholder)
    val iconVector: ImageVector, // Polished Compose ImageVector
    val description: String
)

/**
 * MilestoneViewModel - Manages gamified badges and achievements.
 * Integrates Room persistence reactively to determine badge unlock status.
 */
class MilestoneViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val milestonesDao = database.claimedMilestonesDao()

    // 6 Core Achievements static metadata definitions
    private val staticBadges = listOf(
        BadgeState(
            id = "first_streak",
            title = "First Streak",
            subtitle = "Active 3 Days",
            isUnlocked = false,
            iconResId = android.R.drawable.btn_star_big_on,
            iconVector = Icons.Default.Favorite,
            description = "Congratulations on taking your first steps! Unlocked by maintaining a consistent daily preparation streak of 3 consecutive days."
        ),
        BadgeState(
            id = "formula_master",
            title = "Formula Master",
            subtitle = "Revise Physics",
            isUnlocked = false,
            iconResId = android.R.drawable.ic_dialog_info,
            iconVector = Icons.Default.Build,
            description = "Perfect conceptual recall! Unlocked by successfully memorizing and completing all core physics formula card revisions."
        ),
        BadgeState(
            id = "mock_warrior",
            title = "Mock Warrior",
            subtitle = "Score > 180+",
            isUnlocked = false,
            iconResId = android.R.drawable.star_on,
            iconVector = Icons.Default.ThumbUp,
            description = "Under pressure, you thrive! Unlocked by logging a comprehensive full-syllabus mock exam score exceeding 180/300."
        ),
        BadgeState(
            id = "ai_explorer",
            title = "AI Explorer",
            subtitle = "Gemini Help",
            isUnlocked = false,
            iconResId = android.R.drawable.ic_search_category_default,
            iconVector = Icons.Default.Search,
            description = "Curiosity unlocked! Awarded for using Gemini intelligent analysis to break down complex JEE Advanced questions."
        ),
        BadgeState(
            id = "xp_pioneer",
            title = "XP Pioneer",
            subtitle = "Cross 2,000 XP",
            isUnlocked = false,
            iconResId = android.R.drawable.btn_star_big_on,
            iconVector = Icons.Default.Star,
            description = "Dedication unmatched! Unlocked by cross-accumulating 2,000 Total Experience Points (XP) through daily mock questions."
        ),
        BadgeState(
            id = "jee_conqueror",
            title = "JEE Conqueror",
            subtitle = "100% Reached",
            isUnlocked = false,
            iconResId = android.R.drawable.star_on,
            iconVector = Icons.Default.CheckCircle,
            description = "Ultimate victory! Unlocked by successfully scoring or completing all mock question patterns at Level 17 milestone."
        )
    )

    // Flow of claimed badges retrieved continuously from database
    val claimedBadgesFlow = milestonesDao.getAllMilestonesFlow()

    // Reactive computation combining DB state and static configurations
    val badges: StateFlow<List<BadgeState>> = claimedBadgesFlow.map { claimedList ->
        val claimedIds = claimedList.map { it.badgeId }.toSet()
        staticBadges.map { badge ->
            badge.copy(isUnlocked = claimedIds.contains(badge.id))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = staticBadges
    )

    // Currently selected badge for detail view sheet / modal
    private val _selectedBadge = MutableStateFlow<BadgeState?>(null)
    val selectedBadge: StateFlow<BadgeState?> = _selectedBadge

    fun selectBadge(badge: BadgeState?) {
        _selectedBadge.value = badge
    }

    /**
     * Unlocks/Claims a badge directly in Room on a background IO Thread.
     */
    fun claimBadge(badgeId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val existing = milestonesDao.getMilestoneByBadgeId(badgeId)
                if (existing == null) {
                    milestonesDao.insertMilestone(
                        ClaimedMilestones(
                            badgeId = badgeId,
                            streakCount = 1,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
            }
            // Update selected badge state if open
            _selectedBadge.value?.let { current ->
                if (current.id == badgeId) {
                    _selectedBadge.value = current.copy(isUnlocked = true)
                }
            }
        }
    }

    /**
     * Removes/Locks a badge for reset or demonstration purposes.
     */
    fun lockBadge(badgeId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val existing = milestonesDao.getMilestoneByBadgeId(badgeId)
                if (existing != null) {
                    milestonesDao.deleteMilestone(existing)
                }
            }
            _selectedBadge.value?.let { current ->
                if (current.id == badgeId) {
                    _selectedBadge.value = current.copy(isUnlocked = false)
                }
            }
        }
    }
}
