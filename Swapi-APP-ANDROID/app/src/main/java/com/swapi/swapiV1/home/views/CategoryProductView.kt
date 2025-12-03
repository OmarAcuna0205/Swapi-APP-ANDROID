package com.swapi.swapiV1.home.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.swapi.swapiV1.R
import com.swapi.swapiV1.home.model.repository.HomeRepository
import com.swapi.swapiV1.home.viewmodel.HomeUIState
import com.swapi.swapiV1.home.viewmodel.HomeViewModel
import com.swapi.swapiV1.home.viewmodel.HomeViewModelFactory
import com.swapi.swapiV1.navigation.ScreenNavigation
import com.swapi.swapiV1.home.views.CategoryProductCardView
import com.swapi.swapiV1.utils.ErrorMessageMapper
import com.swapi.swapiV1.utils.dismissKeyboardOnClick
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryProductView(
    navController: NavController,
    category: String // Recibimos la categoria como argumento de navegacion
) {
    // Inicializamos el ViewModel utilizando un Factory para inyectar el repositorio.
    // Esto asegura una correcta separacion de responsabilidades y facilita el testing.
    val repository = remember { HomeRepository() }
    val factory = HomeViewModelFactory(repository)
    val viewModel: HomeViewModel = viewModel(factory = factory)

    // Recolectamos el estado de la UI de manera reactiva y consciente del ciclo de vida.
    // Esto significa que la UI se actualizara automaticamente cuando los datos cambien
    // y detendra la recoleccion si la app pasa a segundo plano para ahorrar recursos.
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Estado local para almacenar el texto que el usuario escribe en el buscador
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Logica de mapeo para internacionalizacion:
    // Convertimos el ID de la categoria (que viene del backend/navegacion)
    // al recurso de string correspondiente en el archivo strings.xml.
    val displayTitle = when(category.lowercase()) {
        "ventas" -> stringResource(R.string.ventas_title)
        "rentas" -> stringResource(R.string.rentas_title)
        "servicios" -> stringResource(R.string.servicios_title)
        "anuncios" -> stringResource(R.string.anuncios_title)
        else -> category.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    val swapiBrandColor = Color(0xFF4A8BFF)

    Scaffold(
        topBar = {
            CategoryTopBar(
                title = displayTitle,
                searchQuery = searchQuery,
                onQueryChange = { searchQuery = it }, // Actualizamos el estado local de busqueda
                onBack = { navController.popBackStack() },
                brandColor = swapiBrandColor
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                        )
                    )
                )
                .padding(paddingValues)
                .dismissKeyboardOnClick(), // Utilidad para ocultar teclado al tocar el fondo
            contentAlignment = Alignment.Center
        ) {
            // Manejo de estados de la UI (Patron MVI/MVVM):
            // Renderizamos diferentes componentes segun el estado actual de los datos (Carga, Error o Exito).
            when (val state = uiState) {
                is HomeUIState.Loading -> CircularProgressIndicator(color = swapiBrandColor)

                is HomeUIState.Error -> {
                    // Si hay error, usamos el Mapper para traducir el codigo tecnico
                    // a un mensaje legible para el usuario definido en los recursos.
                    val msg = ErrorMessageMapper.getMessage(context, state.message)
                    Text(
                        text = msg,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                is HomeUIState.Success -> {
                    // Logica de filtrado en el cliente:
                    // 1. Filtramos primero por la categoria de esta pantalla.
                    // 2. Luego aplicamos el filtro de busqueda si el usuario escribio algo.
                    val filteredList = state.products.filter { product ->
                        val isCategoryMatch = product.category.equals(category, ignoreCase = true)
                        val isSearchMatch = if (searchQuery.isBlank()) true else {
                            product.title.contains(searchQuery, ignoreCase = true) ||
                                    product.description.contains(searchQuery, ignoreCase = true)
                        }
                        isCategoryMatch && isSearchMatch
                    }

                    if (filteredList.isNotEmpty()) {
                        // Renderizado eficiente de la lista usando LazyVerticalGrid.
                        // Solo renderiza los elementos visibles para optimizar memoria.
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(1),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(filteredList, key = { it.id }) { product ->
                                // Reutilizamos el componente de tarjeta para cada producto
                                CategoryProductCardView(
                                    product = product,
                                    onClick = {
                                        navController.navigate(ScreenNavigation.ProductDetail.createRoute(product.id))
                                    }
                                )
                            }
                        }
                    } else {
                        // Estado vacio: Se muestra cuando no hay productos en la categoria
                        // o la busqueda no arroja resultados.
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                Icons.Filled.Search,
                                contentDescription = null,
                                modifier = Modifier.size(60.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Text(
                                text = stringResource(R.string.category_empty, displayTitle),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

// Componente extraido para la barra superior (TopBar).
// Esto mejora la legibilidad y permite reutilizar o modificar la cabecera independientemente.
@Composable
private fun CategoryTopBar(
    title: String,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onBack: () -> Unit,
    brandColor: Color
) {
    Surface(
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            // Fila para el boton de regreso y el titulo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 8.dp, end = 20.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.common_atras_cd)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Campo de texto de busqueda con estilos personalizados
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                placeholder = { Text(stringResource(R.string.category_search_hint, title)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    focusedIndicatorColor = brandColor,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }
    }
}