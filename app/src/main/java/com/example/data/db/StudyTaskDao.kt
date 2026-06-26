package com.example.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyTaskDao {

    @Query("SELECT * FROM study_tasks ORDER BY id ASC")
    fun getAllStudyTasksFlow(): Flow<List<StudyTask>>

    @Query("SELECT * FROM study_tasks")
    suspend fun getAllStudyTasks(): List<StudyTask>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyTask(task: StudyTask)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllStudyTasks(tasks: List<StudyTask>)

    @Update
    suspend fun updateStudyTask(task: StudyTask)

    @Query("UPDATE study_tasks SET isCompleted = :completed WHERE id = :id")
    suspend fun setTaskCompleted(id: Int, completed: Boolean)

    @Query("DELETE FROM study_tasks")
    suspend fun deleteAllStudyTasks()
}
