package com.dulcemiel.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun TemaDulceMiel(contenido: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = MielOscura,
            secondary = Hoja,
            background = Crema,
            surface = Color.White,
            onBackground = Tinta,
            onSurface = Tinta
        ),
        content = contenido
    )
}
