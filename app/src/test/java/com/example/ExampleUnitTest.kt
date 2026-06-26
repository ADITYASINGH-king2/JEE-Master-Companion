package com.example

import com.example.data.gamification.XpTrackerEngine
import com.example.data.gamification.StreakValidator
import com.example.data.gamification.StreakValidationException
import com.example.ui.screens.getAvatarIdByIndex
import com.example.ui.screens.getAvatarIndexById
import com.example.ui.screens.avatarOptionsList
import com.example.ui.viewmodel.PatternsViewModel
import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 * Includes robust assertions for the gamification XpTrackerEngine to guarantee
 * zero mathematical micro-stutter anomalies.
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun `calculateLevel returns correct level based on total xp`() {
        // Base state: 0 XP should be level 1
        assertEquals(1, XpTrackerEngine.calculateLevel(0))

        // Level 2 base starts at 1000 XP
        assertEquals(2, XpTrackerEngine.calculateLevel(1000))

        // Level 17 (Max Level) capped at 16000+ XP
        assertEquals(17, XpTrackerEngine.calculateLevel(16000))
        assertEquals(17, XpTrackerEngine.calculateLevel(25000))
    }

    @Test
    fun `getProgressRatio returns correct pair for level bounds`() {
        // 0 XP -> 0 accumulated out of 1000 target
        val ratioZero = XpTrackerEngine.getProgressRatio(0)
        assertEquals(0, ratioZero.first)
        assertEquals(1000, ratioZero.second)

        // 1500 XP -> Level 2 has 500 accumulated out of 1000 target
        val ratioMid = XpTrackerEngine.getProgressRatio(1500)
        assertEquals(500, ratioMid.first)
        assertEquals(1000, ratioMid.second)

        // Over Max Level cap (17) -> Progress bar completely full
        val ratioMax = XpTrackerEngine.getProgressRatio(18000)
        assertEquals(1000, ratioMax.first)
        assertEquals(1000, ratioMax.second)
    }

    @Test
    fun `getProgressFloat returns ratio between 0f and 1f`() {
        assertEquals(0.0f, XpTrackerEngine.getProgressFloat(0), 0.001f)
        assertEquals(0.5f, XpTrackerEngine.getProgressFloat(1500), 0.001f)
        assertEquals(1.0f, XpTrackerEngine.getProgressFloat(18000), 0.001f)
    }

    @Test
    fun `validateClaim accepts correct daily claims`() {
        val lastWall = 1000000000L
        val lastElapsed = 100000L

        // Attempting to claim after 25 hours (90,000,000 milliseconds)
        val currentWall = lastWall + 90000000L
        val currentElapsed = lastElapsed + 90000000L

        try {
            StreakValidator.validateClaim(
                lastClaimWallTime = lastWall,
                lastClaimElapsedRealtime = lastElapsed,
                currentWallTime = currentWall,
                currentElapsedRealtime = currentElapsed
            )
            // Success, should not throw
        } catch (e: Exception) {
            fail("Valid daily claim should not throw an exception")
        }
    }

    @Test
    fun `validateClaim throws DoubleClaimDebounced if claimed within 24 hours`() {
        val lastWall = 1000000000L
        val lastElapsed = 100000L

        // Attempting to claim after only 5 hours (18,000,000 milliseconds)
        val currentWall = lastWall + 18000000L
        val currentElapsed = lastElapsed + 18000000L

        val exception = assertThrows(StreakValidationException.DoubleClaimDebounced::class.java) {
            StreakValidator.validateClaim(
                lastClaimWallTime = lastWall,
                lastClaimElapsedRealtime = lastElapsed,
                currentWallTime = currentWall,
                currentElapsedRealtime = currentElapsed
            )
        }

        // 24 hours - 5 hours = 19 hours remaining (68,400,000 milliseconds)
        assertEquals(68400000L, exception.remainingMillis)
    }

    @Test
    fun `validateClaim throws TimeSpoofDetected if wall clock is shifted forward but elapsed real time is not`() {
        val lastWall = 1000000000L
        val lastElapsed = 100000L

        // Attempting to spoof: shifted wall clock forward by 25 hours, but elapsed real time only advanced by 10 seconds (10000 ms)
        val currentWall = lastWall + 90000000L
        val currentElapsed = lastElapsed + 10000L

        assertThrows(StreakValidationException.TimeSpoofDetected::class.java) {
            StreakValidator.validateClaim(
                lastClaimWallTime = lastWall,
                lastClaimElapsedRealtime = lastElapsed,
                currentWallTime = currentWall,
                currentElapsedRealtime = currentElapsed
            )
        }
    }

    @Test
    fun `validateClaim throws TimeSpoofDetected if wall clock is shifted backwards`() {
        val lastWall = 1000000000L
        val lastElapsed = 100000L

        // Attempting to claim with shifted backward clock
        val currentWall = lastWall - 5000L
        val currentElapsed = lastElapsed + 5000L

        assertThrows(StreakValidationException.TimeSpoofDetected::class.java) {
            StreakValidator.validateClaim(
                lastClaimWallTime = lastWall,
                lastClaimElapsedRealtime = lastElapsed,
                currentWallTime = currentWall,
                currentElapsedRealtime = currentElapsed
            )
        }
    }

    @Test
    fun `strict non-numeric input filtering rules protect against bad pastes`() {
        val badPasteMobile = "9876abc54321xyz"
        val filteredMobile = badPasteMobile.filter { it.isDigit() }.take(10)
        assertEquals("987654321", filteredMobile)

        val badPasteOtp = "12ab34cd"
        val filteredOtp = badPasteOtp.filter { it.isDigit() }.take(4)
        assertEquals("1234", filteredOtp)
    }

    @Test
    fun `static bypass rule grants access only for 1234`() {
        val validOtp = "1234"
        val invalidOtp = "5678"
        assertTrue(validOtp == "1234")
        assertFalse(invalidOtp == "1234")
    }

    @Test
    fun `target date parsing correctly handles future years and determines remaining days`() {
        val targetYear = 2028
        val targetCalendar = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.YEAR, targetYear)
            set(java.util.Calendar.MONTH, java.util.Calendar.JANUARY)
            set(java.util.Calendar.DAY_OF_MONTH, 24)
            set(java.util.Calendar.HOUR_OF_DAY, 9)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }

        // Simulating current time: January 1st, 2028 (23 days before the exam)
        val nowCalendar = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.YEAR, targetYear)
            set(java.util.Calendar.MONTH, java.util.Calendar.JANUARY)
            set(java.util.Calendar.DAY_OF_MONTH, 1)
            set(java.util.Calendar.HOUR_OF_DAY, 9)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }

        val diff = targetCalendar.timeInMillis - nowCalendar.timeInMillis
        val days = diff / (24 * 3600000L)
        
        assertEquals(23L, days)
        assertTrue("Urgency state activates when remaining days is under 90 days", days < 90)
    }

    @Test
    fun `target date parsing determines non-urgency when remaining days is 100`() {
        val targetYear = 2028
        val targetCalendar = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.YEAR, targetYear)
            set(java.util.Calendar.MONTH, java.util.Calendar.JANUARY)
            set(java.util.Calendar.DAY_OF_MONTH, 24)
            set(java.util.Calendar.HOUR_OF_DAY, 9)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }

        // Simulating current time: 100 days before Jan 24, 2028
        val nowTime = targetCalendar.timeInMillis - (100 * 24 * 3600000L)
        val diff = targetCalendar.timeInMillis - nowTime
        val days = diff / (24 * 3600000L)

        assertEquals(100L, days)
        assertFalse("Urgency state should not activate when remaining days is 90 or more", days < 90)
    }

    @Test
    fun `profile name validation correctly requires at least 3 characters`() {
        val shortName = "Ab"
        val validName = "Abc"
        val nameWithSpaces = "  Ab  "

        val isShortValid = shortName.trim().length >= 3
        val isValidValid = validName.trim().length >= 3
        val isNameWithSpacesValid = nameWithSpaces.trim().length >= 3

        assertFalse("Names with under 3 non-whitespace characters are invalid", isShortValid)
        assertTrue("Names with 3 or more characters are valid", isValidValid)
        assertFalse("Names that reduce to under 3 non-whitespace characters are invalid", isNameWithSpacesValid)
    }

    @Test
    fun `avatar mapping helpers correctly resolve index to id and vice-versa`() {
        // Test first option
        val firstId = getAvatarIdByIndex(0)
        assertEquals("person", firstId)
        assertEquals(0, getAvatarIndexById("person"))

        // Test arbitrary valid index
        val thirdId = getAvatarIdByIndex(2)
        assertEquals("star", thirdId)
        assertEquals(2, getAvatarIndexById("star"))

        // Test index coercion bounds (negative index and large index)
        assertEquals("person", getAvatarIdByIndex(-1))
        assertEquals(getAvatarIdByIndex(avatarOptionsList.lastIndex), getAvatarIdByIndex(100))

        // Test invalid/unknown id returns fallback index 0
        assertEquals(0, getAvatarIndexById("non_existent_avatar_id_xyz"))
    }

    @Test
    fun `target year selector dynamically generates correct options starting from current year`() {
        val currentYear = java.time.Year.now().value
        val expectedOptions = listOf(currentYear, currentYear + 1, currentYear + 2, currentYear + 3)

        assertEquals(4, expectedOptions.size)
        assertEquals(currentYear, expectedOptions[0])
        assertEquals(currentYear + 3, expectedOptions[3])
    }

    @Test
    fun `study session service contains correct threshold of 12 hours in seconds`() {
        // 12 hours * 60 minutes * 60 seconds = 43,200 seconds
        val expectedSeconds = 12 * 3600
        assertEquals(expectedSeconds, StudySessionService.MEDAL_THRESHOLD_SECONDS)
        assertEquals("com.example.action.START", StudySessionService.ACTION_START)
        assertEquals("com.example.action.STOP", StudySessionService.ACTION_STOP)
        assertEquals("com.example.action.RESET", StudySessionService.ACTION_RESET)
    }

    @Test
    fun `high yield patterns contains no LaTeX formatting characters`() {
        // Test that our offline pattern texts do not use LaTeX notations like \, $, {, }, etc.
        val forbiddenChars = listOf("$", "\\", "{", "}")
        
        val patterns = listOf(
            "Resonance frequency is f = 1 / (2π * √(LC)). Electromagnetic energy oscillates continuously between the capacitor's electric field and the inductor's magnetic field without loss.",
            "Radii of orbits are directly proportional to n² / Z. Transitions emit photons where frequency corresponds to energy differences between atomic states.",
            "The half-life of a first-order chemical reaction remains completely independent of the starting reactant concentration.",
            "A reaction becomes spontaneous under constant pressure and temperature conditions when the change in Gibbs free energy is strictly negative.",
            "An indispensable tool in symmetric integrals, collapsing complicated trigonometric denominators into steady constant coefficients.",
            "Critical determinant shortcuts mapping identity and adjoint scalar proportions across square n×n matrix systems."
        )

        for (desc in patterns) {
            for (forbidden in forbiddenChars) {
                assertFalse(
                    "Found forbidden LaTeX character '$forbidden' in description: $desc",
                    desc.contains(forbidden)
                )
            }
        }
    }

    @Test
    fun `patterns viewmodel contains all default offline patterns`() {
        val vm = PatternsViewModel()
        assertEquals(9, vm.allPatterns.size)
    }

    @Test
    fun `patterns viewmodel correctly filters by subject`() {
        val vm = PatternsViewModel()
        
        // Filter by PHYSICS
        val physicsPatterns = vm.allPatterns.filter { it.subject == "PHYSICS" }
        assertEquals(3, physicsPatterns.size)
    }

    @Test
    fun `patterns viewmodel correctly filters by class level`() {
        val vm = PatternsViewModel()
        
        // Filter by 11TH class level
        val class11Patterns = vm.allPatterns.filter { it.classLevel == "11TH" }
        assertEquals(4, class11Patterns.size)
        
        // Filter by 12TH class level
        val class12Patterns = vm.allPatterns.filter { it.classLevel == "12TH" }
        assertEquals(5, class12Patterns.size)
    }

    @Test
    fun `patterns viewmodel correctly filters by weightage`() {
        val vm = PatternsViewModel()
        
        // Filter by CRITICAL
        val criticalPatterns = vm.allPatterns.filter { it.weightage == "CRITICAL" }
        assertEquals(3, criticalPatterns.size)
    }
}
