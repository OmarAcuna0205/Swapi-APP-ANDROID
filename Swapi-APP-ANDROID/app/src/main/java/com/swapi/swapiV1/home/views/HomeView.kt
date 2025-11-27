package com.swapi.swapiV1.home.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.swapi.swapiV1.home.model.dto.Product // <--- USAMOS PRODUCT
import com.swapi.swapiV1.home.model.network.HomeApiImpl
import com.swapi.swapiV1.home.model.repository.HomeRepository
import com.swapi.swapiV1.home.viewmodel.HomeUIState
import com.swapi.swapiV1.home.viewmodel.HomeViewModel
import com.swapi.swapiV1.home.viewmodel.HomeViewModelFactory
import com.swapi.swapiV1.navigation.ScreenNavigation
import com.swapi.swapiV1.utils.datastore.DataStoreManager
import com.swapi.swapiV1.utils.dismissKeyboardOnClick
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeView(
    navController: NavController,
    dataStore: DataStoreManager
) {
    // Inicializamos el ViewModel (sin parámetros en el Repo si usas la versión simple)
    val factory = HomeViewModelFactory(HomeRepository())
    val viewModel: HomeViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val showSearchBar by viewModel.showSearchBar.collectAsStateWithLifecycle()
    val searchText by viewModel.searchText.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            SwapiTopBar(
                showSearchBar = showSearchBar,
                searchText = searchText,
                onSearchTextChange = viewModel::onSearchTextChange,
                onToggleSearchBar = viewModel::onToggleSearchBar,
                onSearchAction = viewModel::onSearchSubmit
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .dismissKeyboardOnClick(),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is HomeUIState.Loading -> CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                is HomeUIState.Error -> Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
                is HomeUIState.Success -> {
                    val userName by dataStore.userNameFlow.collectAsState(initial = "...")
                    val allProducts = state.products

                    // --- FILTRADO LOCAL (Búsqueda) ---
                    val filteredProducts = if (searchText.isNotBlank()) {
                        allProducts.filter {
                            it.title.contains(searchText, ignoreCase = true) ||
                                    it.description.contains(searchText, ignoreCase = true)
                        }
                    } else {
                        allProducts
                    }

                    // --- AGRUPADO POR CATEGORÍAS ---
                    // El backend devuelve una lista plana, aquí la organizamos para que se vea bonita
                    // Definimos el orden que queremos mostrar
                    val categoriesOrder = listOf("ventas", "rentas", "servicios", "anuncios")

                    // Agrupamos los productos
                    val productsByCategory = remember(filteredProducts) {
                        filteredProducts.groupBy { it.category.lowercase() }
                    }

                    val isEmpty = filteredProducts.isEmpty()

                    // Muestra la lista SÓLO si NO está vacía
                    AnimatedVisibility(
                        visible = !isEmpty,
                        enter = fadeIn(tween(300)),
                        exit = fadeOut(tween(300))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            if (!showSearchBar) {
                                Text(
                                    text = stringResource(R.string.home_bienvenida, userName),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            // Iteramos sobre las categorías en orden
                            categoriesOrder.forEach { categoryKey ->
                                val productsInCat = productsByCategory[categoryKey] ?: emptyList()

                                if (productsInCat.isNotEmpty()) {
                                    // Convertimos "ventas" a "Ventas" para el título
                                    val title = categoryKey.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

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
                        }
                    }

                    // Muestra "Sin Resultados"
                    AnimatedVisibility(
                        visible = isEmpty && showSearchBar,
                        enter = fadeIn(tween(300)),
                        exit = fadeOut(tween(300))
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .padding(32.dp)
                                .align(Alignment.Center)
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

    // Construimos la URL de la imagen (Asegúrate que esta IP sea la de tu compu)
    val baseUrl = "http://192.168.1.69:3000/storage/"
    val imageUrl = if (product.images.isNotEmpty()) baseUrl + product.images[0] else ""

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

                // Sección del Autor (Avatar con inicial)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = product.author.firstName.take(1).uppercase(),
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = product.author.firstName,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Etiqueta de Categoría
            if (product.category.isNotBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(bottomStart = 8.dp, topEnd = 16.dp),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = product.category.uppercase(),
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