package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class StudyTask(
    val id: Int,
    val topic: String,
    val subject: String, // "Physics", "Chemistry", "Maths"
    val durationMinutes: Int,
    val difficulty: String, // "High Weightage", "Medium", "Revision"
    val completed: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdaptiveStudyPlannerWidget() {
    var tasks by remember {
        mutableStateOf(
            listOf(
                StudyTask(1, "Rotational Mechanics (Moment of Inertia)", "Physics", 90, "High Weightage"),
                StudyTask(2, "Coordination Compounds (CFT & Isomerism)", "Chemistry", 60, "High Weightage"),
                StudyTask(3, "Indefinite Integration (Special Forms)", "Maths", 75, "Medium"),
                StudyTask(4, "Thermodynamics Revision", "Physics", 45, "Revision")
            )
        )
    }

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var newTopic by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf("Physics") }
    var selectedDifficulty by remember { mutableStateOf("High Weightage") }
    var newDuration by remember { mutableStateOf("60") }

    val completedTasks = tasks.count { it.completed }
    val progress = if (tasks.isEmpty()) 0f else completedTasks.toFloat() / tasks.size

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .testTag("study_planner_card"),
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
                        text = "Adaptive Study Planner",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Prioritized JEE preparation scheduler",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                IconButton(
                    onClick = { showAddTaskDialog = true },
                    modifier = Modifier.testTag("add_task_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "Add Task",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Task list
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                tasks.forEach { task ->
                    StudyTaskRow(
                        task = task,
                        onCheckedChange = { isChecked ->
                            tasks = tasks.map {
                                if (it.id == task.id) it.copy(completed = isChecked) else it
                            }
                        },
                        onDelete = {
                            tasks = tasks.filter { it.id != task.id }
                        }
                    )
                }
            }
        }
    }

    if (showAddTaskDialog) {
        AlertDialog(
            onDismissRequest = { showAddTaskDialog = false },
            title = { Text("Add JEE Study Task") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newTopic,
                        onValueChange = { newTopic = it },
                        label = { Text("Topic name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Subject selector
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("Physics", "Chemistry", "Maths").forEach { subject ->
                            FilterChip(
                                selected = selectedSubject == subject,
                                onClick = { selectedSubject = subject },
                                label = { Text(subject) }
                            )
                        }
                    }

                    // Priority / Weightage selector
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("High Weightage", "Medium", "Revision").forEach { diff ->
                            FilterChip(
                                selected = selectedDifficulty == diff,
                                onClick = { selectedDifficulty = diff },
                                label = { Text(diff) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = newDuration,
                        onValueChange = { newDuration = it },
                        label = { Text("Duration (minutes)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTopic.isNotBlank()) {
                            val durationVal = newDuration.toIntOrNull() ?: 60
                            tasks = tasks + StudyTask(
                                id = (tasks.maxOfOrNull { it.id } ?: 0) + 1,
                                topic = newTopic,
                                subject = selectedSubject,
                                durationMinutes = durationVal,
                                difficulty = selectedDifficulty
                            )
                            newTopic = ""
                            showAddTaskDialog = false
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTaskDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun StudyTaskRow(
    task: StudyTask,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val subjectColor = when (task.subject) {
        "Physics" -> MaterialTheme.colorScheme.primary
        "Chemistry" -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.tertiary
    }

    val difficultyBg = when (task.difficulty) {
        "High Weightage" -> MaterialTheme.colorScheme.errorContainer
        "Medium" -> MaterialTheme.colorScheme.warnContainer()
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val difficultyText = when (task.difficulty) {
        "High Weightage" -> MaterialTheme.colorScheme.onErrorContainer
        "Medium" -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.completed) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.completed,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.testTag("task_checkbox_${task.id}")
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.topic,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        textDecoration = if (task.completed) TextDecoration.LineThrough else null,
                        fontWeight = if (task.completed) FontWeight.Normal else FontWeight.SemiBold
                    ),
                    color = if (task.completed) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Subject tag
                    Box(
                        modifier = Modifier
                            .background(subjectColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = task.subject,
                            style = MaterialTheme.typography.labelSmall,
                            color = subjectColor,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Weightage tag
                    Box(
                        modifier = Modifier
                            .background(difficultyBg, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = task.difficulty,
                            style = MaterialTheme.typography.labelSmall,
                            color = difficultyText,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Duration tag
                    Text(
                        text = "⏱️ ${task.durationMinutes}m",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete task",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun ColorScheme.warnContainer(): Color {
    return Color(0xFFFFECB3) // Yellow/amber background for medium difficulty
}
