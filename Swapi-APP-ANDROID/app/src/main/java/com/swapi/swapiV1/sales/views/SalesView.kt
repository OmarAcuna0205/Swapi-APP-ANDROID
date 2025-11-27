package com.swapi.swapiV1.sales.views

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.swapi.swapiV1.R
import com.swapi.swapiV1.home.model.dto.Product // <--- CAMBIO: Usamos Product
import com.swapi.swapiV1.home.model.repository.HomeRepository
import com.swapi.swapiV1.home.viewmodel.HomeUIState
import com.swapi.swapiV1.home.viewmodel.HomeViewModel
import com.swapi.swapiV1.home.viewmodel.HomeViewModelFactory
import com.swapi.swapiV1.navigation.ScreenNavigation
import com.swapi.swapiV1.utils.dismissKeyboardOnClick

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesView(navController: NavController) {
    // Inicializamos el ViewModel (usando repo simple)
    val factory = HomeViewModelFactory(HomeRepository())
    val viewModel: HomeViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var fabScale by remember { mutableStateOf(1f) }

    val animatedFabScale by animateFloatAsState(
        targetValue = fabScale,
        animationSpec = tween(200),
        label = "fabScale"
    )

    val swapiBrandColor = Color(0xFF4A8BFF)

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
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        fabScale = 0.85f
                        // Asumo que NewPublication no espera argumentos por ahora
                        navController.navigate(ScreenNavigation.NewPublication.route)
                    },
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = animatedFabScale
                            scaleY = animatedFabScale
                        },
                    shape = CircleShape,
                    containerColor = swapiBrandColor,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 10.dp,
                        pressedElevation = 16.dp
                    )
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = stringResource(id = R.string.sales_fab_add_cd),
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            topBar = {
                SalesTopBar(
                    searchQuery = searchQuery,
                    onQueryChange = { searchQuery = it },
                    brandColor = swapiBrandColor
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .dismissKeyboardOnClick(),
                contentAlignment = Alignment.Center
            ) {
                when (val state = uiState) {
                    is HomeUIState.Loading -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(50.dp),
                                strokeWidth = 4.dp,
                                color = swapiBrandColor
                            )
                            Text(
                                stringResource(id = R.string.sales_loading_text),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    is HomeUIState.Error -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                Icons.Filled.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(72.dp),
                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                            )
                            Text(
                                stringResource(id = R.string.sales_error_title),
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                state.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    is HomeUIState.Success -> {
                        // --- LÓGICA DE FILTRADO ---
                        val allListings = state.products // Ahora es una lista plana

                        // 1. Filtramos SOLO los de categoría "ventas" (Case insensitive)
                        val salesListings = allListings.filter {
                            it.category.equals("ventas", ignoreCase = true)
                        }

                        // 2. Filtramos por búsqueda si el usuario escribió algo
                        val filteredListings = if (searchQuery.isBlank()) salesListings else {
                            salesListings.filter {
                                it.title.contains(searchQuery, ignoreCase = true) ||
                                        it.description.contains(searchQuery, ignoreCase = true)
                            }
                        }

                        AnimatedVisibility(
                            visible = filteredListings.isNotEmpty(),
                            enter = fadeIn(tween(500)) + scaleIn(initialScale = 0.97f, animationSpec = tween(500)),
                            exit = fadeOut(tween(300)) + scaleOut(targetScale = 0.97f, animationSpec = tween(300))
                        ) {
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(minSize = 160.dp), // Ajusté el tamaño a 160dp para que quepan 2 columnas
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(filteredListings, key = { it.id }) { product ->
                                    SaleProductCard(
                                        product = product, // Enviamos Product
                                        onClick = {
                                            navController.navigate(
                                                ScreenNavigation.ProductDetail.createRoute(product.id)
                                            )
                                        }
                                    )
                                }
                            }
                        }

                        // Estado vacío (sin resultados)
                        AnimatedVisibility(
                            visible = filteredListings.isEmpty(),
                            enter = fadeIn(tween(400)),
                            exit = fadeOut(tween(300))
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.padding(32.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Search,
                                    contentDescription = null,
                                    modifier = Modifier.size(72.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                                Text(
                                    stringResource(id = R.string.sales_search_no_results_title),
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    if (searchQuery.isNotEmpty())
                                        stringResource(id = R.string.sales_search_no_results_subtitle)
                                    else
                                        "No hay publicaciones de ventas disponibles aún.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SalesTopBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    brandColor: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)),
        shadowElevation = 10.dp,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 40.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        "Ventas",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        stringResource(id = R.string.sales_header_subtitle),
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    Icons.Filled.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = brandColor
                )
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                placeholder = {
                    Text(
                        stringResource(id = R.string.sales_search_placeholder),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = stringResource(id = R.string.sales_search_icon_cd),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = brandColor,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    disabledIndicatorColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                textStyle = MaterialTheme.typography.bodyLarge
            )
        }
    }
}