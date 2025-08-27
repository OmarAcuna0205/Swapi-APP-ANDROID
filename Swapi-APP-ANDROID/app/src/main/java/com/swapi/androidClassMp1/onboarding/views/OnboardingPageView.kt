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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.swapi.androidClassMp1.onboarding.model.OnboardingPageModel
import com.swapi.androidClassMp1.ui.theme.SwapiBlue
import com.swapi.androidClassMp1.ui.theme.SwapiBlueLight
import com.swapi.androidClassMp1.ui.theme.SwapiWhite

@Composable
fun OnboardingPageView(pageModel: OnboardingPageModel, selected: Boolean = false) {
    // Animación de escala de la imagen
    val scale by animateFloatAsState(targetValue = if (selected) 1.1f else 1f)
    val animatedAlpha by animateFloatAsState(if (selected) 1f else 0.7f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(SwapiBlue, SwapiBlueLight)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Image(
                painter = painterResource(id = pageModel.imageRes),
                contentDescription = pageModel.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = pageModel.title,
                style = MaterialTheme.typography.titleLarge,
                color = SwapiWhite,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer { alpha = animatedAlpha }
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = pageModel.description,
                style = MaterialTheme.typography.bodyLarge,
                color = SwapiWhite.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer { alpha = animatedAlpha }
            )
        }
    }
}
