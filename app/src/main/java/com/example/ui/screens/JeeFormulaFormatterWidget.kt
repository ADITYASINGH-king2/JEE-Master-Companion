package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.JeeFormulaFormatter

data class SavedFormula(
    val id: Int,
    val title: String,
    val raw: String,
    val formatted: String
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JeeFormulaFormatterWidget() {
    var titleInput by remember { mutableStateOf("") }
    var rawInput by remember { mutableStateOf("") }

    val formattedPreview = remember(rawInput) {
        JeeFormulaFormatter.formatSymbols(rawInput)
    }

    var savedFormulas by remember {
        mutableStateOf(
            listOf(
                SavedFormula(1, "E-Field of point charge", "E = k * q / r^2", "E = k * q / r²"),
                SavedFormula(2, "Phase difference", "delta_phi = (2 * pi / lambda) * delta_x", "Δ_φ = (2 * π / λ) * Δ_x")
            )
        )
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
                text = "Native Formula Editor",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Type text equations to dynamically format into pretty Unicode matrices & Greek scripts",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Editor Block
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = titleInput,
                    onValueChange = { titleInput = it },
                    label = { Text("Formula title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = rawInput,
                    onValueChange = { rawInput = it },
                    label = { Text("Formula (e.g., alpha + beta = theta)") },
                    placeholder = { Text("Use pi, theta, lambda, alpha, delta, ^2, sqrt") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Formatting hints row
                Text(
                    text = "Supported: alpha -> α, theta -> θ, lambda -> λ, pi -> π, ^2 -> ², sqrt -> √",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )

                // Live Preview Block
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "LIVE RENDER PREVIEW",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (formattedPreview.isBlank()) "(Start typing...)" else formattedPreview,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                Button(
                    onClick = {
                        if (titleInput.isNotBlank() && rawInput.isNotBlank()) {
                            savedFormulas = savedFormulas + SavedFormula(
                                id = (savedFormulas.maxOfOrNull { it.id } ?: 0) + 1,
                                title = titleInput,
                                raw = rawInput,
                                formatted = formattedPreview
                            )
                            titleInput = ""
                            rawInput = ""
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Save Formula")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Save Formula")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Saved Custom Formulas
            Text(
                text = "My Custom Saved Formulas:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                savedFormulas.forEach { f ->
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = f.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = f.formatted,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
