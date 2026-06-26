package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * QuizQuestion represents a dynamic mock JEE question.
 */
data class QuizQuestion(
    val id: Int,
    val subject: String,
    val questionText: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String
)

@Composable
fun QuizzesScreen(
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    val questions = remember {
        listOf(
            QuizQuestion(
                1,
                "PHYSICS",
                "A parallel plate capacitor is charged and then disconnected from the source. If the distance between the plates is doubled, how does the electric energy density change?",
                listOf(
                    "Increases by a factor of 4",
                    "Doubles",
                    "Remains the same",
                    "Decreases by a factor of 2"
                ),
                2,
                "Since energy density u = 1/2 * ε0 * E^2, and E = Q / (A * ε0). Since charging source is disconnected, Q remains constant. Doubling the distance does not affect the electric field E, hence energy density remains the same."
            ),
            QuizQuestion(
                2,
                "CHEMISTRY",
                "Which of the following coordinates represents the geometry of XeF4 according to VSEPR theory?",
                listOf(
                    "Tetrahedral",
                    "Square planar",
                    "Octahedral",
                    "Trigonal bipyramidal"
                ),
                1,
                "XeF4 has 8 valence electrons from Xe + 4 from F = 12 total (6 electron pairs). 4 bonding pairs and 2 lone pairs result in sp3d2 hybridization and a square planar geometry."
            ),
            QuizQuestion(
                3,
                "MATHEMATICS",
                "What is the value of the integral ∫_{-1}^{1} |x| dx?",
                listOf(
                    "0",
                    "1/2",
                    "1",
                    "2"
                ),
                2,
                "The function |x| is even. Thus, ∫_{-1}^{1} |x| dx = 2 * ∫_{0}^{1} x dx = 2 * [x^2 / 2]_{0}^{1} = 1."
            ),
            QuizQuestion(
                4,
                "PHYSICS",
                "A satellite is revolving close to the earth's surface. What minimum percentage increase in its velocity is required so that it escapes from the gravitational field?",
                listOf(
                    "21%",
                    "41.4%",
                    "50%",
                    "100%"
                ),
                1,
                "Orbital velocity is v_o = √(GM/R). Escape velocity is v_e = √(2GM/R) = √2 * v_o. The fractional increase is √2 - 1 ≈ 0.414, which corresponds to an increase of 41.4%."
            )
        )
    }

    var selectedAnswers by remember { mutableStateOf(mapOf<Int, Int>()) }
    var score by remember { mutableStateOf(0) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Daily Quiz Leaderboard Header
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "JEE Daily Mini-Quizzes",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Instant performance analytics diagnostics",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Quiz Hub",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Score Tracker",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "$score / ${questions.size}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = {
                                selectedAnswers = emptyMap()
                                score = 0
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.align(Alignment.CenterVertically)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reset Quiz")
                        }
                    }
                }
            }
        }

        // List of quiz questions
        itemsIndexed(questions) { index, question ->
            val selectedOption = selectedAnswers[question.id]
            val isAnswered = selectedOption != null

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quiz_card_${question.id}")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when (question.subject) {
                                "PHYSICS" -> Color(0xFF00E5FF).copy(alpha = 0.15f)
                                "CHEMISTRY" -> Color(0xFFFF9100).copy(alpha = 0.15f)
                                else -> Color(0xFFD500F9).copy(alpha = 0.15f)
                            },
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Text(
                                text = question.subject,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = when (question.subject) {
                                    "PHYSICS" -> Color(0xFF00E5FF)
                                    "CHEMISTRY" -> Color(0xFFFF9100)
                                    else -> Color(0xFFD500F9)
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Text(
                            text = "Q. ${index + 1}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }

                    Text(
                        text = question.questionText,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        question.options.forEachIndexed { optIndex, option ->
                            val isCurrentSelected = selectedOption == optIndex
                            val isCorrectAnswer = optIndex == question.correctOptionIndex

                            val borderStrokeColor = when {
                                isCurrentSelected && isCorrectAnswer -> Color.Green
                                isCurrentSelected && !isCorrectAnswer -> MaterialTheme.colorScheme.error
                                isAnswered && isCorrectAnswer -> Color.Green.copy(alpha = 0.5f)
                                else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                            }

                            val backgroundTint = when {
                                isCurrentSelected && isCorrectAnswer -> Color.Green.copy(alpha = 0.1f)
                                isCurrentSelected && !isCorrectAnswer -> MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                                else -> MaterialTheme.colorScheme.surface
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        width = if (isCurrentSelected) 2.dp else 1.dp,
                                        color = borderStrokeColor,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(backgroundTint)
                                    .clickable(enabled = !isAnswered) {
                                        // Haptic on answer selection
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)

                                        val updated = selectedAnswers.toMutableMap()
                                        updated[question.id] = optIndex
                                        selectedAnswers = updated

                                        if (optIndex == question.correctOptionIndex) {
                                            score += 1
                                        }
                                    }
                                    .padding(14.dp)
                                    .testTag("quiz_option_${question.id}_$optIndex")
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .border(
                                            1.5.dp,
                                            borderStrokeColor,
                                            CircleShape
                                        )
                                        .background(
                                            if (isCurrentSelected) borderStrokeColor.copy(alpha = 0.2f) else Color.Transparent
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isAnswered && isCorrectAnswer) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Correct",
                                            tint = Color.Green,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    } else if (isCurrentSelected && !isCorrectAnswer) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Incorrect",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    } else {
                                        Text(
                                            text = ('A'.code + optIndex).toChar().toString(),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    AnimatedVisibility(visible = isAnswered) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                                .background(
                                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Performance Solution & Analytics:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = question.explanation,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
