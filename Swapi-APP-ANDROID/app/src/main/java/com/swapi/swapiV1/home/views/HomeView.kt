package com.swapi.swapiV1.home.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.swapi.swapiV1.R
import com.swapi.swapiV1.components.topbar.SwapiTopBar
import com.swapi.swapiV1.home.model.dto.Product
import com.swapi.swapiV1.home.model.repository.HomeRepository
import com.swapi.swapiV1.home.viewmodel.HomeUIState
import com.swapi.swapiV1.home.viewmodel.HomeViewModel
import com.swapi.swapiV1.home.viewmodel.HomeViewModelFactory
import com.swapi.swapiV1.navigation.ScreenNavigation
import com.swapi.swapiV1.utils.Constants
import com.swapi.swapiV1.utils.ErrorMessageMapper
import com.swapi.swapiV1.utils.datastore.DataStoreManager
import com.swapi.swapiV1.utils.dismissKeyboardOnClick
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    navController: NavController,
    dataStore: DataStoreManager
) {
    val factory = HomeViewModelFactory(HomeRepository())
    val viewModel: HomeViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchText by viewModel.searchText.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    // --- LOGICA DE REFRESH ---
    val currentBackStack = navController.currentBackStackEntry
    val refreshHomeState by currentBackStack?.savedStateHandle
        ?.getStateFlow("refresh_home", false)
        ?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(false) }

    LaunchedEffect(refreshHomeState) {
        if (refreshHomeState) {
            viewModel.onRefresh()
            currentBackStack?.savedStateHandle?.set("refresh_home", false)
        }
    }
    // -------------------------

    // Configuración del Pull to Refresh
    val pullRefreshState = rememberPullToRefreshState()
    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            viewModel.onRefresh()
        }
    }
    LaunchedEffect(isRefreshing) {
        if (!isRefreshing) {
            pullRefreshState.endRefresh()
        } else {
            pullRefreshState.startRefresh()
        }
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .dismissKeyboardOnClick()
                .nestedScroll(pullRefreshState.nestedScrollConnection),
            contentAlignment = Alignment.TopCenter
        ) {
            when (val state = uiState) {
                is HomeUIState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                is HomeUIState.Error -> {
                    // CORRECCIÓN: Usar Mapper para el error
                    val errorMsg = ErrorMessageMapper.getMessage(androidx.compose.ui.platform.LocalContext.current, state.message)
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = errorMsg,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                is HomeUIState.Success -> {
                    val userName by dataStore.userNameFlow.collectAsState(initial = "...")
                    val allProducts = state.products

                    val filteredProducts = if (searchText.isNotBlank()) {
                        allProducts.filter {
                            it.title.contains(searchText, ignoreCase = true) ||
                                    it.description.contains(searchText, ignoreCase = true)
                        }
                    } else {
                        allProducts
                    }

                    val categoriesOrder = listOf("ventas", "rentas", "servicios", "anuncios")
                    val productsByCategory = remember(filteredProducts) {
                        filteredProducts.groupBy { it.category.lowercase() }
                    }
                    val isEmpty = filteredProducts.isEmpty()

                    // CORRECCIÓN: Mapa para traducir los títulos de las secciones
                    val categoryTitles = mapOf(
                        "ventas" to stringResource(R.string.ventas_title),
                        "rentas" to stringResource(R.string.rentas_title),
                        "servicios" to stringResource(R.string.servicios_title),
                        "anuncios" to stringResource(R.string.anuncios_title)
                    )

                    // --- CONTENIDO SCROLLEABLE ---
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // 1. BARRA DE BÚSQUEDA
                        Spacer(modifier = Modifier.height(8.dp))
                        SwapiTopBar(
                            searchText = searchText,
                            onSearchTextChange = viewModel::onSearchTextChange
                        )

                        // 2. BIENVENIDA
                        AnimatedVisibility(visible = !isEmpty) {
                            if (searchText.isBlank()) {
                                Text(
                                    text = stringResource(R.string.home_bienvenida, userName),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)
                                )
                            }
                        }

                        // 3. CATEGORÍAS
                        if (!isEmpty) {
                            categoriesOrder.forEach { categoryKey ->
                                val productsInCat = productsByCategory[categoryKey] ?: emptyList()

                                if (productsInCat.isNotEmpty()) {
                                    // CORRECCIÓN: Usar el título traducido, o fallback al key capitalizado
                                    val title = categoryTitles[categoryKey] ?: categoryKey.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

                                    Column(modifier = Modifier.padding(bottom = 24.dp)) {
                                        SectionHeader(
                                            title = title,
                                            onSeeMoreClicked = {
                                                when (categoryKey) {
                                                    "ventas" -> navController.navigate(ScreenNavigation.Sales.route)
                                                    "rentas" -> navController.navigate(ScreenNavigation.Rents.route)
                                                    "servicios" -> navController.navigate(ScreenNavigation.Services.route)
                                                    "anuncios" -> navController.navigate(ScreenNavigation.Ads.route)
                                                }
                                            }
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        LazyRow(
                                            contentPadding = PaddingValues(horizontal = 16.dp),
                                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            items(productsInCat) { product ->
                                                ModernProductCard(product = product, navController = navController)
                                            }
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        } else {
                            // Estado Vacío (Sin resultados)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier
                                    .padding(32.dp)
                                    .padding(top = 40.dp)
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
                                    stringResource(id = R.string.sales_search_no_results_subtitle),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Indicador de Refresh
            PullToRefreshContainer(
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun SectionHeader(title: String, onSeeMoreClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold
        )
        TextButton(onClick = onSeeMoreClicked) {
            Text(stringResource(R.string.home_ver_mas), fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun ModernProductCard(product: Product, navController: NavController) {
    val priceColor = Color(0xFF448AFF)
    val format = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    val baseUrl = Constants.BASE_URL + "storage/"
    val imageUrl = if (product.images.isNotEmpty()) baseUrl + product.images[0] else ""

    // --- CORRECCIÓN FINAL: Traducir la etiqueta de la tarjeta ---
    val categoryLabel = when(product.category.lowercase()) {
        "ventas" -> stringResource(R.string.ventas_title)
        "rentas" -> stringResource(R.string.rentas_title)
        "servicios" -> stringResource(R.string.servicios_title)
        "anuncios" -> stringResource(R.string.anuncios_title)
        else -> product.category // Fallback por si llega algo raro
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .width(280.dp)
            .height(200.dp)
            .clickable {
                navController.navigate(ScreenNavigation.ProductDetail.createRoute(product.id))
            }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = product.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 0.4f * 200.dp.value
                        )
                    )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.title,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontSize = 18.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = format.format(product.price),
                        color = priceColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val defaultUser = stringResource(R.string.common_usuario_default)
                    val authorName = product.author?.firstName ?: defaultUser
                    val initial = authorName.take(1).uppercase()

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initial,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = authorName,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (product.category.isNotBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(bottomStart = 8.dp, topEnd = 16.dp),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        // USAMOS LA ETIQUETA TRADUCIDA AQUÍ
                        text = categoryLabel.uppercase(),
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}