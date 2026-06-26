package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * HighYieldPattern represents a repeated, heavily-weighted JEE core concept.
 */
data class HighYieldPattern(
    val id: String,
    val subject: String, // "PHYSICS", "CHEMISTRY", "MATHEMATICS"
    val chapter: String,
    val title: String,
    val weightage: String, // "CRITICAL", "HIGH", "MEDIUM"
    val frequency: String, // text description
    val description: String,
    val classLevel: String, // "11TH", "12TH"
    val frequencyPercentage: Int, // 1 to 100
    val scoringEaseScore: Int // 1 to 100
)

/**
 * PatternsViewModel - Manages the high-yield patterns state, implementing immediate, stutter-free
 * combined filtering and sorting in compliance with clean MVVM architecture.
 */
class PatternsViewModel : ViewModel() {

    // Hardcoded, offline repository of repeated JEE patterns across Physics, Chemistry, and Mathematics
    val allPatterns = listOf(
        HighYieldPattern(
            id = "pattern_lc_oscillations",
            subject = "PHYSICS",
            chapter = "Electromagnetic Induction & AC",
            title = "LC Oscillations Energy Conservation",
            weightage = "HIGH",
            frequency = "87% frequency in past 10 years",
            description = "Resonance frequency is f = 1 / (2π * √(LC)). Electromagnetic energy oscillates continuously between the capacitor's electric field and the inductor's magnetic field without loss.",
            classLevel = "12TH",
            frequencyPercentage = 87,
            scoringEaseScore = 65
        ),
        HighYieldPattern(
            id = "pattern_bohr_radii",
            subject = "PHYSICS",
            chapter = "Modern Physics",
            title = "Bohr's Atomic Orbit Radii & Transitions",
            weightage = "CRITICAL",
            frequency = "92% frequency in past 10 years",
            description = "Radii of orbits are directly proportional to n² / Z. Transitions emit photons where frequency corresponds to energy differences between atomic states.",
            classLevel = "12TH",
            frequencyPercentage = 92,
            scoringEaseScore = 85
        ),
        HighYieldPattern(
            id = "pattern_first_order",
            subject = "CHEMISTRY",
            chapter = "Chemical Kinetics",
            title = "Integrated First-Order Rate Law",
            weightage = "CRITICAL",
            frequency = "95% frequency in past 10 years",
            description = "The half-life of a first-order chemical reaction remains completely independent of the starting reactant concentration.",
            classLevel = "12TH",
            frequencyPercentage = 95,
            scoringEaseScore = 90
        ),
        HighYieldPattern(
            id = "pattern_gibbs_free",
            subject = "CHEMISTRY",
            chapter = "Thermodynamics",
            title = "Gibbs Free Energy & Spontaneity",
            weightage = "MEDIUM",
            frequency = "76% frequency in past 10 years",
            description = "A reaction becomes spontaneous under constant pressure and temperature conditions when the change in Gibbs free energy is strictly negative.",
            classLevel = "11TH",
            frequencyPercentage = 76,
            scoringEaseScore = 50
        ),
        HighYieldPattern(
            id = "pattern_kings_property",
            subject = "MATHEMATICS",
            chapter = "Definite Integration",
            title = "King's Property & Symmetry",
            weightage = "CRITICAL",
            frequency = "96% frequency in past 10 years",
            description = "An indispensable tool in symmetric integrals, collapsing complicated trigonometric denominators into steady constant coefficients.",
            classLevel = "12TH",
            frequencyPercentage = 96,
            scoringEaseScore = 80
        ),
        HighYieldPattern(
            id = "pattern_adjoint_matrix",
            subject = "MATHEMATICS",
            chapter = "Matrices & Determinants",
            title = "Properties of Adjoint Matrices",
            weightage = "HIGH",
            frequency = "89% frequency in past 10 years",
            description = "Critical determinant shortcuts mapping identity and adjoint scalar proportions across square n×n matrix systems.",
            classLevel = "12TH",
            frequencyPercentage = 89,
            scoringEaseScore = 75
        ),
        HighYieldPattern(
            id = "pattern_projectile_motion",
            subject = "PHYSICS",
            chapter = "Kinematics",
            title = "Projectile Range Maxima",
            weightage = "HIGH",
            frequency = "82% frequency in past 10 years",
            description = "Maximum horizontal range occurs at angle θ = 45° with value R = u² / g. Symmetric trajectory properties allow rapid calculations of time-of-flight and peak coordinate ratios.",
            classLevel = "11TH",
            frequencyPercentage = 82,
            scoringEaseScore = 95
        ),
        HighYieldPattern(
            id = "pattern_gaseous_state",
            subject = "CHEMISTRY",
            chapter = "Gaseous State",
            title = "Maxwell Speed Distribution",
            weightage = "MEDIUM",
            frequency = "72% frequency in past 10 years",
            description = "The ratios of most probable speed, average speed, and root-mean-square speed are v_mp : v_avg : v_rms = √(2) : √(8/π) : √(3). Remains constant for any ideal gas temperature proportions.",
            classLevel = "11TH",
            frequencyPercentage = 72,
            scoringEaseScore = 70
        ),
        HighYieldPattern(
            id = "pattern_conic_sections",
            subject = "MATHEMATICS",
            chapter = "Conic Sections",
            title = "Tangent Equations for Parabola",
            weightage = "HIGH",
            frequency = "85% frequency in past 10 years",
            description = "Equation of tangent to parabola y² = 4ax with slope m is given by y = mx + a/m. Crucial for finding common tangents with other quadratic systems.",
            classLevel = "11TH",
            frequencyPercentage = 85,
            scoringEaseScore = 88
        )
    )

