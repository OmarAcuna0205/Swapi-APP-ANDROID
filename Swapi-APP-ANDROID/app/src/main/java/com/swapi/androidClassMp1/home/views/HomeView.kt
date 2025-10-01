package com.swapi.androidClassMp1.home.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.swapi.androidClassMp1.home.model.dto.HomeSectionDto
import com.swapi.androidClassMp1.home.model.dto.ListingDto
import com.swapi.androidClassMp1.home.model.network.HomeApiImpl
import com.swapi.androidClassMp1.home.model.repository.HomeRepository
import com.swapi.androidClassMp1.home.viewmodel.HomeUIState
import com.swapi.androidClassMp1.home.viewmodel.HomeViewModel
import com.swapi.androidClassMp1.home.viewmodel.HomeViewModelFactory

@Composable
fun HomeView() {
    // 1. Creamos las dependencias aquí mismo. En apps más grandes se usaría un inyector de dependencias como Hilt.
    val repository = HomeRepository(HomeApiImpl.retrofitApi)
    val factory = HomeViewModelFactory(repository)
    val homeViewModel: HomeViewModel = viewModel(factory = factory)

    // 2. Observamos el estado de la UI
    val uiState = homeViewModel.uiState

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        when (uiState) {
            is HomeUIState.Loading -> LoadingScreen()
            is HomeUIState.Success -> HomeScreenContent(sections = uiState.sections)
            is HomeUIState.Error -> ErrorScreen(message = uiState.message)
        }
    }
}

@Composable
fun HomeScreenContent(sections: List<HomeSectionDto>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(sections) { section ->
            HomeSection(section)
        }
    }
}

@Composable
fun HomeSection(section: HomeSectionDto) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(
            text = section.sectionTitle,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(section.listings) { listing ->
                ListingCard(listing)
            }
        }
    }
}

@Composable
fun ListingCard(listing: ListingDto) {
    Card(
        modifier = Modifier.width(280.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.height(320.dp)) {
            // Imagen de fondo
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(listing.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = listing.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradiente oscuro para que el texto sea legible
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black),
                            startY = 400f
                        )
                    )
            )

            // Contenido de la tarjeta
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom // Alineamos todo abajo
            ) {
                Text(
                    text = listing.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${String.format("%,.2f", listing.price)} ${listing.currency}",
                    color = MaterialTheme.colorScheme.primary, // Un color que resalte
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = listing.user.avatarUrl,
                        contentDescription = "Avatar de ${listing.user.name}",
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = listing.user.name,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f) // Blanco con transparencia
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = message, color = MaterialTheme.colorScheme.error, fontSize = 18.sp)
    }
}