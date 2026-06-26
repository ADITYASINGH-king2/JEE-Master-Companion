package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.AppDatabase
import com.example.data.db.WeakChapter
import com.example.data.db.StudyTask
import com.example.ui.viewmodel.StudyPlannerViewModel
import com.example.ui.viewmodel.ProgressTrackerViewModel
import com.example.ui.screens.MatcherCategory
import com.example.ui.screens.MatchResult
import com.example.ui.screens.matchFormula
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ExampleRobolectricTest {

    private lateinit var context: Context
    private lateinit var database: AppDatabase

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        database = AppDatabase.getDatabase(context)
    }

    @Test
    fun testAppContextExists() {
        assertNotNull(context)
    }

    @Test
    fun testDatabaseSavesWeakChapter() = runBlocking {
        val dao = database.weakChapterDao()
        dao.deleteAllWeakChapters()

        val chapter = WeakChapter(chapterName = "Electrostatics", subject = "PHYSICS", priority = "CRITICAL")
        dao.insertWeakChapter(chapter)

        val allChapters = dao.getAllWeakChapters()
        assertEquals(1, allChapters.size)
        assertEquals("Electrostatics", allChapters[0].chapterName)
        assertEquals("PHYSICS", allChapters[0].subject)
        assertEquals("CRITICAL", allChapters[0].priority)
    }

    @Test
    fun testDatabaseSavesStudyTask() = runBlocking {
        val dao = database.studyTaskDao()
        dao.deleteAllStudyTasks()

        val task = StudyTask(
            title = "Test Task",
            description = "Test Desc",
            subject = "PHYSICS",
            durationMinutes = 45,
            taskType = "THEORY_REVISION",
            isCompleted = false
        )
        dao.insertStudyTask(task)

        val allTasks = dao.getAllStudyTasks()
        assertEquals(1, allTasks.size)
        assertEquals("Test Task", allTasks[0].title)
        assertFalse(allTasks[0].isCompleted)

        // Test completion toggle query
        dao.setTaskCompleted(allTasks[0].id, true)
        val updatedTasks = dao.getAllStudyTasks()
        assertTrue(updatedTasks[0].isCompleted)
    }

    @Test
    fun testStudyPlannerViewModelInitialization() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        // Initialize ViewModel which should trigger default bootstrapping
        val viewModel = StudyPlannerViewModel(app)

        // Wait for flows to collect initial bootstrapped values using predicate filters
        val weakChaptersList = viewModel.weakChaptersState.first { it.isNotEmpty() }
        assertEquals(3, weakChaptersList.size) // Rotational Dynamics, Chemical Equilibrium, Permutations & Combinations

        val studyTasksList = viewModel.studyTasksState.first { it.isNotEmpty() }
        assertTrue(studyTasksList.isNotEmpty())
    }

    @Test
    fun testStudyPlannerViewModelAddAndDeleteWeakChapter() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = StudyPlannerViewModel(app)

        // Clear existing for clean isolated test
        database.weakChapterDao().deleteAllWeakChapters()

        viewModel.addWeakChapter("Organic Synthesis", "CHEMISTRY", "CRITICAL")

        val listAfterAdd = viewModel.weakChaptersState.first { it.any { ch -> ch.chapterName == "Organic Synthesis" } }
        assertEquals(1, listAfterAdd.size)
        assertEquals("Organic Synthesis", listAfterAdd[0].chapterName)

        // Delete it
        viewModel.deleteWeakChapter(listAfterAdd[0])
        val listAfterDelete = viewModel.weakChaptersState.first { it.isEmpty() }
        assertTrue(listAfterDelete.isEmpty())
    }

    @Test
    fun testProgressTrackerViewModelInitialization() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        
        // Ensure db is empty to trigger bootstrap
        database.mockTestHistoryDao().deleteAllHistories()
        
        val viewModel = ProgressTrackerViewModel(app)
        val histories = viewModel.testHistoryState.first { it.isNotEmpty() }
        
        assertEquals(5, histories.size)
        assertEquals("JEE Mains Mock-1", histories[0].testId)
        assertEquals(165, histories[0].rawScore)
        assertEquals(62.5, histories[0].accuracyDelta, 0.01)
    }

    @Test
    fun testProgressTrackerViewModelDeleteHistory() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        database.mockTestHistoryDao().deleteAllHistories()
        
        val viewModel = ProgressTrackerViewModel(app)
        val histories = viewModel.testHistoryState.first { it.isNotEmpty() }
        assertEquals(5, histories.size)
        
        // Delete first history item
        viewModel.deleteTestHistory(histories[0])
        val updatedHistories = viewModel.testHistoryState.first { it.size == 4 }
        assertEquals(4, updatedHistories.size)
    }

    @Test
    fun testGivenToFindMatcherKinematicsRules() {
        // Test Kinematics: u, a, t -> v => v = u + a·t
        val result1 = matchFormula(
            category = MatcherCategory.KINEMATICS,
            givens = setOf("u", "a", "t"),
            target = "v"
        )
        assertTrue(result1 is MatchResult.Success)
        assertEquals("v = u + a·t", (result1 as MatchResult.Success).unicodeFormula)

        // Test Kinematics: u, a, s -> v => v² = u² + 2·a·s
        val result2 = matchFormula(
            category = MatcherCategory.KINEMATICS,
            givens = setOf("u", "a", "s"),
            target = "v"
        )
        assertTrue(result2 is MatchResult.Success)
        assertEquals("v² = u² + 2·a·s", (result2 as MatchResult.Success).unicodeFormula)

        // Test Kinematics partial info
        val result3 = matchFormula(
            category = MatcherCategory.KINEMATICS,
            givens = setOf("u", "s"),
            target = "v"
        )
        assertTrue(result3 is MatchResult.Partial)
    }

    @Test
    fun testGivenToFindMatcherElectricityRules() {
        // Test Electricity: I, R -> V => V = I · R
        val result1 = matchFormula(
            category = MatcherCategory.ELECTRICITY,
            givens = setOf("I", "R"),
            target = "V"
        )
        assertTrue(result1 is MatchResult.Success)
        assertEquals("V = I · R", (result1 as MatchResult.Success).unicodeFormula)

        // Test Electricity: V, R -> P => P = V² / R
        val result2 = matchFormula(
            category = MatcherCategory.ELECTRICITY,
            givens = setOf("V", "R"),
            target = "P"
        )
        assertTrue(result2 is MatchResult.Success)
        assertEquals("P = V² / R", (result2 as MatchResult.Success).unicodeFormula)
    }

    @Test
    fun testGivenToFindMatcherRotationalRules() {
        // Test Rotational: ω₀, α, t -> ω => ω = ω₀ + α·t
        val result1 = matchFormula(
            category = MatcherCategory.ROTATIONAL,
            givens = setOf("ω₀", "α", "t"),
            target = "ω"
        )
        assertTrue(result1 is MatchResult.Success)
        assertEquals("ω = ω₀ + α·t", (result1 as MatchResult.Success).unicodeFormula)
    }

    @Test
    fun testDerivationVaultViewModelOperations() {
        // Instantiate our DerivationVaultViewModel with test context
        val viewModel = com.example.ui.screens.DerivationVaultViewModel(context)

        // 1. Check initial defaults
        assertEquals("", viewModel.searchQuery)
        assertEquals("All", viewModel.selectedCategory)

        // 2. Test Category and Search Query updates
        viewModel.selectCategory("Mechanics")
        assertEquals("Mechanics", viewModel.selectedCategory)
        viewModel.searchQuery = "motion"
        assertEquals("motion", viewModel.searchQuery)

        // 3. Test expand/collapse states (Thread-safe Snapshotted Map check)
        assertFalse(viewModel.isExpanded("escape_velocity"))
        viewModel.toggleExpanded("escape_velocity")
        assertTrue(viewModel.isExpanded("escape_velocity"))
        viewModel.toggleExpanded("escape_velocity")
        assertFalse(viewModel.isExpanded("escape_velocity"))

        // 4. Test Favorite Bookmark persistence in SharedPreferences
        assertFalse(viewModel.favorites["photoelectric_effect"] ?: false)
        viewModel.toggleFavorite("photoelectric_effect")
        assertTrue(viewModel.favorites["photoelectric_effect"] ?: false)

        // Instantiate another instance of viewModel to check persistent loading from preferences
        val secondViewModel = com.example.ui.screens.DerivationVaultViewModel(context)
        assertTrue(secondViewModel.favorites["photoelectric_effect"] ?: false)
    }

    @Test
    fun testJeeFormulaFormatterEngine() {
        // 1. Verify parsing exponents (Superscript)
        val formattedE = com.example.util.JeeFormulaFormatter.format("E = m c^{2}")
        assertNotNull(formattedE)
        // Since we cannot inspect individual span structures easily in standard JUnit assert without deep reflections,
        // we verify that braces are parsed correctly and length or character content is processed accurately.
        assertTrue(formattedE.text.contains("E = m c2"))
        assertFalse(formattedE.text.contains("^{2}"))

        // 2. Verify parsing subscripts (Subscript)
        val formattedWater = com.example.util.JeeFormulaFormatter.format("H_{2}O")
        assertTrue(formattedWater.text.contains("H2O"))
        assertFalse(formattedWater.text.contains("_{2}"))

        // 3. Verify standard single character super/subscripts work without braces
        val formattedSingleSuper = com.example.util.JeeFormulaFormatter.format("x^2")
        assertTrue(formattedSingleSuper.text.contains("x2"))
        assertFalse(formattedSingleSuper.text.contains("^2"))

        val formattedSingleSub = com.example.util.JeeFormulaFormatter.format("v_e")
        assertTrue(formattedSingleSub.text.contains("ve"))
        assertFalse(formattedSingleSub.text.contains("_e"))

        // 4. Verify integrated Unicode dictionary exists and has standard constants
        val symbols = com.example.util.JeeFormulaFormatter.UNICODE_SYMBOLS
        assertFalse(symbols.isEmpty())
        
        val containsPi = symbols.any { it.symbol == "π" }
        assertTrue(containsPi)

        val containsEpsilon = symbols.any { it.symbol == "ε₀" }
        assertTrue(containsEpsilon)
    }

    @Test
    fun testNcertLawsViewModelFilteringAndDebounce() = runBlocking {
        val viewModel = com.example.ui.screens.NcertLawsViewModel()

        // 1. Initially, laws database shouldn't be empty
        assertFalse(viewModel.ncertLawsDatabase.isEmpty())

        // 2. Initial state of filteredLaws should contain the database entries
        kotlinx.coroutines.delay(100)
        val initialList = viewModel.filteredLaws.value
        assertFalse(initialList.isEmpty())

        // 3. Update query and test that debounce doesn't instantly filter
        viewModel.updateSearchQuery("Coulomb")
        // Immediate check should be the previous state because of the 300ms debounce
        assertEquals(initialList.size, viewModel.filteredLaws.value.size)

        // Wait for debounce (300ms) to complete
        kotlinx.coroutines.delay(400)
        val filteredList = viewModel.filteredLaws.value
        // Should find "Coulomb's Law"
        assertTrue(filteredList.any { it.title.contains("Coulomb") })
        // Should filter out Ohm's Law
        assertFalse(filteredList.any { it.title.contains("Ohm") })

        // 4. Test category filtering (e.g., Chemistry)
        viewModel.selectChapterFilter("Chemistry")
        kotlinx.coroutines.delay(400)
        val chemistryList = viewModel.filteredLaws.value
        assertTrue(chemistryList.all { it.subject == "Chemistry" })
    }

    @Test
    fun testPhysicsPatternsViewModelFilteringAndDebounce() = runBlocking {
        val viewModel = com.example.ui.screens.PhysicsPatternsViewModel()

        // 1. Initially, physics patterns list shouldn't be empty
        assertFalse(com.example.ui.viewmodel.PhysicsPatternsRepository.patternsList.isEmpty())

        // 2. Initial state of filteredPatterns should contain the database entries
        kotlinx.coroutines.delay(100)
        val initialList = viewModel.filteredPatterns.value
        assertFalse(initialList.isEmpty())

        // 3. Update query and test that debounce doesn't instantly filter
        viewModel.updateSearchQuery("Bohr")
        // Immediate check should equal initial size due to 300ms debounce
        assertEquals(initialList.size, viewModel.filteredPatterns.value.size)

        // Wait for debounce (300ms) to complete
        kotlinx.coroutines.delay(400)
        val filteredList = viewModel.filteredPatterns.value
        // Should find "Bohr"
        assertTrue(filteredList.any { it.title.contains("Bohr") })
        // Should filter out Coulomb's Law
        assertFalse(filteredList.any { it.title.contains("Coulomb") })

        // 4. Test chapter filtering
        viewModel.selectChapter("Electrostatics")
        kotlinx.coroutines.delay(400)
        val electrostaticsList = viewModel.filteredPatterns.value
        assertTrue(electrostaticsList.all { it.chapter == "Electrostatics" })
    }
}
