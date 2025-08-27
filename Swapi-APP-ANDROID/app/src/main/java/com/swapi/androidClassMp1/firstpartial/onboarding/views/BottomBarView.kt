package com.swapi.androidClassMp1.firstpartial.onboarding.views

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
                "Anterior",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onNext,
            shape = RoundedCornerShape(50),
            modifier = Modifier.height(48.dp)
        ) {
            Text(
                if (isLastPage) "Empezar" else "Siguiente",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
