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
import androidx.compose.ui.platform.LocalContext
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
    // Inyección de dependencias manual para el ViewModel
    val factory = HomeViewModelFactory(HomeRepository())
    val viewModel: HomeViewModel = viewModel(factory = factory)

    // Observamos los estados de forma eficiente para el ciclo de vida
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchText by viewModel.searchText.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // --- LÓGICA DE AUTO-REFRESH (Comunicación entre pantallas) ---
    // Esto permite que si el usuario crea un post en otra pantalla y vuelve aquí,
    // la lista se actualice automáticamente sin que tenga que estirar la pantalla.
    val currentBackStack = navController.currentBackStackEntry
    val refreshHomeState by currentBackStack?.savedStateHandle
        ?.getStateFlow("refresh_home", false)
        ?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(false) }

    LaunchedEffect(refreshHomeState) {
        if (refreshHomeState) {
            viewModel.onRefresh()
            // Importante: Reseteamos la bandera a false para evitar bucles infinitos
            currentBackStack?.savedStateHandle?.set("refresh_home", false)
        }
    }

    // --- LÓGICA DE PULL-TO-REFRESH (Gesto manual) ---
    val pullRefreshState = rememberPullToRefreshState()

    // Si el usuario estira la pantalla, avisamos al ViewModel
    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            viewModel.onRefresh()
        }
    }

    // Sincronización inversa: Cuando el ViewModel termina de cargar (isRefreshing = false),
    // avisamos a la UI que esconda el indicador de carga.
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
                // Vincula el gesto físico con el estado del refresh
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
                    val errorMsg = ErrorMessageMapper.getMessage(context, state.message)
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = errorMsg,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                is HomeUIState.Success -> {
                    val userName by dataStore.userNameFlow.collectAsState(initial = "...")
                    val allProducts = state.products

                    // Filtrado local en tiempo real para una búsqueda instantánea
                    val filteredProducts = if (searchText.isNotBlank()) {
                        allProducts.filter {
                            it.title.contains(searchText, ignoreCase = true) ||
                                    it.description.contains(searchText, ignoreCase = true)
                        }
                    } else {
                        allProducts
                    }

                    // Definimos el orden estricto en el que queremos que aparezcan las secciones
                    val categoriesOrder = listOf("ventas", "rentas", "servicios", "anuncios")

                    // Agrupamos los productos una sola vez usando 'remember' para optimizar rendimiento
                    val productsByCategory = remember(filteredProducts) {
                        filteredProducts.groupBy { it.category.lowercase() }
                    }
                    val isEmpty = filteredProducts.isEmpty()

                    // Mapa para traducir las claves técnicas de la BD a texto de UI
                    val categoryTitles = mapOf(
                        "ventas" to stringResource(R.string.ventas_title),
                        "rentas" to stringResource(R.string.rentas_title),
                        "servicios" to stringResource(R.string.servicios_title),
                        "anuncios" to stringResource(R.string.anuncios_title)
                    )

                    // --- CONTENIDO PRINCIPAL SCROLLEABLE ---
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // 1. Barra de búsqueda superior
                        Spacer(modifier = Modifier.height(8.dp))
                        SwapiTopBar(
                            searchText = searchText,
                            onSearchTextChange = viewModel::onSearchTextChange
                        )

                        // 2. Mensaje de Bienvenida (Se oculta al buscar para ahorrar espacio)
                        AnimatedVisibility(visible = !isEmpty && searchText.isBlank()) {
                            Text(
                                text = stringResource(R.string.home_bienvenida, userName),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        // 3. Renderizado de Secciones
                        if (!isEmpty) {
                            categoriesOrder.forEach { categoryKey ->
                                val productsInCat = productsByCategory[categoryKey] ?: emptyList()

                                if (productsInCat.isNotEmpty()) {
                                    val title = categoryTitles[categoryKey]
                                        ?: categoryKey.replaceFirstChar {
                                            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                                        }

                                    Column(modifier = Modifier.padding(bottom = 24.dp)) {
                                        SectionHeader(
                                            title = title,
                                            onSeeMoreClicked = {
                                                // Navegación específica por categoría
                                                when (categoryKey) {
                                                    "ventas" -> navController.navigate(ScreenNavigation.Sales.route)
                                                    "rentas" -> navController.navigate(ScreenNavigation.Rents.route)
                                                    "servicios" -> navController.navigate(ScreenNavigation.Services.route)
                                                    "anuncios" -> navController.navigate(ScreenNavigation.Ads.route)
                                                }
                                            }
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Carrusel horizontal de productos
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
                            // Espacio extra al final para que el último elemento no quede pegado al borde
                            Spacer(modifier = Modifier.height(80.dp))
                        } else {
                            // Estado Vacío: Cuando la búsqueda no arroja resultados
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

            // Componente visual del indicador de carga (la ruedita superior)
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

    // --- CORRECCIÓN FINAL ---
    // Quitamos la concatenación "baseUrl + storage/"
    val imageUrl = if (product.images.isNotEmpty()) product.images[0] else ""

    // Traducción de la etiqueta flotante sobre la imagen
    val categoryLabel = when(product.category.lowercase()) {
        "ventas" -> stringResource(R.string.ventas_title)
        "rentas" -> stringResource(R.string.rentas_title)
        "servicios" -> stringResource(R.string.servicios_title)
        "anuncios" -> stringResource(R.string.anuncios_title)
        else -> product.category
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
        // Usamos un Box para apilar capas: Imagen al fondo -> Gradiente oscuro -> Texto encima
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = product.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Capa de gradiente negro: Esencial para que el texto blanco se lea
            // sobre cualquier tipo de foto (clara u oscura).
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            // El gradiente empieza un poco antes de la mitad para suavizar la transición
                            startY = 0.4f * 200.dp.value
                        )
                    )
            )

            // Información del producto alineada abajo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Columna izquierda: Título y Precio
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

                // Columna derecha: Avatar circular del autor
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val defaultUser = stringResource(R.string.common_usuario_default)
                    val authorName = product.author?.firstName ?: defaultUser
                    // Tomamos la primera letra del nombre para crear un avatar genérico
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

            // Etiqueta de Categoría (Esquina superior derecha)
            if (product.category.isNotBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(bottomStart = 8.dp, topEnd = 16.dp),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
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