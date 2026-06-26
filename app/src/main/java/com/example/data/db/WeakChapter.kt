package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * WeakChapter - Room entity representing a user's identified weak area.
 * Used for targeted revision scheduling.
 */
@Entity(tableName = "weak_chapters")
data class WeakChapter(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val chapterName: String,
    val subject: String, // "PHYSICS", "CHEMISTRY", "MATHEMATICS"
    val priority: String // "CRITICAL", "HIGH", "MEDIUM"
)
