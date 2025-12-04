package com.swapi.swapiV1.onboarding.views

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.swapi.swapiV1.R

/**
 * Componente Composable que renderiza la barra de navegación inferior del Onboarding.
 * Contiene los controles para avanzar o retroceder entre las páginas de introducción.
 */
@Composable
fun BottomBarView(
    isLastPage: Boolean, // Indica si estamos en la última diapositiva para cambiar el texto del botón.
    page: Int,           // Índice de la página actual.
    total: Int,          // Total de páginas (no usado visualmente aquí, pero útil para contexto).
    onPrev: () -> Unit,  // Callback para manejar el evento de retroceso.
    onNext: () -> Unit   // Callback para manejar el evento de avance o finalización.
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Condición lógica: El botón "Anterior" solo se muestra si NO estamos en la primera página (índice 0).
        if (page > 0) {
            TextButton(
                onClick = onPrev
            ) {
                Text(
                    text = stringResource(id = R.string.onboarding_previous_button),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // Este Spacer con weight(1f) ocupa todo el espacio disponible horizontalmente,
        // empujando el botón "Siguiente" hacia el extremo derecho de la fila.
        Spacer(Modifier.weight(1f))

        Button(
            onClick = onNext,
            shape = RoundedCornerShape(50),
            modifier = Modifier.height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1976D2),
                contentColor = Color.White
            )
        ) {
            // Lógica de UI: Si es la última página, mostramos "Empezar", de lo contrario "Siguiente".
            Text(
                text = if (isLastPage) stringResource(id = R.string.onboarding_start_button)
                else stringResource(id = R.string.onboarding_next_button),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}