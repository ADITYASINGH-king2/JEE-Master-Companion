package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Schema Entity for tracking mock test histories.
 * Stores test identifiers, scores, accuracy percentages/deltas, and timestamp of completion.
 */
@Entity(tableName = "mock_test_history")
data class MockTestHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val testId: String,
    val rawScore: Int,
    val accuracyDelta: Double,
    val timestamp: Long
)
