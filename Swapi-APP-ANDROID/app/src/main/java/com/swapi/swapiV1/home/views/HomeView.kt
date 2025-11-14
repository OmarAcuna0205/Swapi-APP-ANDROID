package com.swapi.swapiV1.home.views

// --- CAMBIO: Imports añadidos para la animación y el icono ---
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
// --------------------------------------------------------
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
import androidx.compose.material.icons.filled.Search // <-- Import añadido
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign // <-- Import añadido
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.swapi.swapiV1.R
import com.swapi.swapiV1.components.topbar.SwapiTopBar
import com.swapi.swapiV1.home.model.dto.ListingDto
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
    val factory = HomeViewModelFactory(HomeRepository(HomeApiImpl.retrofitApi))
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

                    // --- CAMBIO: Lógica de visibilidad añadida ---
                    val allListingsEmpty = state.sections.all { it.listings.isEmpty() }

                    // Muestra la lista SÓLO si NO está vacía
                    AnimatedVisibility(
                        visible = !allListingsEmpty,
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

                            state.sections.forEach { section ->
                                Column(modifier = Modifier.padding(bottom = 24.dp)) {
                                    SectionHeader(
                                        title = section.sectionTitle,
                                        onSeeMoreClicked = {
                                            when (section.sectionTitle) {
                                                "Ventas" -> navController.navigate(ScreenNavigation.Sales.route) // Idealmente, esto usaría una key, no "Ventas"
                                                "Rentas" -> navController.navigate(ScreenNavigation.Rents.route)
                                                "Servicios" -> navController.navigate(ScreenNavigation.Services.route)
                                                "Anuncios" -> navController.navigate(ScreenNavigation.Ads.route)
                                            }
                                        }
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    LazyRow(
                                        contentPadding = PaddingValues(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        items(section.listings) { listing ->
                                            ModernProductCard(listing = listing, navController = navController)
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // Muestra "Sin Resultados" SÓLO si la lista está vacía Y la barra de búsqueda está activa
                    AnimatedVisibility(
                        visible = allListingsEmpty && showSearchBar,
                        enter = fadeIn(tween(300)),
                        exit = fadeOut(tween(300))
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .padding(32.dp)
                                .align(Alignment.Center) // Centra el mensaje
                        ) {
                            Icon(
                                Icons.Filled.Search,
                                contentDescription = null,
                                modifier = Modifier.size(72.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                            Text(
                                stringResource(id = R.string.sales_search_no_results_title),
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
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

// ... (SectionHeader y ModernProductCard se quedan igual que en tu archivo original)
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
fun ModernProductCard(listing: ListingDto, navController: NavController) {
    val priceColor = Color(0xFF448AFF) // Azul
    val format = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    format.currency = java.util.Currency.getInstance(listing.currency)

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .width(280.dp)
            .height(200.dp)
            .clickable {
                navController.navigate(ScreenNavigation.ProductDetail.createRoute(listing.id))
            }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = listing.imageUrl,
                contentDescription = listing.title,
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
                        text = listing.title,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontSize = 18.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = format.format(listing.price),
                        color = priceColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = listing.user.avatarUrl,
                        contentDescription = stringResource(R.string.home_card_avatar_cd, listing.user.name),
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = listing.user.name.split(" ").first(),
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (listing.category.isNotBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(bottomStart = 8.dp, topEnd = 16.dp),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = listing.category,
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