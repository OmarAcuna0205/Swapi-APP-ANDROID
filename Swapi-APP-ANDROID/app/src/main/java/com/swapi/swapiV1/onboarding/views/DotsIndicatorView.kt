package com.swapi.swapiV1.onboarding.views

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Componente visual que muestra una fila de indicadores (puntos) para la paginación.
 * Se utiliza para dar retroalimentación al usuario sobre en qué paso del Onboarding se encuentra.
 *
 * @param totalDots Cantidad total de páginas o puntos a renderizar.
 * @param selectedIndex El índice de la página actualmente activa (base 0).
 * @param modifier Modificador para ajustar el layout (márgenes, alineación, etc.).
 */
@Composable
fun DotsIndicatorView(
    totalDots: Int,
    selectedIndex: Int,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        // Generamos dinámicamente la cantidad de puntos según el total de páginas
        repeat(totalDots) { index ->

            // Determinamos si el punto actual corresponde a la página activa
            val selected = index == selectedIndex

            // Animación de tamaño:
            // Si está seleccionado, el punto crece a 12.dp, si no, se reduce a 8.dp.
            // animateDpAsState se encarga de interpolar el cambio suavemente.
            val dotSize by animateDpAsState(if (selected) 12.dp else 8.dp)

            // Animación de color:
            // Cambia suavemente entre azul (activo) y gris (inactivo).
            val dotColor by animateColorAsState(
                if (selected) Color(0xFF1976D2)
                else Color.Gray.copy(alpha = 0.3f)
            )

            // Representación gráfica del punto
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp) // Espaciado entre puntos
                    .size(dotSize)              // Tamaño dinámico animado
                    .clip(CircleShape)          // Forma circular
                    .background(dotColor)       // Color dinámico animado
            )
        }
    }
}