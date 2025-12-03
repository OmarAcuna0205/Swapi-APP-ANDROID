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
import com.swapi.swapiV1.sales.views.SaleProductCard
import com.swapi.swapiV1.utils.ErrorMessageMapper
import com.swapi.swapiV1.utils.dismissKeyboardOnClick
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryProductView(
    navController: NavController,
    category: String
) {
    val repository = remember { HomeRepository() }
    val factory = HomeViewModelFactory(repository)
    val viewModel: HomeViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    // --- 1. TRADUCCIÓN DEL TÍTULO ---
    // Mapeamos el ID del backend (ej: "ventas") al stringResource correspondiente
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
                onQueryChange = { searchQuery = it },
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
                .dismissKeyboardOnClick(),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is HomeUIState.Loading -> CircularProgressIndicator(color = swapiBrandColor)

                is HomeUIState.Error -> {
                    // --- 2. MANEJO DE ERRORES CON MAPPER ---
                    val msg = ErrorMessageMapper.getMessage(context, state.message)
                    Text(
                        text = msg,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                is HomeUIState.Success -> {
                    val filteredList = state.products.filter { product ->
                        val isCategoryMatch = product.category.equals(category, ignoreCase = true)
                        val isSearchMatch = if (searchQuery.isBlank()) true else {
                            product.title.contains(searchQuery, ignoreCase = true) ||
                                    product.description.contains(searchQuery, ignoreCase = true)
                        }
                        isCategoryMatch && isSearchMatch
                    }

                    if (filteredList.isNotEmpty()) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(1),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(filteredList, key = { it.id }) { product ->
                                SaleProductCard(
                                    product = product,
                                    onClick = {
                                        navController.navigate(ScreenNavigation.ProductDetail.createRoute(product.id))
                                    }
                                )
                            }
                        }
                    } else {
                        // --- 3. ESTADO VACÍO CON FORMATO ---
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
                                // "No hay Ventas disponibles" / "No Sales available"
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 8.dp, end = 20.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.common_atras_cd) // --- 4. CD ---
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                // --- 5. PLACEHOLDER DINÁMICO ---
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