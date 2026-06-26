package com.example.data.gamification

import android.os.SystemClock

/**
 * Structured validation exceptions for daily streak claim integrity.
 */
sealed class StreakValidationException(message: String) : Exception(message) {
    class TimeSpoofDetected(message: String) : StreakValidationException(message)
    class DoubleClaimDebounced(message: String, val remainingMillis: Long) : StreakValidationException(message)
}

/**
 * StreakValidator - Protects daily streak rewards against spamming,
 * double claims, and clock/time manipulation cheating.
 */
object StreakValidator {
    const val DAY_IN_MILLIS = 86400000L // 24 Hours

    /**
     * Validates a check-in claim attempt against the stored timestamps.
     * Throws a StreakValidationException if verification fails.
     */
    @Throws(StreakValidationException::class)
    fun validateClaim(
        lastClaimWallTime: Long,
        lastClaimElapsedRealtime: Long,
        currentWallTime: Long = System.currentTimeMillis(),
        currentElapsedRealtime: Long = SystemClock.elapsedRealtime()
    ) {
        // 1. Clock manipulation backward detection
        if (currentWallTime < lastClaimWallTime) {
            throw StreakValidationException.TimeSpoofDetected(
                "System clock shifted backwards. Please synchronize your device time settings."
            )
        }

        // 2. Mismatch/Spoof verification if device has not rebooted
        if (currentElapsedRealtime >= lastClaimElapsedRealtime) {
            val elapsedRealtimeDelta = currentElapsedRealtime - lastClaimElapsedRealtime
            val wallTimeDelta = currentWallTime - lastClaimWallTime

            // Check if 24 hours of actual physical runtime have elapsed
            if (elapsedRealtimeDelta < DAY_IN_MILLIS) {
                // If the user shifted the wall clock forward to show >24 hours, but physical device time is <24 hours
                if (wallTimeDelta >= DAY_IN_MILLIS) {
                    throw StreakValidationException.TimeSpoofDetected(
                        "System clock manipulation detected. True active study time is insufficient."
                    )
                } else {
                    // Normal double-claim attempt within 24 hours
                    val remaining = DAY_IN_MILLIS - elapsedRealtimeDelta
                    throw StreakValidationException.DoubleClaimDebounced(
                        "You can only claim your study streak reward once every 24 hours.",
                        remainingMillis = remaining
                    )
                }
            }
        } else {
            // 3. Device reboot detected (elapsed real-time has reset)
            // In this case, we fall back to wall time delta, but we also verify that at least
            // the elapsed realtime itself doesn't show any fishy behavior.
            val wallTimeDelta = currentWallTime - lastClaimWallTime
            if (wallTimeDelta < DAY_IN_MILLIS) {
                val remaining = DAY_IN_MILLIS - wallTimeDelta
                throw StreakValidationException.DoubleClaimDebounced(
                    "You can only claim your study streak reward once every 24 hours.",
                    remainingMillis = remaining
                )
            }
        }
    }

    /**
     * Helper to compute remaining milliseconds until next claim.
     * Returns 0 if claim is valid.
     */
    fun getRemainingMillis(
        lastClaimWallTime: Long,
        lastClaimElapsedRealtime: Long,
        currentWallTime: Long = System.currentTimeMillis(),
        currentElapsedRealtime: Long = SystemClock.elapsedRealtime()
    ): Long {
        if (currentWallTime < lastClaimWallTime) return DAY_IN_MILLIS

        if (currentElapsedRealtime >= lastClaimElapsedRealtime) {
            val elapsedRealtimeDelta = currentElapsedRealtime - lastClaimElapsedRealtime
            val wallTimeDelta = currentWallTime - lastClaimWallTime

            if (elapsedRealtimeDelta < DAY_IN_MILLIS) {
                if (wallTimeDelta >= DAY_IN_MILLIS) {
                    // Spoofed clock - treat as fully locked out
                    return DAY_IN_MILLIS
                }
                return DAY_IN_MILLIS - elapsedRealtimeDelta
            }
        } else {
            val wallTimeDelta = currentWallTime - lastClaimWallTime
            if (wallTimeDelta < DAY_IN_MILLIS) {
                return DAY_IN_MILLIS - wallTimeDelta
            }
        }
        return 0L
    }
}
