package com.example.proyecto.ui.theme

import android.app.Activity
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
    primary = Pantone320,        // Color principal para elementos interactivos en modo oscuro
    secondary = PantoneCoolGray8, // Color secundario para elementos menos prominentes
    tertiary = Pantone621Violet, // Color terciario para contrastes o acentos
    background = Pantone302,    // Fondo general en modo oscuro (un azul oscuro)
    surface = Pantone302,       // Superficies de componentes como tarjetas, dialogos
    onPrimary = White,          // Color del texto/iconos sobre el primary
    onSecondary = White,        // Color del texto/iconos sobre el secondary
    onTertiary = White,         // Color del texto/iconos sobre el tertiary
    onBackground = White,       // Color del texto/iconos sobre el background
    onSurface = White,          // Color del texto/iconos sobre la surface
    error = Pantone1575,        // Color para indicar errores (ej. un naranja/rojo)
    onError = White             // Color del texto/iconos sobre el error
)

private val LightColorScheme = lightColorScheme(
    primary = Pantone302,
    secondary = Pantone320,
    tertiary = Pantone621Violet,
    background = Pantone621,
    surface = White,
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    error = Pantone1575,
    onError = White
)

@Composable
fun ProyectoTheme(
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography, // <- aquí ya usas Gotham
        content = content
    )
}