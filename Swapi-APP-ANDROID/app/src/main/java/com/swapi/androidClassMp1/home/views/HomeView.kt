package com.swapi.androidClassMp1.home.views

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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.swapi.androidClassMp1.home.model.dto.ListingDto
import com.swapi.androidClassMp1.home.model.network.HomeApiImpl
import com.swapi.androidClassMp1.home.model.repository.HomeRepository
import com.swapi.androidClassMp1.home.viewmodel.HomeUIState
import com.swapi.androidClassMp1.home.viewmodel.HomeViewModel
import com.swapi.androidClassMp1.home.viewmodel.HomeViewModelFactory
import com.swapi.androidClassMp1.navigation.ScreenNavigation
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeView(navController: NavController) {
    val factory = HomeViewModelFactory(HomeRepository(HomeApiImpl.retrofitApi))
    val viewModel: HomeViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize(),
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        // CAMBIO CLAVE: Quitamos el padding manual de aquí.
                        // El Scaffold ya lo maneja a través del NavHost.
                        .padding(top = 8.dp)
                ) {
                    state.sections.forEach { section ->
                        Column(modifier = Modifier.padding(bottom = 24.dp)) {
                            SectionHeader(
                                title = section.sectionTitle,
                                onSeeMoreClicked = {
                                    when (section.sectionTitle) {
                                        "Ventas" -> navController.navigate(ScreenNavigation.Ventas.route)
                                        "Rentas" -> navController.navigate(ScreenNavigation.Rentas.route)
                                        "Servicios" -> navController.navigate(ScreenNavigation.Servicios.route)
                                        "Anuncios" -> navController.navigate(ScreenNavigation.Anuncios.route)
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
                    // Agregamos un espacio al final para que el último elemento no quede pegado
                    // a la barra de navegación al hacer scroll hasta el fondo.
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// Las funciones SectionHeader y ModernProductCard se quedan igual
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
            Text("Ver más", fontWeight = FontWeight.SemiBold)
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
                        contentDescription = "Avatar de ${listing.user.name}",
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