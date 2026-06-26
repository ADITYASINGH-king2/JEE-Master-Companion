package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * StudyTask - Room entity representing a generated study planner activity block.
 * Users can interactively mark these blocks as completed.
 */
@Entity(tableName = "study_tasks")
data class StudyTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val subject: String, // "PHYSICS", "CHEMISTRY", "MATHEMATICS", "GENERAL"
    val durationMinutes: Int,
    val isCompleted: Boolean = false,
    val taskType: String // "THEORY_REVISION", "PRACTICE_PROBLEMS", "REVISION_BLOCK", "MOCK_DRILL"
)
