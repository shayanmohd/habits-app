package com.mohdshayan.habits.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightScheme = lightColorScheme(
    primary = TextPrimary,
    onPrimary = BgPaper,
    surface = BgPaper,
    background = BgPaper,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    secondary = TextSecondary,
    error = MissedRed
)

@Composable
fun HabitTrackTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightScheme,
        typography = AppTypography,
        content = content
    )
}
