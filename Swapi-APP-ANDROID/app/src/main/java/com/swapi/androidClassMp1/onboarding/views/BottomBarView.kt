package com.swapi.androidClassMp1.onboarding.views

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
import com.swapi.androidClassMp1.R

@Composable
fun BottomBarView(
    isLastPage: Boolean,
    page: Int,
    total: Int,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        TextButton(
            enabled = page > 0,
            onClick = onPrev
        ) {
            Text(
                text = stringResource(id = R.string.onboarding_previous_button),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onNext,
            shape = RoundedCornerShape(50),
            modifier = Modifier.height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1976D2),   // Fondo del botón
                contentColor = Color.White           // Color del texto
            )
        ) {
            Text(
                text = if (isLastPage) stringResource(id = R.string.onboarding_start_button)
                else stringResource(id = R.string.onboarding_next_button),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

