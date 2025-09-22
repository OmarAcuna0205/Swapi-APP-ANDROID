package com.swapi.androidClassMp1.components.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.swapi.androidClassMp1.navigation.ScreenNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwapiTopBar(
    navController: NavHostController,
    onSearchAction: (String) -> Unit
) {
    var showSearchBar by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    CenterAlignedTopAppBar(
        title = {
            if (showSearchBar) {
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }

                // --- CAMBIO PRINCIPAL: Usamos BasicTextField para control total ---
                BasicTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .focusRequester(focusRequester),
                    singleLine = true,
                    // Estilo del texto que el usuario escribe
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 16.sp
                    ),
                    // El cursor será del mismo color que el texto
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onPrimaryContainer),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            onSearchAction(searchText)
                            keyboardController?.hide()
                        }
                    ),
                    decorationBox = { innerTextField ->
                        // Esta es la "caja" que decora nuestro campo de texto
                        Row(
                            modifier = Modifier
                                .background(
                                    color = Color.Black.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(32.dp)
                                )
                                // --- ¡AQUÍ ESTÁ EL CONTROL DE LA ALTURA! ---
                                .padding(vertical = 8.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                // Si no hay texto, mostramos el placeholder
                                if (searchText.isEmpty()) {
                                    Text(
                                        text = "Buscar en Swapi...",
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                                        fontSize = 16.sp
                                    )
                                }
                                // Aquí se dibuja el campo de texto real
                                innerTextField()
                            }
                            // Mostramos el botón de limpiar si hay texto
                            if (searchText.isNotEmpty()) {
                                IconButton(
                                    onClick = { searchText = "" },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Limpiar búsqueda",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }
                )
            } else {
                Text(
                    text = "Swapi",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        // El resto del código no cambia...
        navigationIcon = {
            if (showSearchBar) {
                IconButton(onClick = {
                    showSearchBar = false
                    searchText = ""
                }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            } else {
                IconButton(onClick = { navController.navigate(ScreenNavigation.Profile.route) }) {
                    Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Ir al perfil")
                }
            }
        },
        actions = {
            // La lupa solo se muestra cuando NO está activa la búsqueda
            if (!showSearchBar) {
                IconButton(onClick = { showSearchBar = true }) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar")
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFFE3F2FD),
            titleContentColor = Color(0xFF0D47A1)
        )
    )
}