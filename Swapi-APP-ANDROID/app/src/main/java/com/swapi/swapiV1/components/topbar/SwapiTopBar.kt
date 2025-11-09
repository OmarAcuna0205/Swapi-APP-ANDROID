package com.swapi.swapiV1.components.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwapiTopBar(
    // --- CAMBIO: Acepta todos los estados y eventos como parámetros ---
    showSearchBar: Boolean,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onToggleSearchBar: () -> Unit,
    onSearchAction: () -> Unit // <-- Para el botón "Enter" del teclado
    // --------------------------------------------------------
) {
    // Ya no tiene estado interno (var showSearchBar by remember...)

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val SwapiBlue = Color(0xFF448AFF)

    CenterAlignedTopAppBar(
        title = {
            if (showSearchBar) {
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
                BasicTextField(
                    value = searchText, // <-- Usa el parámetro
                    onValueChange = onSearchTextChange, // <-- Llama a la función del VM
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .focusRequester(focusRequester),
                    singleLine = true,
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        onSearchAction() // <-- Llama a la función del VM
                        keyboardController?.hide()
                    }),
                    decorationBox = { innerTextField ->
                        Row(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(32.dp)
                                )
                                .padding(vertical = 8.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                if (searchText.isEmpty()) {
                                    Text(
                                        text = "Buscar en Swapi...",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                innerTextField()
                            }
                            if (searchText.isNotEmpty()) {
                                IconButton(
                                    onClick = { onSearchTextChange("") }, // Limpia el texto
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Limpiar búsqueda"
                                    )
                                }
                            }
                        }
                    }
                )
            } else {
                Text(
                    text = "Swapi",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = SwapiBlue
                )
            }
        },
        navigationIcon = {
            if (showSearchBar) {
                IconButton(onClick = onToggleSearchBar) { // <-- Llama a la función del VM
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Cerrar búsqueda")
                }
            }
        },
        actions = {
            if (!showSearchBar) {
                IconButton(onClick = onToggleSearchBar) { // <-- Llama a la función del VM
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar")
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}