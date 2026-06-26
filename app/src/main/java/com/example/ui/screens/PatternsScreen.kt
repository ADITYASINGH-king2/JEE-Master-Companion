package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.db.AppDatabase
import com.example.ui.viewmodel.HighYieldPattern
import com.example.ui.viewmodel.PatternsViewModel

/**
 * PatternsScreen - JEE high-yield repeated patterns repository & diagnostic trends dashboard.
 * Refactored by Jetpack Compose UI Expert to implement robust MVVM state management,
 * Material 3 filter chips, modal bottom sheet sorting, and full LaTeX-free mathematical equations.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatternsScreen(
    modifier: Modifier = Modifier,
    viewModel: PatternsViewModel = viewModel()
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val testHistories by database.mockTestHistoryDao().getAllTestHistoriesFlow().collectAsState(initial = emptyList())

    // Tabs for switching between high-yield pattern repository and diagnostic analytics
    var selectedTabState by remember { mutableStateOf(0) }
    val tabTitles = listOf("High-Yield Patterns", "Diagnostics & Trends")

    // State bindings from PatternsViewModel
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val selectedClassLevel by viewModel.selectedClassLevel.collectAsState()
    val selectedWeightage by viewModel.selectedWeightage.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    val filteredPatterns by viewModel.filteredPatterns.collectAsState()

    // Bottom sheet state for sorting
    var showSortBottomSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tab row header
        TabRow(
            selectedTabIndex = selectedTabState,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("patterns_tab_row")
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabState == index,
                    onClick = { selectedTabState = index },
                    text = { Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp) },
                    modifier = Modifier.testTag("patterns_tab_$index")
                )
            }
        }

        if (selectedTabState == 0) {
            // High-Yield Pattern Repository View
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Search bar and Sort trigger row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Search chapters or patterns...") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("patterns_search_field"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    // Material 3 Sorting Button
                    IconButton(
                        onClick = { showSortBottomSheet = true },
                        modifier = Modifier
                            .size(52.dp)
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                            .testTag("patterns_sort_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = "Sort Patterns",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Filter Section: Horizontal scrolling rows of FilterChips
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Category 1: Subject
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Subject:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.width(64.dp)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val subjects = listOf("ALL", "PHYSICS", "CHEMISTRY", "MATHEMATICS")
                            items(subjects) { subject ->
                                FilterChip(
                                    selected = selectedSubject == subject,
                                    onClick = { viewModel.setSelectedSubject(subject) },
                                    label = { Text(subject, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    modifier = Modifier.testTag("subject_chip_$subject")
                                )
                            }
                        }
                    }

                    // Category 2: Class level
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Class:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.width(64.dp)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val classes = listOf("ALL", "11TH", "12TH")
                            items(classes) { classLvl ->
                                FilterChip(
                                    selected = selectedClassLevel == classLvl,
                                    onClick = { viewModel.setSelectedClassLevel(classLvl) },
                                    label = { Text(classLvl, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                                    ),
                                    modifier = Modifier.testTag("class_chip_$classLvl")
                                )
                            }
                        }
                    }

                    // Category 3: Weightage
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Weight:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.width(64.dp)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val weightages = listOf("ALL", "CRITICAL", "HIGH", "MEDIUM")
                            items(weightages) { weight ->
                                FilterChip(
                                    selected = selectedWeightage == weight,
                                    onClick = { viewModel.setSelectedWeightage(weight) },
                                    label = { Text(weight, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer
                                    ),
                                    modifier = Modifier.testTag("weight_chip_$weight")
                                )
                            }
                        }
                    }
                }

                // Highly optimized LazyColumn with unique keys
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .testTag("patterns_lazy_column")
                ) {
                    if (filteredPatterns.isEmpty()) {
                        item {
                            // 4. Empty State UI with clear filters prompt
                            Card(
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp)
                                    .testTag("patterns_empty_state")
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = "No Results",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))

                                    Text(
                                        text = "No patterns found for this specific filter combination.",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Try adjusting your Subject, Class level, or Weightage filters to discover other high-yield recurring JEE patterns.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(20.dp))

                                    Button(
                                        onClick = { viewModel.clearAllFilters() },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.testTag("clear_filters_button")
                                    ) {
                                        Text("Clear All Filters", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    } else {
                        items(
                            items = filteredPatterns,
                            key = { it.id }
                        ) { pattern ->
                            PatternCard(pattern = pattern)
                        }
                    }
                }
            }
        } else {
            // Diagnostics & Trends View
            val totalTests = testHistories.size
            val averageScore = if (totalTests > 0) testHistories.map { it.rawScore }.average() else 0.0
            val maxScore = if (totalTests > 0) testHistories.maxOf { it.rawScore } else 0
            val averageAccuracy = if (totalTests > 0) testHistories.map { it.accuracyDelta }.average() else 0.0

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Analytics Card
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Text(
                                text = "Diagnostics & Trends",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Total Mock Tests",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                    Text(
                                        text = "$totalTests",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "High Score",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                    Text(
                                        text = "$maxScore / 300",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Average Metric Score: ${String.format("%.1f", averageScore)}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { (averageScore / 300f).toFloat().coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Average Syllabus Accuracy: ${String.format("%.1f", averageAccuracy)}%",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { (averageAccuracy / 100f).toFloat().coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = MaterialTheme.colorScheme.tertiary,
                                trackColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                            )
                        }
                    }
                }

                // Subject mastery indicators (Physics, Chemistry, Maths)
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Subject Core Mastery",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Physics
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Physics (Mechanics, Electrodynamics)", style = MaterialTheme.typography.bodyMedium)
                                    Text(text = "78%", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { 0.78f },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                            // Chemistry
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Chemistry (Physical, Organic)", style = MaterialTheme.typography.bodyMedium)
                                    Text(text = "84%", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { 0.84f },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            // Mathematics
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Mathematics (Calculus, Algebra)", style = MaterialTheme.typography.bodyMedium)
                                    Text(text = "65%", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { 0.65f },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Bottom Sheet for Sorting Options
    if (showSortBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSortBottomSheet = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    text = "Sort Patterns By",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Option 1: Most Frequently Asked
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.setSortOrder(PatternsViewModel.SortOrder.FREQUENCY)
                            showSortBottomSheet = false
                        }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    RadioButton(
                        selected = sortOrder == PatternsViewModel.SortOrder.FREQUENCY,
                        onClick = {
                            viewModel.setSortOrder(PatternsViewModel.SortOrder.FREQUENCY)
                            showSortBottomSheet = false
                        },
                        modifier = Modifier.testTag("sort_frequency_radio")
                    )
                    Column {
                        Text(
                            text = "Most Frequently Asked",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Prioritized by percentage of past JEE appearances",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Custom box divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                        .padding(vertical = 8.dp)
                )

                // Option 2: Easiest to Score
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.setSortOrder(PatternsViewModel.SortOrder.EASIEST_TO_SCORE)
                            showSortBottomSheet = false
                        }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    RadioButton(
                        selected = sortOrder == PatternsViewModel.SortOrder.EASIEST_TO_SCORE,
                        onClick = {
                            viewModel.setSortOrder(PatternsViewModel.SortOrder.EASIEST_TO_SCORE)
                            showSortBottomSheet = false
                        },
                        modifier = Modifier.testTag("sort_easiest_radio")
                    )
                    Column {
                        Text(
                            text = "Easiest to Score",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Prioritized by straightforward application and scoring ease",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * Dynamic mapping function resolving Compose's AnnotatedString without LaTeX for the mathematical equations.
 * Adheres to NO LATEX instruction, applying Unicode symbols and native baseline shifts.
 */
