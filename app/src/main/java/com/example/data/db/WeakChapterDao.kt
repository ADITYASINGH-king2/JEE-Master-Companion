package com.example.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WeakChapterDao {

    @Query("SELECT * FROM weak_chapters ORDER BY id ASC")
    fun getAllWeakChaptersFlow(): Flow<List<WeakChapter>>

    @Query("SELECT * FROM weak_chapters")
    suspend fun getAllWeakChapters(): List<WeakChapter>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeakChapter(chapter: WeakChapter)

    @Delete
    suspend fun deleteWeakChapter(chapter: WeakChapter)

    @Query("DELETE FROM weak_chapters")
    suspend fun deleteAllWeakChapters()
}
