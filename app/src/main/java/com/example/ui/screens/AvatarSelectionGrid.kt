package com.example.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Predefined list of student-friendly vector icons to represent student avatars.
 */
data class AvatarOption(
    val id: String,
    val icon: ImageVector,
    val label: String
)

val avatarOptionsList = listOf(
    AvatarOption("person", Icons.Default.Person, "Aspirant"),
    AvatarOption("face", Icons.Default.Face, "Scholar"),
    AvatarOption("star", Icons.Default.Star, "Topper"),
    AvatarOption("favorite", Icons.Default.Favorite, "Passion"),
    AvatarOption("thumb_up", Icons.Default.ThumbUp, "Achiever"),
    AvatarOption("home", Icons.Default.Home, "Steady"),
    AvatarOption("lock", Icons.Default.Lock, "Secure"),
    AvatarOption("edit", Icons.Default.Edit, "Writer"),
    AvatarOption("settings", Icons.Default.Settings, "Planner"),
    AvatarOption("info", Icons.Default.Info, "Helper")
)

fun getAvatarIconByIndex(index: Int): ImageVector {
    val safeIndex = index.coerceIn(0, avatarOptionsList.lastIndex)
    return avatarOptionsList[safeIndex].icon
}

fun getAvatarIdByIndex(index: Int): String {
    val safeIndex = index.coerceIn(0, avatarOptionsList.lastIndex)
    return avatarOptionsList[safeIndex].id
}

fun getAvatarIndexById(id: String): Int {
    val index = avatarOptionsList.indexOfFirst { it.id == id }
    return if (index != -1) index else 0
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AvatarSelectionGrid(
    selectedAvatarId: String,
    onAvatarSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val avatars = remember { avatarOptionsList }

    // App's Electric Blue Accent color & unselected container colors
    val electricBlue = Color(0xFF00E5FF)
    val defaultUnselectedBorder = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("avatar_selection_grid_container")
    ) {
        Text(
            text = "Select Study Avatar",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 68.dp),
            contentPadding = PaddingValues(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp) // Bound height to avoid nested LazyColumn scroll conflict
                .testTag("avatar_lazy_grid")
        ) {
            itemsIndexed(avatars, key = { _, item -> item.id }) { _, avatar ->
                val isSelected = avatar.id == selectedAvatarId

                // Visual States (Highlight container & desaturate unselected icons)
                val borderStrokeColor = if (isSelected) electricBlue else defaultUnselectedBorder
                val borderThickness = if (isSelected) 3.dp else 1.dp
                val cardAlpha = if (isSelected) 1.0f else 0.5f
                val containerColor = if (isSelected) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = containerColor),
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = borderThickness,
                            color = borderStrokeColor,
                            shape = RoundedCornerShape(16.dp)
                        )
                        // Interactive Ripple feedback on click
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onAvatarSelected(avatar.id) }
                        .alpha(cardAlpha)
                        .testTag("avatar_card_${avatar.id}")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = avatar.icon,
                            contentDescription = avatar.label,
                            tint = if (isSelected) electricBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("avatar_icon_${avatar.id}")
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = avatar.label,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) electricBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
