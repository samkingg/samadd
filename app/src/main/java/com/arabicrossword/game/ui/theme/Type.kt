package com.arabicrossword.game.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.sp
import com.arabicrossword.game.R

// Arabic fonts - using system fonts for now, can be replaced with custom fonts
val ArabicFontFamily = FontFamily.Default

val ArabicTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = ArabicFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
        textDirection = TextDirection.Rtl,
        textAlign = TextAlign.Center
    ),
    displayMedium = TextStyle(
        fontFamily = ArabicFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp,
        textDirection = TextDirection.Rtl,
        textAlign = TextAlign.Center
    ),
    displaySmall = TextStyle(
        fontFamily = ArabicFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp,
        textDirection = TextDirection.Rtl,
        textAlign = TextAlign.Center
    ),
    headlineLarge = TextStyle(
        fontFamily = ArabicFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
        textDirection = TextDirection.Rtl,
        textAlign = TextAlign.Start
    ),
    headlineMedium = TextStyle(
        fontFamily = ArabicFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
        textDirection = TextDirection.Rtl,
        textAlign = TextAlign.Start
    ),
    headlineSmall = TextStyle(
        fontFamily = ArabicFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
        textDirection = TextDirection.Rtl,
        textAlign = TextAlign.Start
    ),
    titleLarge = TextStyle(
        fontFamily = ArabicFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
        textDirection = TextDirection.Rtl,
        textAlign = TextAlign.Start
    ),
    titleMedium = TextStyle(
        fontFamily = ArabicFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
        textDirection = TextDirection.Rtl,
        textAlign = TextAlign.Start
    ),
    titleSmall = TextStyle(
        fontFamily = ArabicFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        textDirection = TextDirection.Rtl,
        textAlign = TextAlign.Start
    ),
    bodyLarge = TextStyle(
        fontFamily = ArabicFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        textDirection = TextDirection.Rtl,
        textAlign = TextAlign.Start
    ),
    bodyMedium = TextStyle(
        fontFamily = ArabicFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
        textDirection = TextDirection.Rtl,
        textAlign = TextAlign.Start
    ),
    bodySmall = TextStyle(
        fontFamily = ArabicFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
        textDirection = TextDirection.Rtl,
        textAlign = TextAlign.Start
    ),
    labelLarge = TextStyle(
        fontFamily = ArabicFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        textDirection = TextDirection.Rtl,
        textAlign = TextAlign.Center
    ),
    labelMedium = TextStyle(
        fontFamily = ArabicFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        textDirection = TextDirection.Rtl,
        textAlign = TextAlign.Center
    ),
    labelSmall = TextStyle(
        fontFamily = ArabicFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        textDirection = TextDirection.Rtl,
        textAlign = TextAlign.Center
    )
)

// Special styles for crossword grid
val CrosswordCellTextStyle = TextStyle(
    fontFamily = ArabicFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 18.sp,
    textAlign = TextAlign.Center,
    textDirection = TextDirection.Rtl
)

val ClueNumberTextStyle = TextStyle(
    fontFamily = ArabicFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 10.sp,
    textAlign = TextAlign.Center
)