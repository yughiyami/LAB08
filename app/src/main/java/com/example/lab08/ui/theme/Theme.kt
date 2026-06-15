package com.example.lab08.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

enum class AppThemeMode { MODERN_BLUE, GREEN, PURPLE, ORANGE }

private val BlueLightScheme = lightColorScheme(
    primary = ModernBluePrimary, secondary = ModernBlueSecondary, tertiary = ModernBlueTertiary
)
private val BlueDarkScheme = darkColorScheme(
    primary = ModernBluePrimaryDark, secondary = ModernBlueSecondary, tertiary = ModernBlueTertiary
)
private val GreenLightScheme = lightColorScheme(
    primary = GreenPrimary, secondary = GreenSecondary, tertiary = GreenTertiary
)
private val GreenDarkScheme = darkColorScheme(
    primary = GreenPrimaryDark, secondary = GreenSecondary, tertiary = GreenTertiary
)
private val PurpleLightScheme = lightColorScheme(
    primary = PurplePrimary, secondary = PurpleSecondary, tertiary = PurpleTertiary
)
private val PurpleDarkScheme = darkColorScheme(
    primary = PurplePrimaryDark, secondary = PurpleSecondary, tertiary = PurpleTertiary
)
private val OrangeLightScheme = lightColorScheme(
    primary = OrangePrimary, secondary = OrangeSecondary, tertiary = OrangeTertiary
)
private val OrangeDarkScheme = darkColorScheme(
    primary = OrangePrimaryDark, secondary = OrangeSecondary, tertiary = OrangeTertiary
)

@Composable
fun LAB08Theme(
    themeMode: AppThemeMode = AppThemeMode.MODERN_BLUE,
    darkMode: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        AppThemeMode.MODERN_BLUE -> if (darkMode) BlueDarkScheme else BlueLightScheme
        AppThemeMode.GREEN -> if (darkMode) GreenDarkScheme else GreenLightScheme
        AppThemeMode.PURPLE -> if (darkMode) PurpleDarkScheme else PurpleLightScheme
        AppThemeMode.ORANGE -> if (darkMode) OrangeDarkScheme else OrangeLightScheme
    }
    val typography = if (themeMode == AppThemeMode.ORANGE) OrangeTypography else Typography

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}
