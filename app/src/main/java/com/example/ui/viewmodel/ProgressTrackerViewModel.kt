package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.db.MockTestHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ProgressTrackerViewModel - Core VM for Tool 2: Progress & Trend Tracker.
 * Safely pulls mock test historical data from Room database using Coroutines on Dispatchers.IO
 * so the main UI thread never freezes, and handles sample bootstrapping.
 */
class ProgressTrackerViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val mockTestHistoryDao = database.mockTestHistoryDao()

    private val _testHistoryState = MutableStateFlow<List<MockTestHistory>>(emptyList())
    val testHistoryState: StateFlow<List<MockTestHistory>> = _testHistoryState.asStateFlow()

    init {
        loadHistoricalData()
    }

    /**
     * Pulls historical mock test scores from Room database on Dispatchers.IO.
     * Guarantees non-blocking reactive stream emission for Canvas drawing logic.
     */
    fun loadHistoricalData() {
        viewModelScope.launch {
            // Room Flows are asynchronous, but we enforce Dispatchers.IO collection for extra safety
            withContext(Dispatchers.IO) {
                mockTestHistoryDao.getAllTestHistoriesFlow().collect { histories ->
                    // Sort chronologically for correct timeline progression
                    val sorted = histories.sortedBy { it.timestamp }
                    
                    if (sorted.isEmpty()) {
                        // Bootstrap realistic mock test history data if empty
                        bootstrapMockTestData()
                    } else {
                        _testHistoryState.value = sorted
                    }
                }
            }
        }
    }

    /**
     * Bootstraps 5 historical JEE mock tests with realistic scores/accuracy trends.
     */
    private suspend fun bootstrapMockTestData() {
        val now = System.currentTimeMillis()
        val dayMs = 24 * 60 * 60 * 1000L
        
        val samples = listOf(
            MockTestHistory(testId = "JEE Mains Mock-1", rawScore = 165, accuracyDelta = 62.5, timestamp = now - 10 * dayMs),
            MockTestHistory(testId = "JEE Mains Mock-2", rawScore = 185, accuracyDelta = 68.0, timestamp = now - 7 * dayMs),
            MockTestHistory(testId = "JEE Advanced Mock-1", rawScore = 198, accuracyDelta = 71.4, timestamp = now - 5 * dayMs),
            MockTestHistory(testId = "JEE Mains Mock-3", rawScore = 230, accuracyDelta = 78.5, timestamp = now - 3 * dayMs),
            MockTestHistory(testId = "JEE Mains Mock-4", rawScore = 255, accuracyDelta = 86.2, timestamp = now - 1 * dayMs)
        )
        
        samples.forEach {
            mockTestHistoryDao.insertTestHistory(it)
        }
    }

    /**
     * Interactively delete a logged test score.
     */
    fun deleteTestHistory(history: MockTestHistory) {
        viewModelScope.launch(Dispatchers.IO) {
            mockTestHistoryDao.deleteTestHistory(history)
        }
    }
}
