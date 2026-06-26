package com.example.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for ClaimedMilestones.
 * Features strict coroutine integration for standard persistence cycles.
 */
@Dao
interface ClaimedMilestonesDao {

    @Query("SELECT * FROM claimed_milestones ORDER BY timestamp DESC")
    fun getAllMilestonesFlow(): Flow<List<ClaimedMilestones>>

    @Query("SELECT * FROM claimed_milestones ORDER BY timestamp DESC")
    suspend fun getAllMilestones(): List<ClaimedMilestones>

    @Query("SELECT * FROM claimed_milestones WHERE badgeId = :badgeId LIMIT 1")
    suspend fun getMilestoneByBadgeId(badgeId: String): ClaimedMilestones?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestone(milestone: ClaimedMilestones)

    @Update
    suspend fun updateMilestone(milestone: ClaimedMilestones)

    @Delete
    suspend fun deleteMilestone(milestone: ClaimedMilestones)

    @Query("DELETE FROM claimed_milestones")
    suspend fun deleteAllMilestones()
}
