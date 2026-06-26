package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.db.StudyTask
import com.example.data.db.WeakChapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * StudyPlannerViewModel - Dynamic scheduler & timetable generator for JEE preparation.
 * Handles Room DB state ingestion of identified weak chapters, triggers adaptive timetable generation,
 * and maintains task completion updates using reactive flows.
 */
class StudyPlannerViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val weakChapterDao = database.weakChapterDao()
    private val studyTaskDao = database.studyTaskDao()

    // 1. Data Ingestion: Expose the weak chapters as a reactive StateFlow
    val weakChaptersState: StateFlow<List<WeakChapter>> = weakChapterDao.getAllWeakChaptersFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Expose generated study tasks as a reactive StateFlow
    val studyTasksState: StateFlow<List<StudyTask>> = studyTaskDao.getAllStudyTasksFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    init {
        // Bootstrap default weak chapters and default schedule if empty to provide immediate user value
        viewModelScope.launch {
            val existingChapters = weakChapterDao.getAllWeakChapters()
            if (existingChapters.isEmpty()) {
                weakChapterDao.insertWeakChapter(
                    WeakChapter(chapterName = "Rotational Dynamics", subject = "PHYSICS", priority = "CRITICAL")
                )
                weakChapterDao.insertWeakChapter(
                    WeakChapter(chapterName = "Chemical Equilibrium", subject = "CHEMISTRY", priority = "HIGH")
                )
                weakChapterDao.insertWeakChapter(
                    WeakChapter(chapterName = "Permutations & Combinations", subject = "MATHEMATICS", priority = "HIGH")
                )
            }

            val existingTasks = studyTaskDao.getAllStudyTasks()
            if (existingTasks.isEmpty()) {
                // Generate initial schedule based on the bootstrapped weak chapters
                generatePersonalizedTimetableInternal()
            }
        }
    }

    /**
     * Add a customized identified weak chapter to the database.
     */
    fun addWeakChapter(chapterName: String, subject: String, priority: String) {
        if (chapterName.isBlank()) {
            _errorMessage.value = "Chapter name cannot be empty."
            return
        }
        viewModelScope.launch {
            try {
                weakChapterDao.insertWeakChapter(
                    WeakChapter(chapterName = chapterName.trim(), subject = subject, priority = priority)
                )
                _successMessage.value = "Added weak chapter: $chapterName"
                // Auto-regenerate planner timetable to accommodate the new weak area
                generatePersonalizedTimetableInternal()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add chapter: ${e.localizedMessage}"
            }
        }
    }

    /**
     * Delete an identified weak chapter.
     */
    fun deleteWeakChapter(chapter: WeakChapter) {
        viewModelScope.launch {
            try {
                weakChapterDao.deleteWeakChapter(chapter)
                _successMessage.value = "Removed weak chapter: ${chapter.chapterName}"
                // Auto-regenerate planner timetable to accommodate the changes
                generatePersonalizedTimetableInternal()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to remove chapter: ${e.localizedMessage}"
            }
        }
    }

    /**
     * Interactively toggle task completion status in the Room database.
     */
    fun toggleTaskCompletion(id: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            try {
                studyTaskDao.setTaskCompleted(id, isCompleted)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update task: ${e.localizedMessage}"
            }
        }
    }

    /**
     * Explicit trigger to regenerate a fresh personalized timetable.
     */
    fun regenerateTimetable() {
        viewModelScope.launch {
            generatePersonalizedTimetableInternal()
            _successMessage.value = "Adaptive Study Plan generated successfully!"
        }
    }

    /**
     * 2. Generation Engine: Inner algorithm synthesizing daily revision blocks,
     * theory sweeps, and custom-targeted quiz challenges specifically around weak chapters.
     */
    private suspend fun generatePersonalizedTimetableInternal() {
        val weakChapters = weakChapterDao.getAllWeakChapters()
        val tasks = mutableListOf<StudyTask>()

        if (weakChapters.isEmpty()) {
            // Standard general fallback timetable if no weak chapters exist
            tasks.add(
                StudyTask(
                    title = "General Core Revision",
                    description = "Review physics and math key conceptual notes and cheat sheets.",
                    subject = "GENERAL",
                    durationMinutes = 60,
                    taskType = "REVISION_BLOCK"
                )
            )
            tasks.add(
                StudyTask(
                    title = "Mixed Practice Drill",
                    description = "Attempt 15 mixed-topic MCQ practice exercises on JEE levels.",
                    subject = "GENERAL",
                    durationMinutes = 90,
                    taskType = "PRACTICE_PROBLEMS"
                )
            )
        } else {
            // 2. Generation algorithm: Prioritize weak chapters and schedule targeted blocks
            weakChapters.forEach { chapter ->
                when (chapter.priority) {
                    "CRITICAL" -> {
                        // Critical weak chapters require comprehensive Theory + High Practice blocks
                        tasks.add(
                            StudyTask(
                                title = "Deep Theory Revise: ${chapter.chapterName}",
                                description = "Study derivations, core formulas, and critical pitfalls for ${chapter.chapterName}.",
                                subject = chapter.subject,
                                durationMinutes = 75,
                                taskType = "THEORY_REVISION"
                            )
                        )
                        tasks.add(
                            StudyTask(
                                title = "High-Intensity Practice: ${chapter.chapterName}",
                                description = "Solve 20 high-yield repeated past-year JEE mains questions on ${chapter.chapterName}.",
                                subject = chapter.subject,
                                durationMinutes = 90,
                                taskType = "PRACTICE_PROBLEMS"
                            )
                        )
                    }
                    "HIGH" -> {
                        // High priority weak chapters get structured revision and average practice drill
                        tasks.add(
                            StudyTask(
                                title = "Formula Revision: ${chapter.chapterName}",
                                description = "Review quick reference sheets and solve 5 basic examples for ${chapter.chapterName}.",
                                subject = chapter.subject,
                                durationMinutes = 45,
                                taskType = "THEORY_REVISION"
                            )
                        )
                        tasks.add(
                            StudyTask(
                                title = "Targeted Practice: ${chapter.chapterName}",
                                description = "Solve 12 medium-difficulty level JEE problems for ${chapter.chapterName}.",
                                subject = chapter.subject,
                                durationMinutes = 60,
                                taskType = "PRACTICE_PROBLEMS"
                            )
                        )
                    }
                    else -> {
                        // Medium priority weak chapters get a rapid quiz or brief concept check
                        tasks.add(
                            StudyTask(
                                title = "Concept Sweep: ${chapter.chapterName}",
                                description = "Scan weak sub-topics and clarify doubts on ${chapter.chapterName}.",
                                subject = chapter.subject,
                                durationMinutes = 30,
                                taskType = "REVISION_BLOCK"
                            )
                        )
                    }
                }
            }

            // Always append a daily mock drill or a comprehensive revision block for general balance
            tasks.add(
                StudyTask(
                    title = "Mock MCQ Sprint",
                    description = "Complete a 30-minute rapid-fire countdown MCQ session to build speed.",
                    subject = "GENERAL",
                    durationMinutes = 45,
                    taskType = "MOCK_DRILL"
                )
            )
        }

        // Write the fresh list to Room DB in a single block transaction
        studyTaskDao.deleteAllStudyTasks()
        studyTaskDao.insertAllStudyTasks(tasks)
    }

    fun dismissMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}
