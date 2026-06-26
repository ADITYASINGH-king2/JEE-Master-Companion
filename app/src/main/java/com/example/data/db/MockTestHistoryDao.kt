package com.example.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for MockTestHistory.
 * Enforces Kotlin Coroutine suspend methods for write operations,
 * and reactive asynchronous Flow streams for data observation.
 */
@Dao
interface MockTestHistoryDao {

    @Query("SELECT * FROM mock_test_history ORDER BY timestamp DESC")
    fun getAllTestHistoriesFlow(): Flow<List<MockTestHistory>>

    @Query("SELECT * FROM mock_test_history ORDER BY timestamp DESC")
    suspend fun getAllTestHistories(): List<MockTestHistory>

    @Query("SELECT * FROM mock_test_history WHERE testId = :testId")
    suspend fun getTestHistoryByTestId(testId: String): List<MockTestHistory>

    @Query("SELECT * FROM mock_test_history WHERE rawScore >= :minScore")
    suspend fun getTestHistoriesWithMinScore(minScore: Int): List<MockTestHistory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestHistory(history: MockTestHistory)

    @Update
    suspend fun updateTestHistory(history: MockTestHistory)

    @Delete
    suspend fun deleteTestHistory(history: MockTestHistory)

    @Query("DELETE FROM mock_test_history")
    suspend fun deleteAllHistories()
}
