package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class MockTestRecord(
    val testId: String,
    val date: String,
    val physicsScore: Int,
    val chemistryScore: Int,
    val mathsScore: Int,
    val totalScore: Int,
    val percentile: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressTrendTrackerWidget() {
    var mockTests by remember {
        mutableStateOf(
            listOf(
                MockTestRecord("AITS-1", "Jan 10", 65, 72, 58, 195, 98.2),
                MockTestRecord("AITS-2", "Jan 25", 70, 68, 62, 200, 98.5),
                MockTestRecord("AITS-3", "Feb 12", 75, 80, 71, 226, 99.1),
                MockTestRecord("AITS-4", "Mar 05", 82, 78, 85, 245, 99.6),
                MockTestRecord("AITS-5", "Mar 20", 88, 85, 92, 265, 99.8)
            )
        )
    }

    var selectedIndex by remember { mutableStateOf(mockTests.lastIndex) }
    var showAddTestDialog by remember { mutableStateOf(false) }

    // Dialog input fields
    var testName by remember { mutableStateOf("") }
    var pScore by remember { mutableStateOf("") }
    var cScore by remember { mutableStateOf("") }
    var mScore by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Progress & Trend Tracker",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Visualizing JEE Mock Test Performance",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                IconButton(onClick = { showAddTestDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add test score",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Custom Line Chart
            val primaryColor = MaterialTheme.colorScheme.primary
            val secondaryColor = MaterialTheme.colorScheme.secondary
            val tertiaryColor = MaterialTheme.colorScheme.tertiary
            val onSurfaceColor = MaterialTheme.colorScheme.onSurface

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(onSurfaceColor.copy(alpha = 0.03f), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(mockTests) {
                            detectTapGestures { offset ->
                                val width = size.width
                                val stepX = width / (mockTests.size - 1).coerceAtLeast(1)
                                val index = (offset.x / stepX).plus(0.5f).toInt().coerceIn(0, mockTests.lastIndex)
                                selectedIndex = index
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height

                    // Max total score in JEE Main is 300
                    val maxScore = 300f
                    val stepX = width / (mockTests.size - 1).coerceAtLeast(1)

                    // Draw grid lines
                    for (i in 1..4) {
                        val y = height * (i / 5f)
                        drawLine(
                            color = onSurfaceColor.copy(alpha = 0.08f),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Build path for scores
                    val points = mockTests.mapIndexed { idx, test ->
                        val x = idx * stepX
                        val y = height - (test.totalScore / maxScore * height)
                        Offset(x, y)
                    }

                    // Draw line
                    val path = Path().apply {
                        if (points.isNotEmpty()) {
                            moveTo(points[0].x, points[0].y)
                            for (i in 1 until points.size) {
                                lineTo(points[i].x, points[i].y)
                            }
                        }
                    }

                    drawPath(
                        path = path,
                        color = primaryColor,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw selection circle and highlight
                    points.forEachIndexed { idx, point ->
                        val isSelected = idx == selectedIndex
                        drawCircle(
                            color = if (isSelected) primaryColor else primaryColor.copy(alpha = 0.4f),
                            radius = if (isSelected) 7.dp.toPx() else 4.dp.toPx(),
                            center = point
                        )

                        if (isSelected) {
                            drawLine(
                                color = primaryColor.copy(alpha = 0.3f),
                                start = Offset(point.x, 0f),
                                end = Offset(point.x, height),
                                strokeWidth = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Display Info for selected record
            if (selectedIndex in mockTests.indices) {
                val selected = mockTests[selectedIndex]
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Selected: ${selected.testId}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Percentile: ${selected.percentile}%",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ScoreItem("Physics", selected.physicsScore, primaryColor)
                            ScoreItem("Chemistry", selected.chemistryScore, secondaryColor)
                            ScoreItem("Maths", selected.mathsScore, tertiaryColor)
                            ScoreItem("Total", selected.totalScore, onSurfaceColor, isTotal = true)
                        }
                    }
                }
            }
        }
    }

    if (showAddTestDialog) {
        AlertDialog(
            onDismissRequest = { showAddTestDialog = false },
            title = { Text("Log Mock Test Score") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = testName,
                        onValueChange = { testName = it },
                        label = { Text("Test Name (e.g. AITS-6)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = pScore,
                        onValueChange = { pScore = it },
                        label = { Text("Physics Score (Max 100)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = cScore,
                        onValueChange = { cScore = it },
                        label = { Text("Chemistry Score (Max 100)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = mScore,
                        onValueChange = { mScore = it },
                        label = { Text("Maths Score (Max 100)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val p = pScore.toIntOrNull() ?: 0
                        val c = cScore.toIntOrNull() ?: 0
                        val m = mScore.toIntOrNull() ?: 0
                        val tot = p + c + m
                        val pCent = 95.0 + (tot / 300.0 * 4.9) // Simple calculation for placeholder percentile
                        if (testName.isNotBlank()) {
                            mockTests = mockTests + MockTestRecord(
                                testId = testName,
                                date = "Today",
                                physicsScore = p,
                                chemistryScore = c,
                                mathsScore = m,
                                totalScore = tot,
                                percentile = Math.round(pCent * 10.0) / 10.0
                            )
                            selectedIndex = mockTests.lastIndex
                            showAddTestDialog = false
                            testName = ""
                            pScore = ""
                            cScore = ""
                            mScore = ""
                        }
                    }
                ) {
                    Text("Add Score")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTestDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ScoreItem(subject: String, score: Int, color: Color, isTotal: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = subject,
            style = MaterialTheme.typography.bodySmall,
            color = if (isTotal) color else color.copy(alpha = 0.8f),
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = "$score",
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
