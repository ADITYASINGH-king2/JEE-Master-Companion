package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Schema Entity for tracking milestone achievements and user streaks.
 * Uses the badge/milestone ID as the primary key to avoid duplicate entries and repeated updates.
 */
@Entity(tableName = "claimed_milestones")
data class ClaimedMilestones(
    @PrimaryKey val badgeId: String,
    val streakCount: Int,
    val timestamp: Long
)
