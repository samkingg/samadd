package com.arabicrossword.game.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Arabic-inspired color palette
private val ArabicGold = Color(0xFFD4AF37)
private val DeepBlue = Color(0xFF1B365D)
private val SkyBlue = Color(0xFF87CEEB)
private val SandBeige = Color(0xFFF5E6D3)
private val DarkGreen = Color(0xFF355E3B)
private val Burgundy = Color(0xFF800020)
private val Pearl = Color(0xFFF8F8FF)

private val LightColorScheme = lightColorScheme(
    primary = DeepBlue,
    onPrimary = Color.White,
    primaryContainer = SkyBlue,
    onPrimaryContainer = DeepBlue,
    secondary = ArabicGold,
    onSecondary = DeepBlue,
    secondaryContainer = SandBeige,
    onSecondaryContainer = DeepBlue,
    tertiary = DarkGreen,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE8F5E8),
    onTertiaryContainer = DarkGreen,
    error = Burgundy,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Burgundy,
    background = Pearl,
    onBackground = DeepBlue,
    surface = Color.White,
    onSurface = DeepBlue,
    surfaceVariant = SandBeige,
    onSurfaceVariant = DeepBlue,
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0),
    scrim = Color.Black,
    inverseSurface = DeepBlue,
    inverseOnSurface = Pearl,
    inversePrimary = SkyBlue,
    surfaceDim = Color(0xFFF3F0F4),
    surfaceBright = Pearl,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFFDF8FD),
    surfaceContainer = Color(0xFFF7F2F7),
    surfaceContainerHigh = Color(0xFFF1ECF1),
    surfaceContainerHighest = Color(0xFFEBE6EB)
)

private val DarkColorScheme = darkColorScheme(
    primary = SkyBlue,
    onPrimary = DeepBlue,
    primaryContainer = DeepBlue,
    onPrimaryContainer = SkyBlue,
    secondary = ArabicGold,
    onSecondary = DeepBlue,
    secondaryContainer = Color(0xFF4A3C1D),
    onSecondaryContainer = ArabicGold,
    tertiary = Color(0xFF90C695),
    onTertiary = Color(0xFF1B3A20),
    tertiaryContainer = Color(0xFF2D5132),
    onTertiaryContainer = Color(0xFFABE2B0),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF141218),
    onBackground = Color(0xFFE6E0E9),
    surface = Color(0xFF141218),
    onSurface = Color(0xFFE6E0E9),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F),
    scrim = Color.Black,
    inverseSurface = Color(0xFFE6E0E9),
    inverseOnSurface = Color(0xFF322F35),
    inversePrimary = DeepBlue,
    surfaceDim = Color(0xFF141218),
    surfaceBright = Color(0xFF3B383E),
    surfaceContainerLowest = Color(0xFF0F0D13),
    surfaceContainerLow = Color(0xFF1D1B20),
    surfaceContainer = Color(0xFF211F26),
    surfaceContainerHigh = Color(0xFF2B2930),
    surfaceContainerHighest = Color(0xFF36343B)
)

@Composable
fun ArabicCrosswordTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ArabicTypography,
        content = content
    )
}