package com.example.presentation.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

data class AppColors(
    val accent: Color,
    val accentBackground: Color,
    val primaryBackground: Color,
    val primary: Color,
    val secondaryBackground: Color,
    val secondary: Color
)

data class AppTypography(
    val bold20: TextStyle,
    val bold16: TextStyle,
    val bold12: TextStyle,
    val semiBold12: TextStyle,
    val medium20: TextStyle,
    val medium16: TextStyle,
    val medium12: TextStyle,
    val regular20: TextStyle,
    val regular16: TextStyle,
    val regular12: TextStyle
)

object AppTheme {
    val colors: AppColors
        @Composable
        get() = LocalAppColors.current

    val typography: AppTypography
        @Composable
        get() = LocalAppTypography.current
}

internal val LocalAppColors = staticCompositionLocalOf<AppColors> {
    error("No colors provided")
}

internal val LocalAppTypography = staticCompositionLocalOf<AppTypography> {
    error("No font provided")
}