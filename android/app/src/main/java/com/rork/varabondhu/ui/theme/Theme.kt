package com.rork.varabondhu.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val BrandColorScheme = lightColorScheme(
    primary = BrandGreenDeep,
    onPrimary = MintCanvasTop,
    primaryContainer = BrandGreenSoft,
    onPrimaryContainer = Ink,
    secondary = BrandGreen,
    onSecondary = MintCanvasTop,
    background = MintCanvas,
    onBackground = Ink,
    surface = MintCanvas,
    onSurface = Ink,
    surfaceVariant = RoadGrey,
    onSurfaceVariant = InkMuted,
    outline = InkMuted
)

/**
 * VaraBondhu always renders on its pale mint canvas, so the theme deliberately
 * ignores dynamic color and the system dark theme.
 */
@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BrandColorScheme,
        typography = AppTypography,
        content = content
    )
}
