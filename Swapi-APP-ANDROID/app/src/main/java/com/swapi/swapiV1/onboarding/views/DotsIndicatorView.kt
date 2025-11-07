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

@Composable
fun DotsIndicatorView(
    totalDots: Int,
    selectedIndex: Int,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        repeat(totalDots) { index ->
            val selected = index == selectedIndex
            val dotSize by animateDpAsState(if (selected) 12.dp else 8.dp)
            val dotColor by animateColorAsState(
                if (selected) Color(0xFF1976D2) // Azul seleccionado
                else Color.Gray.copy(alpha = 0.3f) // Gris clarito para los no seleccionados
            )
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(dotSize)
                    .clip(CircleShape)
                    .background(dotColor)
            )
        }
    }
}
