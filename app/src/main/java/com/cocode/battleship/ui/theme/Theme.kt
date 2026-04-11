package com.cocode.battleship.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val NavalColorScheme = darkColorScheme(
    primary = SonarCyan,
    onPrimary = DeepNavy,
    primaryContainer = SonarCyanContainer,
    onPrimaryContainer = SonarCyan,
    secondary = PhosphorGreen,
    onSecondary = DeepNavy,
    secondaryContainer = PhosphorGreenDim,
    onSecondaryContainer = PhosphorGreen,
    tertiary = AmberWarning,
    onTertiary = DeepNavy,
    tertiaryContainer = AmberDim,
    onTertiaryContainer = AmberWarning,
    error = TorpedoRed,
    onError = DeepNavy,
    errorContainer = TorpedoRedDim,
    onErrorContainer = TorpedoRed,
    background = DeepNavy,
    onBackground = TextPrimary,
    surface = NavySurface,
    onSurface = TextPrimary,
    surfaceVariant = NavyCard,
    onSurfaceVariant = TextSecondary,
    outline = NavyBorder,
    outlineVariant = TextDim,
    inverseSurface = TextPrimary,
    inverseOnSurface = DeepNavy,
    inversePrimary = SonarCyanDim,
)

@Composable
fun BattleshipTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NavalColorScheme,
        typography = Typography,
        content = content
    )
}
