package com.swapi.swapiV1.components.topbar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.swapi.swapiV1.R

@Composable
fun SwapiTopBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit
) {
    // Obtenemos el controlador para poder ocultar el teclado manualmente al buscar
    val keyboardController = LocalSoftwareKeyboardController.current
    val swapiBrandColor = Color(0xFF4A8BFF)

    // Usamos Surface en lugar de TopAppBar para tener control total sobre el diseño
    // y permitir que la barra fluya con el contenido si fuera necesario.
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            placeholder = {
                Text(
                    text = stringResource(id = R.string.home_buscar_placeholder),
                    style = MaterialTheme.typography.bodyMedium,
                    // Usamos onSurfaceVariant con transparencia para que el placeholder sea sutil
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = swapiBrandColor,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                // Solo mostramos el botón de borrar si hay texto escrito
                if (searchText.isNotEmpty()) {
                    IconButton(onClick = { onSearchTextChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Limpiar búsqueda",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            },
            // Redondeamos las esquinas al 50% de la altura (aprox) para efecto píldora
            shape = RoundedCornerShape(24.dp),
            singleLine = true,

            // Personalización profunda de colores para eliminar bordes por defecto
            colors = TextFieldDefaults.colors(
                // Fondo sutilmente visible tanto en foco como sin foco
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),

                // Establecer estos indicadores en transparente elimina la línea inferior o el borde
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,

                cursorColor = swapiBrandColor
            ),

            // Configuración del teclado: Botón de acción "Buscar"
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                keyboardController?.hide()
            })
        )
    }
}