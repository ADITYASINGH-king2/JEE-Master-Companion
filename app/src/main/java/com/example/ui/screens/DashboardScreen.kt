package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "JEE Prep Tracker",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // TOOL: JEE Countdown
                JeeCountdownWidget()
            }

            item {
                // TOOL 1: Adaptive Study Planner
                AdaptiveStudyPlannerWidget()
            }

            item {
                // TOOL 2: Progress & Trend Tracker (Mock Tests & Graph)
                ProgressTrendTrackerWidget()
            }

            item {
                // TOOL 3: Given-To-Find Matcher
                GivenToFindMatcherWidget()
            }

            item {
                // TOOL 4: Physics High-Yield Patterns
                PhysicsPatternsWidget()
            }

            item {
                // TOOL 5: Advanced Derivation Vault
                DerivationVaultWidget()
            }

            item {
                // TOOL 6: NCERT Physics Laws
                NcertLawsWidget()
            }

            item {
                // TOOL 6 (part 2): Native Formula Editor
                JeeFormulaFormatterWidget()
            }

            item {
                // TOOL 7: Organic Chemistry Notebook
                OrganicChemistryNotebookWidget()
            }
        }
    }
}
