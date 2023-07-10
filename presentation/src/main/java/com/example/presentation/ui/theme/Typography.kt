package com.example.presentation.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.presentation.R

private val thin = Font(R.font.montserrat_thin, FontWeight.W100)
private val extraLight = Font(R.font.montserrat_extralight, FontWeight.W200)
private val light = Font(R.font.montserrat_light, FontWeight.W300)
private val regular = Font(R.font.montserrat_regular, FontWeight.W400)
private val medium = Font(R.font.montserrat_medium, FontWeight.W500)
private val semibold = Font(R.font.montserrat_semibold, FontWeight.W600)
private val bold = Font(R.font.montserrat_bold, FontWeight.W700)
private val extraBold = Font(R.font.montserrat_extrabold, FontWeight.W800)
private val black = Font(R.font.montserrat_black, FontWeight.W900)

private val appFontFamily =
    FontFamily(fonts = listOf(thin, extraLight, light, regular, medium, semibold, bold, extraBold, black))

internal val baseTypography = AppTypography(
    bold20 = TextStyle(
        fontFamily = appFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
    ),
    bold16 = TextStyle(
        fontFamily = appFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
    ),
    bold12 = TextStyle(
        fontFamily = appFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
    ),
    semiBold12 = TextStyle(
        fontFamily = appFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
    ),
    medium20 = TextStyle(
        fontFamily = appFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
    ),
    medium16 = TextStyle(
        fontFamily = appFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
    ),
    medium12 = TextStyle(
        fontFamily = appFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
    ),
    regular20 = TextStyle(
        fontFamily = appFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
    ),
    regular16 = TextStyle(
        fontFamily = appFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    ),
    regular12 = TextStyle(
        fontFamily = appFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
    ),
)