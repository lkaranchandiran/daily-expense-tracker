package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Emerald80,
    onPrimary = Color(0xFF003825),
    primaryContainer = Color(0xFF005137),
    onPrimaryContainer = Color(0xFFA6F2D0),
    secondary = EmeraldGrey80,
    onSecondary = Color(0xFF1E352B),
    secondaryContainer = Color(0xFF354B40),
    onSecondaryContainer = Color(0xFFCEE9D9),
    tertiary = MintTeal80,
    background = ForestDarkBackground,
    onBackground = Color(0xFFE1E3DF),
    surface = ForestDarkSurface,
    onSurface = Color(0xFFE1E3DF),
    surfaceVariant = ForestDarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFBFC9C2),
    outline = Color(0xFF89938D),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

private val LightColorScheme = lightColorScheme(
    primary = Emerald40,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA6F2D0),
    onPrimaryContainer = Color(0xFF002115),
    secondary = EmeraldGrey40,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCEE9D9),
    onSecondaryContainer = Color(0xFF082016),
    tertiary = MintTeal40,
    background = SoftLightBackground,
    onBackground = Color(0xFF191C1B),
    surface = SoftLightSurface,
    onSurface = Color(0xFF191C1B),
    surfaceVariant = SoftLightSurfaceVariant,
    onSurfaceVariant = Color(0xFF404944),
    outline = Color(0xFF707974),
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

@Composable
fun ExpenseTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent emerald aesthetic across devices
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
