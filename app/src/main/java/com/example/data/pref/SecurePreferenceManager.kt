package com.example.data.pref

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * SecurePreferenceManager handles all sensitive client-side preferences and session values.
 * Leverages AndroidX EncryptedSharedPreferences utilizing the MasterKey builder framework
 * to guarantee AES-256 level hardware-backed security.
 * All mutations are safely dispatched off the main thread to prevent UI freezing.
 */
class SecurePreferenceManager private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences

    init {
        // Build the MasterKey using the recommended AES256_GCM scheme
        val masterKey = MasterKey.Builder(context.applicationContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        // Create the EncryptedSharedPreferences instance
        sharedPreferences = EncryptedSharedPreferences.create(
            context.applicationContext,
            SECURE_PREFS_FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Executes value mutations asynchronously off the Main thread using Dispatchers.IO.
     * Uses [SharedPreferences.Editor.apply] to ensure writing to disk happens asynchronously in the background.
     */
    private suspend fun editSecurely(action: (SharedPreferences.Editor) -> Unit) = withContext(Dispatchers.IO) {
        val editor = sharedPreferences.edit()
        action(editor)
        editor.apply()
    }

    // --- User Profile Information Getters & Setters ---

    /**
     * Retrieves the secured user profile name.
     */
    fun getUserName(): String {
        return sharedPreferences.getString(KEY_USER_NAME, "") ?: ""
    }

    /**
     * Asynchronously updates the secured user profile name.
     */
    suspend fun setUserName(name: String) {
        editSecurely { it.putString(KEY_USER_NAME, name) }
    }

    /**
     * Retrieves the secured user selected avatar index.
     */
    fun getSelectedAvatarIndex(): Int {
        return sharedPreferences.getInt(KEY_AVATAR_INDEX, 0)
    }

    /**
     * Asynchronously updates the secured user selected avatar index.
     */
    suspend fun setSelectedAvatarIndex(index: Int) {
        editSecurely { it.putInt(KEY_AVATAR_INDEX, index) }
    }

    /**
     * Retrieves the secured study motto.
     */
    fun getStudyMotto(): String {
        return sharedPreferences.getString(KEY_STUDY_MOTTO, "") ?: ""
    }

    /**
     * Asynchronously updates the secured study motto.
     */
    suspend fun setStudyMotto(motto: String) {
        editSecurely { it.putString(KEY_STUDY_MOTTO, motto) }
    }

    // --- Configuration & Session Parameters Getters & Setters ---

    /**
     * Retrieves the simulated OTP login state.
     */
    fun isOtpLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_OTP_LOGGED_IN, false)
    }

    /**
     * Asynchronously updates the simulated OTP login state.
     */
    suspend fun setOtpLoggedIn(loggedIn: Boolean) {
        editSecurely { it.putBoolean(KEY_OTP_LOGGED_IN, loggedIn) }
    }

    /**
     * Retrieves the selected target JEE year configuration parameter.
     */
    fun getTargetJeeYear(): Int {
        return sharedPreferences.getInt(KEY_TARGET_JEE_YEAR, 2027) // Default to 2027
    }

    /**
     * Asynchronously updates the selected target JEE year configuration parameter.
     */
    suspend fun setTargetJeeYear(year: Int) {
        editSecurely { it.putInt(KEY_TARGET_JEE_YEAR, year) }
    }

    /**
     * Retrieves the securely stored total user experience points (XP).
     */
    fun getTotalXp(): Int {
        return sharedPreferences.getInt(KEY_TOTAL_XP, 0)
    }

    /**
     * Asynchronously updates the securely stored total user experience points (XP).
     */
    suspend fun setTotalXp(xp: Int) {
        editSecurely { it.putInt(KEY_TOTAL_XP, xp) }
    }

    /**
     * Retrieves the last successful daily streak claim wall timestamp.
     */
    fun getLastClaimWallTime(): Long {
        return sharedPreferences.getLong(KEY_LAST_CLAIM_WALL_TIME, 0L)
    }

    /**
     * Updates the last successful daily streak claim wall timestamp.
     */
    suspend fun setLastClaimWallTime(time: Long) {
        editSecurely { it.putLong(KEY_LAST_CLAIM_WALL_TIME, time) }
    }

    /**
     * Retrieves the last successful daily streak claim elapsed realtime.
     */
    fun getLastClaimElapsedRealtime(): Long {
        return sharedPreferences.getLong(KEY_LAST_CLAIM_ELAPSED_REALTIME, 0L)
    }

    /**
     * Updates the last successful daily streak claim elapsed realtime.
     */
    suspend fun setLastClaimElapsedRealtime(time: Long) {
        editSecurely { it.putLong(KEY_LAST_CLAIM_ELAPSED_REALTIME, time) }
    }

    /**
     * Retrieves the securely stored current daily streak count.
     */
    fun getDailyStreakCount(): Int {
        return sharedPreferences.getInt(KEY_DAILY_STREAK_COUNT, 0)
    }

    /**
     * Updates the securely stored current daily streak count.
     */
    suspend fun setDailyStreakCount(streak: Int) {
        editSecurely { it.putInt(KEY_DAILY_STREAK_COUNT, streak) }
    }

    /**
     * Clears all stored credentials and flags.
     */
    suspend fun clearAll() {
        editSecurely { it.clear() }
    }

    companion object {
        private const val SECURE_PREFS_FILE_NAME = "secure_jee_companion_prefs"

        private const val KEY_USER_NAME = "secure_key_user_name"
        private const val KEY_AVATAR_INDEX = "secure_key_avatar_index"
        private const val KEY_STUDY_MOTTO = "secure_key_study_motto"
        private const val KEY_OTP_LOGGED_IN = "secure_key_otp_logged_in"
        private const val KEY_TARGET_JEE_YEAR = "secure_key_target_jee_year"
        private const val KEY_TOTAL_XP = "secure_key_total_xp"
        private const val KEY_LAST_CLAIM_WALL_TIME = "secure_key_last_claim_wall_time"
        private const val KEY_LAST_CLAIM_ELAPSED_REALTIME = "secure_key_last_claim_elapsed_realtime"
        private const val KEY_DAILY_STREAK_COUNT = "secure_key_daily_streak_count"

        @Volatile
        private var INSTANCE: SecurePreferenceManager? = null

        /**
         * Singleton initialization utilizing thread-safe double-check locking.
         */
        fun getInstance(context: Context): SecurePreferenceManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SecurePreferenceManager(context).also { INSTANCE = it }
            }
        }
    }
}
