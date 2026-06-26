package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// Bespoke High-Intensity 'Vibrant Palette' for Study Focus

private val DarkStudyColorScheme = darkColorScheme(
    primary = ElectricPurpleDark,
    secondary = NeonBlueDark,
    tertiary = HotPinkAccent,
    background = ObsidianBg,
    surface = CharcoalSurface,
    onPrimary = OnPrimaryDark,
    onSecondary = OnSecondaryDark,
    onTertiary = OnTertiaryDark,
    onBackground = OnBackgroundDark,
    onSurface = OnSurfaceDark,
    primaryContainer = ElectricPurpleDark,
    onPrimaryContainer = OnPrimaryDark,
    secondaryContainer = CharcoalSurface,
    onSecondaryContainer = NeonBlueDark
)

private val LightStudyColorScheme = lightColorScheme(
    primary = ElectricPurpleLight,
    secondary = SapphireBlueLight,
    tertiary = HotPinkLight,
    background = PureWhiteBg,
    surface = CardWhiteSurface,
    onPrimary = OnPrimaryLight,
    onSecondary = OnSecondaryLight,
    onTertiary = OnTertiaryLight,
    onBackground = OnBackgroundLight,
    onSurface = OnSurfaceLight,
    primaryContainer = ElectricPurpleLight,
    onPrimaryContainer = OnPrimaryLight,
    secondaryContainer = CardWhiteSurface,
    onSecondaryContainer = SapphireBlueLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Automated toggling based on isSystemInDarkTheme() / user-override
    val colorScheme = if (darkTheme) {
        DarkStudyColorScheme
    } else {
        LightStudyColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

/**
 * Master Layout Wrapper System
 * Systematically applies 'safeDrawing' window insets padding to the outermost root container,
 * isolating interactive components, inputs, and floating icons from physical hardware notches,
 * camera cutouts, and system-level gesture bars.
 */
@Composable
fun MasterLayoutWrapper(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    MyApplicationTheme {
        Surface(
            modifier = modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}