@Composable
fun getFormulaForPattern(patternId: String): androidx.compose.ui.text.AnnotatedString {
    return when (patternId) {
        "pattern_lc_oscillations" -> buildAnnotatedString {
            append("U")
            withStyle(SpanStyle(baselineShift = BaselineShift.Subscript, fontSize = 11.sp)) {
                append("total")
            }
            append(" = (1/2) * Q")
            withStyle(SpanStyle(baselineShift = BaselineShift.Superscript, fontSize = 11.sp)) {
                append("2")
            }
            append(" / C + (1/2) * L * I")
            withStyle(SpanStyle(baselineShift = BaselineShift.Superscript, fontSize = 11.sp)) {
                append("2")
            }
        }
        "pattern_bohr_radii" -> buildAnnotatedString {
            append("r")
            withStyle(SpanStyle(baselineShift = BaselineShift.Subscript, fontSize = 11.sp)) {
                append("n")
            }
            append(" = r")
            withStyle(SpanStyle(baselineShift = BaselineShift.Subscript, fontSize = 11.sp)) {
                append("0")
            }
            append(" * (n")
            withStyle(SpanStyle(baselineShift = BaselineShift.Superscript, fontSize = 11.sp)) {
                append("2")
            }
            append(" / Z) [r")
            withStyle(SpanStyle(baselineShift = BaselineShift.Subscript, fontSize = 11.sp)) {
                append("0")
            }
            append(" = 0.529 Å]")
        }
        "pattern_first_order" -> buildAnnotatedString {
            append("ln([A]")
            withStyle(SpanStyle(baselineShift = BaselineShift.Subscript, fontSize = 11.sp)) {
                append("t")
            }
            append(" / [A]")
            withStyle(SpanStyle(baselineShift = BaselineShift.Subscript, fontSize = 11.sp)) {
                append("0")
            }
            append(") = -k * t  or  t")
            withStyle(SpanStyle(baselineShift = BaselineShift.Subscript, fontSize = 11.sp)) {
                append("1/2")
            }
            append(" = 0.693 / k")
        }
        "pattern_gibbs_free" -> buildAnnotatedString {
            append("ΔG = ΔH - T * ΔS")
        }
        "pattern_kings_property" -> buildAnnotatedString {
            append("∫")
            withStyle(SpanStyle(baselineShift = BaselineShift.Subscript, fontSize = 10.sp)) {
                append("a")
            }
            withStyle(SpanStyle(baselineShift = BaselineShift.Superscript, fontSize = 10.sp)) {
                append("b")
            }
            append(" f(x) dx = ∫")
            withStyle(SpanStyle(baselineShift = BaselineShift.Subscript, fontSize = 10.sp)) {
                append("a")
            }
            withStyle(SpanStyle(baselineShift = BaselineShift.Superscript, fontSize = 10.sp)) {
                append("b")
            }
            append(" f(a + b - x) dx")
        }
        "pattern_adjoint_matrix" -> buildAnnotatedString {
            append("|adj(A)| = |A|")
            withStyle(SpanStyle(baselineShift = BaselineShift.Superscript, fontSize = 11.sp)) {
                append("n-1")
            }
            append("  and  adj(adj(A)) = |A|")
            withStyle(SpanStyle(baselineShift = BaselineShift.Superscript, fontSize = 11.sp)) {
                append("n-2")
            }
            append(" * A")
        }
        "pattern_projectile_motion" -> buildAnnotatedString {
            append("R")
            withStyle(SpanStyle(baselineShift = BaselineShift.Subscript, fontSize = 11.sp)) {
                append("max")
            }
            append(" = u")
            withStyle(SpanStyle(baselineShift = BaselineShift.Superscript, fontSize = 11.sp)) {
                append("2")
            }
            append(" / g  [at θ = 45°]")
        }
        "pattern_gaseous_state" -> buildAnnotatedString {
            append("v")
            withStyle(SpanStyle(baselineShift = BaselineShift.Subscript, fontSize = 11.sp)) {
                append("mp")
            }
            append(" : v")
            withStyle(SpanStyle(baselineShift = BaselineShift.Subscript, fontSize = 11.sp)) {
                append("avg")
            }
            append(" : v")
            withStyle(SpanStyle(baselineShift = BaselineShift.Subscript, fontSize = 11.sp)) {
                append("rms")
            }
            append(" = √(2) : √(8/π) : √(3)")
        }
        "pattern_conic_sections" -> buildAnnotatedString {
            append("y = mx + a/m  [Tangent to y")
            withStyle(SpanStyle(baselineShift = BaselineShift.Superscript, fontSize = 11.sp)) {
                append("2")
            }
            append(" = 4ax]")
        }
        else -> buildAnnotatedString { append("") }
    }
}

