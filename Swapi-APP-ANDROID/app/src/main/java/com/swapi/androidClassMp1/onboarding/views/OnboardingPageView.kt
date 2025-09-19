package com.swapi.androidClassMp1.onboarding.views

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.swapi.androidClassMp1.onboarding.model.OnboardingPageModel

@Composable
fun OnboardingPageView(pageModel: OnboardingPageModel, selected: Boolean = false) {
    val animatedAlpha by animateFloatAsState(if (selected) 1f else 0.5f)

    Box(modifier = Modifier.fillMaxSize()) {
        // CAMBIO: La imagen ahora es el fondo y ocupa toda la pantalla.
        Image(
            painter = painterResource(id = pageModel.imageRes),
            contentDescription = pageModel.title,
            // AÑADIDO: Asegura que la imagen cubra el espacio sin deformarse.
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // AÑADIDO: Un degradado oscuro para que el texto blanco siempre sea legible.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                        startY = 600f // Empieza el degradado más abajo
                    )
                )
        )

        // CAMBIO: El Column ahora se alinea en la parte inferior del Box.
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            // AÑADIDO: Empuja el contenido hacia abajo y añade padding.
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(bottom = 150.dp) // Espacio para los botones y los puntos
                .alpha(animatedAlpha)
        ) {
            Text(
                text = pageModel.title,
                // CAMBIO: Añadido fontWeight para hacer la letra más gruesa (negrita)
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = pageModel.description,
                // CAMBIO: Añadido fontWeight para hacerla más notoria (semi-negrita)
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
        }
    }
}