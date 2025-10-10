package com.swapi.androidClassMp1.home.productdetail.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle // <-- Usando la dependencia recomendada
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.swapi.androidClassMp1.home.model.dto.ListingDto
import com.swapi.androidClassMp1.home.model.network.HomeApiImpl // <-- Importante
import com.swapi.androidClassMp1.home.model.repository.HomeRepository
import com.swapi.androidClassMp1.home.productdetail.viewmodel.ProductDetailUiState
import com.swapi.androidClassMp1.home.productdetail.viewmodel.ProductDetailViewModel
import com.swapi.androidClassMp1.home.productdetail.viewmodel.ProductDetailViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    navController: NavController
) {
    // --- LÍNEA CORREGIDA AQUÍ ---
    val factory = ProductDetailViewModelFactory(productId, HomeRepository(HomeApiImpl.retrofitApi))
    val viewModel: ProductDetailViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Lógica para guardar */ }) {
                        Icon(Icons.Default.BookmarkBorder, contentDescription = "Guardar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is ProductDetailUiState.Loading -> CircularProgressIndicator()
                is ProductDetailUiState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
                is ProductDetailUiState.Success -> ProductContentView(product = state.product)
            }
        }
    }
}

@Composable
fun ProductContentView(product: ListingDto) {
    val context = LocalContext.current

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        AsyncImage(
            model = product.imageUrl,
            contentDescription = product.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(product.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("$${product.price} ${product.currency}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Descripción", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(product.description, style = MaterialTheme.typography.bodyLarge)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Vendedor", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                AsyncImage(model = product.user.avatarUrl, contentDescription = null, modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape))
                Spacer(modifier = Modifier.width(12.dp))
                Text(product.user.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { openWhatsApp(context, product.user.phoneNumber, product.title) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Contactar por WhatsApp", fontSize = 16.sp)
            }
        }
    }
}

private fun openWhatsApp(context: Context, phoneNumber: String?, productName: String) {
    if (phoneNumber.isNullOrBlank()) {
        Toast.makeText(context, "El vendedor no especificó un número.", Toast.LENGTH_SHORT).show()
        return
    }
    val message = "Hola, me interesa tu producto '$productName' que vi en Swapi."
    try {
        val intent = Intent(Intent.ACTION_VIEW)
        val url = "https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(message)}"
        intent.data = Uri.parse(url)
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "No se pudo abrir WhatsApp.", Toast.LENGTH_SHORT).show()
    }
}