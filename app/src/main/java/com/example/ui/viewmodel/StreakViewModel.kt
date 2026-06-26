package com.example.ui.viewmodel

import android.app.Application
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.gamification.StreakValidationException
import com.example.data.gamification.StreakValidator
import com.example.data.pref.SecurePreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * StreakViewModel - Presenter module implementing strict time-spoof validations,
 * reactive state propagation, and live lock-out state tracking.
 */
class StreakViewModel(application: Application) : AndroidViewModel(application) {

    private val securePrefs = SecurePreferenceManager.getInstance(application)

    private val _streakCount = MutableStateFlow(0)
    val streakCount: StateFlow<Int> = _streakCount.asStateFlow()

    private val _isClaimable = MutableStateFlow(true)
    val isClaimable: StateFlow<Boolean> = _isClaimable.asStateFlow()

    private val _countdownText = MutableStateFlow("")
    val countdownText: StateFlow<String> = _countdownText.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        loadStreakState()
        startCountdownTicker()
    }

    /**
     * Loads initial streak progress and claim details from secure storage.
     */
    fun loadStreakState() {
        viewModelScope.launch {
            val count = withContext(Dispatchers.IO) {
                securePrefs.getDailyStreakCount()
            }
            _streakCount.value = count
            updateClaimableState()
        }
    }

    /**
     * Continuous background ticker designed to update the interactive button state and countdown.
     * Keeps the UI reactive without causing main thread micro-stutters.
     */
    private fun startCountdownTicker() {
        viewModelScope.launch {
            while (true) {
                updateClaimableState()
                delay(1000)
            }
        }
    }

    /**
     * Periodically queries the StreakValidator to refresh lock-out status and countdown formatting.
     */
    private fun updateClaimableState() {
        val lastWall = securePrefs.getLastClaimWallTime()
        val lastElapsed = securePrefs.getLastClaimElapsedRealtime()

        val remainingMillis = StreakValidator.getRemainingMillis(
            lastClaimWallTime = lastWall,
            lastClaimElapsedRealtime = lastElapsed
        )

        if (remainingMillis <= 0) {
            _isClaimable.value = true
            _countdownText.value = ""
        } else {
            _isClaimable.value = false
            _countdownText.value = formatMillisToTime(remainingMillis)
        }
    }

    /**
     * Intercepts a user's attempt to claim the daily check-in reward.
     * Executes deep validator checks on a background thread and updates preference files.
     */
    fun claimDailyReward(xpViewModel: XpViewModel) {
        viewModelScope.launch {
            _errorMessage.value = null
            _successMessage.value = null

            try {
                // Fetch existing configurations
                val lastWall = securePrefs.getLastClaimWallTime()
                val lastElapsed = securePrefs.getLastClaimElapsedRealtime()
                val currentWall = System.currentTimeMillis()
                val currentElapsed = SystemClock.elapsedRealtime()

                // Perform core integrity validation checks
                StreakValidator.validateClaim(
                    lastClaimWallTime = lastWall,
                    lastClaimElapsedRealtime = lastElapsed,
                    currentWallTime = currentWall,
                    currentElapsedRealtime = currentElapsed
                )

                // Validation succeeded: commit changes securely on IO thread
                withContext(Dispatchers.IO) {
                    val currentStreak = securePrefs.getDailyStreakCount()
                    val newStreak = currentStreak + 1
                    
                    securePrefs.setDailyStreakCount(newStreak)
                    securePrefs.setLastClaimWallTime(currentWall)
                    securePrefs.setLastClaimElapsedRealtime(currentElapsed)
                    
                    _streakCount.value = newStreak
                }

                // Award 100 XP to the student's level tracker
                xpViewModel.awardXp(100)

                _successMessage.value = "Daily Check-in Successful! +100 XP Awarded."
                updateClaimableState()

            } catch (e: StreakValidationException.TimeSpoofDetected) {
                _errorMessage.value = "[Security Check Failed] ${e.message}"
            } catch (e: StreakValidationException.DoubleClaimDebounced) {
                _errorMessage.value = e.message
            } catch (e: Exception) {
                _errorMessage.value = "An unexpected error occurred during reward distribution."
            }
        }
    }

    /**
     * Clears error toasts/banners in the UI.
     */
    fun dismissMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }

    /**
     * Developer override tool to reset check-in clock configurations for diagnostic evaluations.
     */
    fun resetCheckInState() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                securePrefs.setDailyStreakCount(0)
                securePrefs.setLastClaimWallTime(0L)
                securePrefs.setLastClaimElapsedRealtime(0L)
            }
            _streakCount.value = 0
            _errorMessage.value = "Streak metrics reset successfully."
            updateClaimableState()
        }
    }

    /**
     * Converts raw milliseconds into human-readable countdown text (HH:mm:ss).
     */
    private fun formatMillisToTime(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }
}
