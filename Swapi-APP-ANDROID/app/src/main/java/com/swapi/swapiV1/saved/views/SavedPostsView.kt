package com.swapi.swapiV1.saved.views

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource // <-- IMPORT AÑADIDO
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.swapi.swapiV1.R // <-- IMPORT AÑADIDO
import com.swapi.swapiV1.home.model.network.HomeApiImpl
import com.swapi.swapiV1.home.model.repository.HomeRepository
import com.swapi.swapiV1.home.viewmodel.HomeUIState
import com.swapi.swapiV1.home.viewmodel.HomeViewModel
import com.swapi.swapiV1.home.viewmodel.HomeViewModelFactory
import com.swapi.swapiV1.navigation.ScreenNavigation
import com.swapi.swapiV1.utils.dismissKeyboardOnClick

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedPostsView(navController: NavController) {
    val factory = HomeViewModelFactory(HomeRepository(HomeApiImpl.retrofitApi))
    val viewModel: HomeViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }

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
            topBar = {
                SavedTopBar(
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
                                // --- CAMBIO ---
                                stringResource(R.string.saved_cargando),
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
                                Icons.Filled.Bookmark,
                                contentDescription = null,
                                modifier = Modifier.size(72.dp),
                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                            )
                            Text(
                                // --- CAMBIO ---
                                stringResource(R.string.saved_error_titulo),
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                state.message, // Dinámico
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    is HomeUIState.Success -> {
                        val allListings = state.sections.flatMap { it.listings }

                        val filteredListings = if (searchQuery.isBlank()) allListings else {
                            allListings.filter {
                                it.title.contains(searchQuery, ignoreCase = true) ||
                                        it.category.contains(searchQuery, ignoreCase = true)
                            }
                        }

                        AnimatedVisibility(
                            visible = filteredListings.isNotEmpty(),
                            enter = fadeIn(tween(500)) + scaleIn(initialScale = 0.97f, animationSpec = tween(500)),
                            exit = fadeOut(tween(300)) + scaleOut(targetScale = 0.97f, animationSpec = tween(300))
                        ) {
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(minSize = 300.dp),
                                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(20.dp),
                                horizontalArrangement = Arrangement.spacedBy(20.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(filteredListings, key = { it.id }) { listing ->
                                    SavedItemCard(
                                        title = listing.title,
                                        price = "$${listing.price} ${listing.currency}",
                                        imageUrl = listing.imageUrl,
                                        onClick = {
                                            navController.navigate(
                                                ScreenNavigation.ProductDetail.createRoute(listing.id)
                                            )
                                        }
                                    )
                                }
                            }
                        }

                        AnimatedVisibility(
                            visible = filteredListings.isEmpty() && searchQuery.isNotEmpty(),
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
                                    // --- CAMBIO (Reutilizamos la string de Sales) ---
                                    stringResource(R.string.sales_search_no_results_title),
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    // --- CAMBIO (Reutilizamos la string de Sales) ---
                                    stringResource(R.string.sales_search_no_results_subtitle),
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
private fun SavedTopBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    brandColor: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            // --- CAMBIO: Añadido padding para la barra de estado ---
            .windowInsetsPadding(WindowInsets.statusBars),
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
                    // --- CAMBIO: Quitado el padding 'top = 40.dp' ---
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        // --- CAMBIO ---
                        stringResource(R.string.saved_titulo),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        // --- CAMBIO ---
                        stringResource(R.string.saved_subtitulo),
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    Icons.Filled.Bookmark,
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
                        // --- CAMBIO ---
                        stringResource(R.string.saved_buscar_placeholder),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        // --- CAMBIO (Reutilizamos la string de Sales) ---
                        contentDescription = stringResource(R.string.sales_search_icon_cd),
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

@Composable
private fun SavedItemCard(
    title: String,
    price: String,
    imageUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = title, // Dinámico, está bien
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title, // Dinámico
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
                Text(
                    text = price, // Dinámico
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}