package com.swapi.swapiV1.onboarding.views

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.swapi.swapiV1.onboarding.model.OnboardingPageModel

/**
 * Composable que representa una página individual dentro del carrusel de Onboarding.
 * Renderiza la imagen de fondo y superpone el texto con un efecto visual.
 *
 * @param pageModel Contiene los recursos (IDs) de imagen y texto para esta página.
 * @param selected Indica si es la página actual para animar la opacidad del contenido.
 */
@Composable
fun OnboardingPageView(pageModel: OnboardingPageModel, selected: Boolean = false) {
    // Animación de estado:
    // Si la página está seleccionada, la opacidad es 100% (1f), si no, se reduce al 50%.
    // Esto crea una transición suave al deslizar entre páginas.
    val animatedAlpha by animateFloatAsState(if (selected) 1f else 0.5f)

    // Recuperamos los Strings usando los IDs del modelo para soportar internacionalización.
    val title = stringResource(id = pageModel.titleResId)
    val description = stringResource(id = pageModel.descriptionResId)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = pageModel.imageRes),
            contentDescription = title,
            contentScale = ContentScale.Crop, // Ajusta la imagen para cubrir toda la pantalla (recortando si es necesario)
            modifier = Modifier.fillMaxSize()
        )

        // Capa de degradado (Gradient Overlay):
        // Oscurece la parte inferior de la imagen para garantizar que el texto blanco sea legible.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                        startY = 600f
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(bottom = 150.dp)
                .alpha(animatedAlpha) // Aplicamos la animación de opacidad calculada arriba
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
        }
    }
}