/**
 * PatternCard displays information about a single High-Yield repeated concept.
 */
@Composable
fun PatternCard(
    pattern: HighYieldPattern,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .testTag("pattern_card_${pattern.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header: Subject, Chapter title
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when (pattern.subject) {
                            "PHYSICS" -> Color(0xFF00E5FF).copy(alpha = 0.15f)
                            "CHEMISTRY" -> Color(0xFFFF9100).copy(alpha = 0.15f)
                            else -> Color(0xFFD500F9).copy(alpha = 0.15f)
                        }
                    ) {
                        Text(
                            text = pattern.subject,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = when (pattern.subject) {
                                "PHYSICS" -> Color(0xFF00E5FF)
                                "CHEMISTRY" -> Color(0xFFFF9100)
                                else -> Color(0xFFD500F9)
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = "CLASS ${pattern.classLevel}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Weightage badge: Color coded (Red for Critical/High, Orange/Amber for Medium)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (pattern.weightage) {
                        "CRITICAL" -> Color.Red.copy(alpha = 0.15f)
                        "HIGH" -> Color.Red.copy(alpha = 0.12f)
                        else -> Color(0xFFFFC107).copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        text = "${pattern.weightage} WEIGHTAGE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = when (pattern.weightage) {
                            "CRITICAL", "HIGH" -> Color.Red
                            else -> Color(0xFFFF8F00)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = pattern.chapter,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                fontWeight = FontWeight.Bold
            )

            Text(
                text = pattern.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Dynamic equation / formula block (NO LATEX)
            val formula = getFormulaForPattern(pattern.id)
            if (formula.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = formula,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Core Pattern Description
            Text(
                text = pattern.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Frequency indicators
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Frequency",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${pattern.frequency} (${pattern.frequencyPercentage}% Rank)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
