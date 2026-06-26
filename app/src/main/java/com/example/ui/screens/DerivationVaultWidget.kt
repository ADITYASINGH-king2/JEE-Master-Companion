package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class DerivationStep(
    val title: String,
    val formula: String,
    val description: String
)

data class DerivationProof(
    val id: String,
    val title: String,
    val subject: String,
    val targetFormula: String,
    val significance: String,
    val steps: List<DerivationStep>
)

@Composable
fun DerivationVaultWidget() {
    val derivations = remember {
        listOf(
            DerivationProof(
                id = "d1",
                title = "Kepler's Third Law (T² ∝ r³)",
                subject = "Gravitation",
                targetFormula = "T² = (4 π² / G M) r³",
                significance = "Proves planetary orbits orbital time square is proportional to semi-major axis cube.",
                steps = listOf(
                    DerivationStep("Balance Gravitational & Centripetal Forces", "G M m / r² = m v² / r", "The gravitational force provides the centripetal acceleration needed for stable circular orbit."),
                    DerivationStep("Solve for Orbital Velocity", "v = √(G M / r)", "Simplify force balance equation to get the expression for constant tangential velocity v."),
                    DerivationStep("Express Time Period T", "T = 2 π r / v", "Time period of single complete orbit equals total circular distance divided by tangential speed v."),
                    DerivationStep("Substitute v into T & Square", "T² = 4 π² r² / (G M / r) = (4 π² / G M) r³", "Square both sides and substitute the expression for v to arrive at Kepler's Third Law.")
                )
            ),
            DerivationProof(
                id = "d2",
                title = "LC Oscillation Frequency Derivation",
                subject = "Electromagnetic Induction",
                targetFormula = "ω_0 = 1 / √(L C)",
                significance = "Defines resonant angular frequency of an ideal inductor-capacitor LC circuit.",
                steps = listOf(
                    DerivationStep("Kirchhoff's Loop Rule", "- L (di/dt) - q/C = 0", "The sum of voltage drops across inductor and capacitor in closed loop equals zero."),
                    DerivationStep("Express Current in terms of Charge", "i = dq/dt => di/dt = d²q/dt²", "Relate current to charge flow rate, translating to secondary differential form."),
                    DerivationStep("Rewrite Differential Equation", "d²q/dt² + (1 / L C) q = 0", "Substitute di/dt into loop rule to yield a simple harmonic oscillator differential equation."),
                    DerivationStep("Compare with SHO and Solve", "ω_0 = 1 / √(L C)", "Compare with standard simple harmonic form d²x/dt² + ω² x = 0 to get the resonant frequency ω_0.")
                )
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
                text = "Advanced Derivation Vault",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Interactive step-by-step proofs for key JEE equations",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            derivations.forEach { proof ->
                DerivationProofCard(proof = proof)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun DerivationProofCard(proof: DerivationProof) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = proof.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = proof.subject,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand proof icon",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = proof.significance,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Main formula display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                        RoundedCornerShape(6.dp)
                    )
                    .padding(10.dp)
            ) {
                Text(
                    text = proof.targetFormula,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Proof Derivation Steps:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    proof.steps.forEachIndexed { index, step ->
                        StepAccordion(index = index, step = step)
                    }
                }
            }
        }
    }
}

@Composable
fun StepAccordion(index: Int, step: DerivationStep) {
    var stepExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { stepExpanded = !stepExpanded }
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(11.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = step.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = if (stepExpanded) "Hide details" else "Show details",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Formula block
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(4.dp)
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = step.formula,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(
                visible = stepExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        text = step.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
