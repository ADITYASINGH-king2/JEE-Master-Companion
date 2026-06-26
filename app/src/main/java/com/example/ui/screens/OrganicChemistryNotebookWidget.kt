package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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

data class OrganicReaction(
    val id: String,
    val name: String,
    val category: String, // "Electrophilic Addition", "Nucleophilic Substitution", "Name Reaction"
    val reagent: String,
    val description: String,
    val mechanismSteps: List<String>
)

@Composable
fun OrganicChemistryNotebookWidget() {
    val reactions = remember {
        listOf(
            OrganicReaction(
                id = "r1",
                name = "Grignard Reagent Addition",
                category = "Nucleophilic Addition",
                reagent = "R-MgX in dry ether",
                description = "Grignard reagent adds to carbonyl carbon to form primary, secondary, or tertiary alcohols after acid workup.",
                mechanismSteps = listOf(
                    "Reagent R-Mg-X polarizes: Rδ- acts as a strong nucleophile.",
                    "Nucleophilic attack of R- on the partially positive carbonyl carbon.",
                    "Carbonyl double bond breaks: electrons shift to oxygen to form alkoxide ion.",
                    "Protonation / acid workup: H₃O⁺ neutralizes alkoxide to form stable alcohol."
                )
            ),
            OrganicReaction(
                id = "r2",
                name = "Aldol Condensation",
                category = "Name Reaction",
                reagent = "Dilute NaOH, Heat",
                description = "Carbonyl compounds with alpha-hydrogen undergo dimerization under dilute base, followed by heating to form alpha, beta-unsaturated carbonyls.",
                mechanismSteps = listOf(
                    "Deprotonation: Base OH⁻ abstracts acidic alpha-H to form resonance-stabilized enolate ion.",
                    "Nucleophilic Attack: Enolate ion acts as nucleophile, attacking second carbonyl molecule.",
                    "Protonation: Alkoxide abstracts proton from water, forming beta-hydroxy compound.",
                    "Dehydration: Heating causes elimination of water molecule, forming conjugated double bond."
                )
            ),
            OrganicReaction(
                id = "r3",
                name = "Sn1 Substitution",
                category = "Nucleophilic Substitution",
                reagent = "Polar protic solvent (e.g. H₂O, EtOH)",
                description = "Two-step substitution mechanism involving carbocation intermediate, following first-order kinetics.",
                mechanismSteps = listOf(
                    "Leaving Group Departure (Slow, RDS): Leaving group leaves, forming stable carbocation.",
                    "Carbocation rearrangement (if possible) to maximize stability.",
                    "Nucleophilic Attack (Fast): Weak nucleophile attacks carbocation from either front/back.",
                    "Deprotonation: Solvent removes excess proton to yield neutral product."
                )
            )
        )
    }

    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All") + reactions.map { it.category }.distinct()

    val filteredReactions = if (selectedCategory == "All") reactions else reactions.filter { it.category == selectedCategory }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.tertiaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Chemistry Notebook Icon",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "Organic Chemistry Notebook",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Core mechanism, pathways and reagent reference",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filtering Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Reactions list
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                filteredReactions.forEach { rx ->
                    ReactionCard(reaction = rx)
                }
            }
        }
    }
}

@Composable
fun ReactionCard(reaction: OrganicReaction) {
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
                        text = reaction.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = reaction.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand note icon",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Reagent tag
            Box(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
                        RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Reagent: ${reaction.reagent}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = reaction.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Reaction Mechanism Steps:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    reaction.mechanismSteps.forEachIndexed { idx, step ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 3.dp)
                                    .size(16.dp)
                                    .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${idx + 1}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onTertiary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = step,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
