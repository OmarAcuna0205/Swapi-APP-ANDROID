package com.swapi.androidClassMp1.home.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
            is HomeUIState.Loading -> CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary // Color del spinner
            )

            is HomeUIState.Error -> Text(
                text = state.message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )

            is HomeUIState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp) // Más espacio arriba y abajo
                ) {
                    items(state.sections) { section ->
                        Column(modifier = Modifier.padding(bottom = 24.dp)) { // Más espacio entre secciones
                            Text(
                                text = section.sectionTitle,
                                style = MaterialTheme.typography.headlineMedium, // Título de sección más grande
                                fontWeight = FontWeight.ExtraBold, // Más énfasis
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp) // Padding ajustado
                            )
                            Spacer(modifier = Modifier.height(8.dp)) // Menos espacio aquí para que se sienta más compacto

                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp) // Más espacio entre tarjetas
                            ) {
                                items(section.listings) { listing ->
                                    ModernProductCard(listing = listing, navController = navController)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModernProductCard(listing: ListingDto, navController: NavController) {
    // --- CAMBIO AQUÍ ---
    // Un azul vibrante y moderno. Puedes experimentar con otros códigos de color.
    val priceColor = Color(0xFF448AFF)

    // Formateador de moneda
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
            // Imagen de fondo
            AsyncImage(
                model = listing.imageUrl,
                contentDescription = listing.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Gradiente oscuro para legibilidad
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

            // Sección inferior con título/precio y perfil
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Columna para Título y Precio
                Column(
                    modifier = Modifier.weight(1f)
                ) {
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
                        color = priceColor, // <-- ¡COLOR DEL PRECIO CAMBIADO A AZUL!
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Columna para Avatar y Nombre
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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

            // Etiqueta de Categoría
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