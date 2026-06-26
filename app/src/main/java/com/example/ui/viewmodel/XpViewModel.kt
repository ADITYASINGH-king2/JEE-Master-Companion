package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.gamification.XpTrackerEngine
import com.example.data.pref.SecurePreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * XpViewModel - Dynamic, thread-safe presenter handling JEE application gamification state.
 * Commits XP modifications to EncryptedSharedPreferences dynamically on a background IO thread
 * and updates live reactive flows with zero-micro-stutter precision.
 */
class XpViewModel(application: Application) : AndroidViewModel(application) {

    private val securePrefs = SecurePreferenceManager.getInstance(application)

    private val _currentTotalXp = MutableStateFlow(0)
    val currentTotalXp: StateFlow<Int> = _currentTotalXp.asStateFlow()

    private val _computedLevel = MutableStateFlow(1)
    val computedLevel: StateFlow<Int> = _computedLevel.asStateFlow()

    private val _xpProgressToNextLevel = MutableStateFlow(0.0f)
    val xpProgressToNextLevel: StateFlow<Float> = _xpProgressToNextLevel.asStateFlow()

    init {
        loadXpState()
    }

    /**
     * Loads saved XP values from secure hardware-backed storage.
     */
    fun loadXpState() {
        viewModelScope.launch {
            val totalXp = withContext(Dispatchers.IO) {
                securePrefs.getTotalXp()
            }
            updateLocalState(totalXp)
        }
    }

    /**
     * Adds XP securely on a background dispatcher, prevents main-thread block,
     * and updates live state immediately.
     */
    fun awardXp(amount: Int) {
        if (amount <= 0) return
        viewModelScope.launch {
            val updatedXp = withContext(Dispatchers.IO) {
                val currentXp = securePrefs.getTotalXp()
                val newXp = currentXp + amount
                securePrefs.setTotalXp(newXp)
                newXp
            }
            updateLocalState(updatedXp)
        }
    }

    /**
     * Resets XP progress to 0 for debugging or profile restarts.
     */
    fun resetXp() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                securePrefs.setTotalXp(0)
            }
            updateLocalState(0)
        }
    }

    /**
     * Centralized state updater utilizing high-performance XpTrackerEngine
     * to eliminate round-off micro-stutters during re-renders.
     */
    private fun updateLocalState(totalXp: Int) {
        _currentTotalXp.value = totalXp
        _computedLevel.value = XpTrackerEngine.calculateLevel(totalXp)
        _xpProgressToNextLevel.value = XpTrackerEngine.getProgressFloat(totalXp)
    }
}