    // Reactive filtering and sorting configurations
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedSubject = MutableStateFlow("ALL")
    val selectedSubject = _selectedSubject.asStateFlow()

    private val _selectedClassLevel = MutableStateFlow("ALL")
    val selectedClassLevel = _selectedClassLevel.asStateFlow()

    private val _selectedWeightage = MutableStateFlow("ALL")
    val selectedWeightage = _selectedWeightage.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.FREQUENCY)
    val sortOrder = _sortOrder.asStateFlow()

    enum class SortOrder {
        FREQUENCY, // Most Frequently Asked
        EASIEST_TO_SCORE // Easiest to Score
    }

    // Instantly combined StateFlow output that filters and sorts in real-time
    val filteredPatterns: StateFlow<List<HighYieldPattern>> = combine(
        _searchQuery,
        _selectedSubject,
        _selectedClassLevel,
        _selectedWeightage,
        _sortOrder
    ) { search, subject, classLvl, weightage, sort ->
        allPatterns.filter { pattern ->
            val matchesSearch = search.isEmpty() ||
                    pattern.title.contains(search, ignoreCase = true) ||
                    pattern.chapter.contains(search, ignoreCase = true) ||
                    pattern.description.contains(search, ignoreCase = true)

            val matchesSubject = subject == "ALL" || pattern.subject.equals(subject, ignoreCase = true)
            val matchesClass = classLvl == "ALL" || pattern.classLevel.equals(classLvl, ignoreCase = true)
            val matchesWeightage = weightage == "ALL" || pattern.weightage.equals(weightage, ignoreCase = true)

            matchesSearch && matchesSubject && matchesClass && matchesWeightage
        }.sortedWith { a, b ->
            when (sort) {
                SortOrder.FREQUENCY -> b.frequencyPercentage.compareTo(a.frequencyPercentage)
                SortOrder.EASIEST_TO_SCORE -> b.scoringEaseScore.compareTo(a.scoringEaseScore)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = allPatterns
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedSubject(subject: String) {
        _selectedSubject.value = subject
    }

    fun setSelectedClassLevel(classLvl: String) {
        _selectedClassLevel.value = classLvl
    }

    fun setSelectedWeightage(weightage: String) {
        _selectedWeightage.value = weightage
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    fun clearAllFilters() {
        _searchQuery.value = ""
        _selectedSubject.value = "ALL"
        _selectedClassLevel.value = "ALL"
        _selectedWeightage.value = "ALL"
    }
}
