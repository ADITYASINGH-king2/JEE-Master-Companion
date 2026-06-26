package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class FormulaMatch(
    val name: String,
    val relation: String,
    val givenVariables: Set<String>,
    val targetVariable: String,
    val chapter: String,
    val explanation: String
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GivenToFindMatcherWidget() {
    val formulas = remember {
        listOf(
            FormulaMatch("Centripetal Force", "F = m v² / r", setOf("m", "v", "r"), "F", "Circular Motion", "Relates mass, tangential velocity, and circular radius to necessary centripetal force."),
            FormulaMatch("Kinetic Energy", "K.E = ½ m v²", setOf("m", "v"), "K", "Work, Power & Energy", "Calculates translation kinetic energy of a point mass."),
            FormulaMatch("De Broglie Wavelength", "λ = h / (m v)", setOf("m", "v"), "λ", "Modern Physics", "Wave-particle duality wavelength relation using Planck's constant h."),
            FormulaMatch("Cyclotron Radius", "r = m v / (q B)", setOf("m", "v", "q", "B"), "r", "Magnetism", "Path radius of a charged particle performing circular motion in perpendicular magnetic field B."),
            FormulaMatch("Centripetal Acceleration", "a = v² / r", setOf("v", "r"), "a", "Circular Motion", "Radial acceleration pulling towards center of curvature."),
            FormulaMatch("Linear Momentum", "p = m v", setOf("m", "v"), "p", "Laws of Motion", "Calculates total translational linear momentum of a moving body."),
            FormulaMatch("Magnetic Force on Charge", "F = q v B", setOf("q", "v", "B"), "F", "Magnetism", "Lorentz force on charged particle moving through field B at right angles.")
        )
    }

    var selectedGivens by remember { mutableStateOf(setOf<String>()) }
    var searchQuery by remember { mutableStateOf("") }

    val availableVariables = listOf("m", "v", "r", "q", "B", "λ", "F")

    val filteredMatches = formulas.filter { formula ->
        // Matches if it contains any selected givens (or if none selected, matches all)
        val matchesSelected = selectedGivens.isEmpty() || formula.givenVariables.containsAll(selectedGivens)
        val matchesSearch = searchQuery.isEmpty() || formula.name.contains(searchQuery, ignoreCase = true) || formula.chapter.contains(searchQuery, ignoreCase = true)
        matchesSelected && matchesSearch
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Given-To-Find Matcher",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Select variables you know to find the right JEE formula",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search by topic or formula name") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Selection chips
            Text(
                text = "Select Known Variables (Givens):",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                availableVariables.forEach { variable ->
                    val isSelected = selectedGivens.contains(variable)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedGivens = if (isSelected) {
                                selectedGivens - variable
                            } else {
                                selectedGivens + variable
                            }
                        },
                        label = {
                            Text(
                                text = when(variable) {
                                    "m" -> "m (mass)"
                                    "v" -> "v (velocity)"
                                    "r" -> "r (radius)"
                                    "q" -> "q (charge)"
                                    "B" -> "B (magnetic field)"
                                    "λ" -> "λ (wavelength)"
                                    else -> "F (force)"
                                },
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Formula list
            Text(
                text = "Matching High-Yield Formulas (${filteredMatches.size}):",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredMatches.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No formula found with selected parameters.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    filteredMatches.forEach { match ->
                        FormulaMatchCard(match = match)
                    }
                }
            }
        }
    }
}

@Composable
fun FormulaMatchCard(match: FormulaMatch) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = match.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = match.chapter,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = match.relation,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = match.explanation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Requires:",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        match.givenVariables.forEach { variable ->
                            Box(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = variable,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
