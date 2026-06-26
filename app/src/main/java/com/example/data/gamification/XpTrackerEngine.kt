package com.example.data.gamification

/**
 * XpTrackerEngine - Handles strict, high-performance integer-based calculations
 * for XP and Level Progression to prevent micro-stutters and mathematical float leaks.
 */
object XpTrackerEngine {
    const val MAX_LEVEL = 17
    const val XP_PER_LEVEL = 1000

    /**
     * Calculates the level purely using integer arithmetic.
     * Level = (TotalXP / 1000) + 1, capped strictly at MAX_LEVEL (17).
     */
    fun calculateLevel(totalXp: Int): Int {
        if (totalXp <= 0) return 1
        val computed = (totalXp / XP_PER_LEVEL) + 1
        return computed.coerceAtMost(MAX_LEVEL)
    }

    /**
     * Calculates current level's accumulated XP and the total XP needed to clear it.
     * Guaranteed to use pure integer math to avoid round-off discrepancies.
     * Returns Pair(currentLevelXpAccumulated, nextLevelRequiredXpTarget)
     */
    fun getProgressRatio(totalXp: Int): Pair<Int, Int> {
        val currentLevel = calculateLevel(totalXp)
        if (currentLevel >= MAX_LEVEL) {
            // Maximum level reached: progress bar is completely full
            return Pair(XP_PER_LEVEL, XP_PER_LEVEL)
        }
        val previousLevelBase = (currentLevel - 1) * XP_PER_LEVEL
        val accumulated = totalXp - previousLevelBase
        return Pair(accumulated.coerceIn(0, XP_PER_LEVEL), XP_PER_LEVEL)
    }

    /**
     * Computes level progress purely as a float between 0.0f and 1.0f for the Compose UI.
     * The input parameters to the division are strictly pre-checked integers.
     */
    fun getProgressFloat(totalXp: Int): Float {
        val (accumulated, target) = getProgressRatio(totalXp)
        return accumulated.toFloat() / target.toFloat()
    }
}
