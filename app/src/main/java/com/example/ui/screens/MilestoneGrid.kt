package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.viewmodel.BadgeState
import com.example.ui.viewmodel.MilestoneViewModel

/**
 * MilestoneGrid - Visual catalog of unlocked achievements and rewards.
 * Engineered with recomposition-lag protection, dynamic neon-glow frame status indicators,
 * and immersive haptic response cycles.
 */
@Composable
fun MilestoneGrid(
    modifier: Modifier = Modifier,
    viewModel: MilestoneViewModel = viewModel()
) {
    // Observe reactive badge states combined from Room database
    val badgesList: List<BadgeState> by viewModel.badges.collectAsState()
    val selectedBadge by viewModel.selectedBadge.collectAsState()
    val hapticFeedback = LocalHapticFeedback.current

    // Unlocked and locked counts for stats header
    val unlockedCount = badgesList.count { it.isUnlocked }
    val totalCount = badgesList.size

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats Header Banner Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "My Achievements",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Complete JEE challenges & practice sets to unlock premium medals.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = "$unlockedCount/$totalCount",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = "Unlocked",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                            fontSize = 9.sp
                        )
                    }
                }
            }

            // Milestone Layout Grid
            // List Recomposition Lag Fix: Wrap badges collection in an explicit Kotlin List
            // and use unique, stable keys inside items() to isolate compositions.
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .testTag("milestone_grid_container")
            ) {
                items(
                    items = badgesList,
                    key = { it.id } // Stable String ID isolates state changes to individual cards
                ) { badge ->
                    BadgeCard(
                        badge = badge,
                        onClick = {
                            // Haptic click triggered only for unlocked badges to reward completion
                            if (badge.isUnlocked) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            viewModel.selectBadge(badge)
                        }
                    )
                }
            }
        }

        // Animated overlay showing detail description when selected
        AnimatedVisibility(
            visible = selectedBadge != null,
            enter = fadeIn() + scaleIn(initialScale = 0.9f),
            exit = fadeOut() + scaleOut(targetScale = 0.9f)
        ) {
            selectedBadge?.let { badge ->
                BadgeDetailsDialog(
                    badge = badge,
                    onDismiss = { viewModel.selectBadge(null) },
                    onToggleClaim = {
                        if (badge.isUnlocked) {
                            viewModel.lockBadge(badge.id)
                        } else {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.claimBadge(badge.id)
                        }
                    }
                )
            }
        }
    }
}

/**
 * BadgeCard - Single grid cell representing locked or unlocked state.
 */
@Composable
fun BadgeCard(
    badge: BadgeState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Vibrant Neon Blue or Electric Gold highlights for unlocked cards
    val borderColors = if (badge.id == "jee_conqueror" || badge.id == "mock_warrior") {
        listOf(Color(0xFFFFD700), Color(0xFFFF9500)) // Electric Gold
    } else {
        listOf(Color(0xFF00E5FF), Color(0xFF0055FF)) // Vibrant Neon Blue
    }

    val cardBorder = if (badge.isUnlocked) {
        BorderStroke(1.5.dp, Brush.horizontalGradient(borderColors))
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    }

    val cardAlpha = if (badge.isUnlocked) 1.0f else 0.4f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.95f)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("badge_card_${badge.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (badge.isUnlocked) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)
            }
        ),
        border = cardBorder
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .alpha(cardAlpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon visual framework
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        if (badge.isUnlocked) {
                            Brush.radialGradient(
                                colors = listOf(
                                    borderColors.first().copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            )
                        } else {
                            Brush.radialGradient(colors = listOf(Color.Transparent, Color.Transparent))
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (badge.isUnlocked) badge.iconVector else Icons.Default.Lock,
                    contentDescription = badge.title,
                    tint = if (badge.isUnlocked) borderColors.first() else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = badge.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = badge.subtitle,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 1
            )
        }
    }
}

/**
 * BadgeDetailsDialog - Highly polished dialog detailing achievement requirements and diagnostics.
 */
@Composable
fun BadgeDetailsDialog(
    badge: BadgeState,
    onDismiss: () -> Unit,
    onToggleClaim: () -> Unit
) {
    val themeColor = if (badge.id == "jee_conqueror" || badge.id == "mock_warrior") {
        Color(0xFFFFD700) // Electric Gold
    } else {
        Color(0xFF00E5FF) // Neon Blue
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, themeColor.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                .testTag("badge_details_dialog"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close detailed description",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }

                // Glowing circular preview icon
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    themeColor.copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            )
                        )
                        .border(2.dp, themeColor.copy(alpha = 0.8f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (badge.isUnlocked) badge.iconVector else Icons.Default.Lock,
                        contentDescription = badge.title,
                        tint = if (badge.isUnlocked) themeColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        modifier = Modifier.size(48.dp)
                    )
                }

                // Metadata Details
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = badge.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = badge.subtitle,
                        style = MaterialTheme.typography.titleSmall,
                        color = themeColor,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                // Main Description Body
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Milestone Details",
                            tint = themeColor
                        )
                        Text(
                            text = badge.description,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Status banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (badge.isUnlocked) Icons.Default.CheckCircle else Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (badge.isUnlocked) themeColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (badge.isUnlocked) "Milestone Unlocked & Completed" else "Requirements Unmet (Locked)",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = if (badge.isUnlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Developer actions / manual overrides
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Close", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onToggleClaim,
                        modifier = Modifier.weight(1f).testTag("toggle_claim_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (badge.isUnlocked) MaterialTheme.colorScheme.error else themeColor,
                            contentColor = if (badge.isUnlocked) MaterialTheme.colorScheme.onError else Color.Black
                        )
                    ) {
                        Text(
                            text = if (badge.isUnlocked) "Lock Achievement" else "Unlock Instantly",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
