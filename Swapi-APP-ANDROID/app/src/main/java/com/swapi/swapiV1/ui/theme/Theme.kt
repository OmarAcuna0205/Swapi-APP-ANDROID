package com.swapi.swapiV1.ui.theme

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

// --- 1. Tu paleta de colores "Luxury Tech" ---
val DarkNavy = Color(0xFF0D1117)
val RoyalBlue = Color(0xFF3C65F5)
val SlateGray = Color(0xFF8B949E)
val OffWhite = Color(0xFFF0F6FC)
// Hecho público para que puedas usarlo en efectos especiales como el glassmorphism
val GlassyBlue = Color(0x7F1E3A5F)

// --- 2. Mapeamos tu paleta al DarkColorScheme de Material 3 ---
private val LuxuryDarkColorScheme = darkColorScheme(
    primary = RoyalBlue,              // Color de acento principal (botones, precios)
    onPrimary = Color.White,          // Color del texto sobre el primario
    secondary = GlassyBlue,           // Un color secundario
    background = DarkNavy,            // Color de fondo de las pantallas
    surface = DarkNavy,               // Color de fondo de elementos como las Cards
    onBackground = OffWhite,          // Color del texto sobre el fondo
    onSurface = OffWhite,             // Color del texto sobre las surfaces
    onSurfaceVariant = SlateGray,     // Para texto secundario o placeholders
    error = Color(0xFFCF6679),        // Un rojo estándar para errores
    outline = SlateGray.copy(alpha = 0.5f) // Para bordes y divisores
)

// --- (Opcional) Un LightColorScheme coherente ---
private val LuxuryLightColorScheme = lightColorScheme(
    primary = RoyalBlue,
    onPrimary = Color.White,
    secondary = Color(0xFFE3F2FD),
    background = OffWhite,
    surface = Color.White,
    onBackground = DarkNavy,
    onSurface = DarkNavy,
    onSurfaceVariant = SlateGray,
    error = Color(0xFFB00020),
    outline = SlateGray.copy(alpha = 0.5f)
)


@Composable
fun SwpaiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true, // Puedes ponerlo en `false` para forzar tu tema
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // --- 3. Usamos nuestro Luxury Theme ---
        darkTheme -> LuxuryDarkColorScheme
        else -> LuxuryLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}