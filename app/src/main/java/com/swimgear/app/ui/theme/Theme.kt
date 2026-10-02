package com.swimgear.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val LightColors = lightColorScheme(
    primary = Color(0xFF006C67), onPrimary = Color.White,
    primaryContainer = Color(0xFFB6F1DF), onPrimaryContainer = Color(0xFF073D39),
    secondary = Color(0xFF416960), secondaryContainer = Color(0xFFE0EFE9),
    onSecondaryContainer = Color(0xFF173B33),
    background = Color(0xFFF5F8F6), onBackground = Color(0xFF142F2E),
    surface = Color(0xFFF5F8F6), onSurface = Color(0xFF142F2E),
    surfaceContainer = Color(0xFFEAF1ED), surfaceContainerLow = Color.White,
    surfaceContainerHigh = Color(0xFFE4EDE8),
    onSurfaceVariant = Color(0xFF4C625C), outline = Color(0xFF70837C),
    outlineVariant = Color(0xFFCBDAD3),
)
private val DarkColors = darkColorScheme(
    primary = Color(0xFF82D7C5), onPrimary = Color(0xFF003B35),
    primaryContainer = Color(0xFF11534B), onPrimaryContainer = Color(0xFFB6F1DF),
    background = Color(0xFF102421), surface = Color(0xFF102421),
    onBackground = Color(0xFFE1F0E9), onSurface = Color(0xFFE1F0E9),
    surfaceContainerLow = Color(0xFF18302B), surfaceContainer = Color(0xFF203A34),
    onSurfaceVariant = Color(0xFFBACDC4), outlineVariant = Color(0xFF405D52),
)

@Composable
fun SwimGearTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        typography = Typography(
            headlineLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 39.sp),
            headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 27.sp, lineHeight = 34.sp),
            titleLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 29.sp),
            titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 17.sp, lineHeight = 24.sp),
        ),
        content = content,
    )
}
