package com.swapi.swapiV1.home.productdetail.view

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource // <-- Import
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.swapi.swapiV1.R // <-- Import
import com.swapi.swapiV1.home.model.dto.ListingDto
import com.swapi.swapiV1.home.model.network.HomeApiImpl
import com.swapi.swapiV1.home.model.repository.HomeRepository
import com.swapi.swapiV1.home.productdetail.viewmodel.ProductDetailUiState
import com.swapi.swapiV1.home.productdetail.viewmodel.ProductDetailViewModel
import com.swapi.swapiV1.home.productdetail.viewmodel.ProductDetailViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailView(
    productId: String,
    navController: NavController
) {
    val factory = ProductDetailViewModelFactory(productId, HomeRepository(HomeApiImpl.retrofitApi))
    val viewModel: ProductDetailViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val swapiBrandColor = Color(0xFF4A8BFF)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.detail_titulo)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back_button_cd)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Guardar producto */ }) {
                        Icon(
                            Icons.Default.BookmarkBorder,
                            contentDescription = stringResource(R.string.detail_guardar)
                        )
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
                is ProductDetailUiState.Loading -> CircularProgressIndicator(color = swapiBrandColor)
                is ProductDetailUiState.Error -> Text(
                    state.message,
                    color = MaterialTheme.colorScheme.error
                )
                is ProductDetailUiState.Success -> ProductContentView(
                    product = state.product,
                    brandColor = swapiBrandColor
                )
            }
        }
    }
}

@Composable
fun ProductContentView(
    product: ListingDto,
    brandColor: Color
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // --- ¡AQUÍ SACAMOS LAS STRINGS! ---
    // Las sacamos aquí, dentro del Composable, para pasarlas a la función normal.
    val toastMsgNoNum = stringResource(R.string.detail_toast_no_numero)
    val wppMsgTemplate = stringResource(R.string.detail_whatsapp_mensaje) // El "template"
    val toastMsgNoWpp = stringResource(R.string.detail_toast_no_whatsapp)
    // -----------------------------------

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // ... (AsyncImage y primer Column se quedan igual)
        AsyncImage(
            model = product.imageUrl,
            contentDescription = product.title,
            // ...
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ... (Título y Precio se quedan igual)

            // ... (Descripción se queda igual)
            Surface(/*...*/) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.detail_descripcion),/*...*/)
                    Text(product.description, /*...*/)
                }
            }

            // ... (Vendedor se queda igual)
            Surface(/*...*/) {
                Row(modifier = Modifier.padding(16.dp)) {
                    AsyncImage(
                        model = product.user.avatarUrl,
                        contentDescription = stringResource(R.string.detail_avatar_vendedor_cd),
                        // ...
                    )
                    // ...
                    Column {
                        Text(product.user.name, /*...*/)
                        Text(stringResource(R.string.detail_vendedor_verificado), /*...*/)
                    }
                }
            }


            // --- ¡AQUÍ PASAMOS LAS STRINGS! ---
            Button(
                onClick = {
                    openWhatsApp(
                        context,
                        product.user.phoneNumber,
                        product.title,
                        // Le pasamos las strings como texto normal
                        toastMsgNoNum = toastMsgNoNum,
                        wppMsgTemplate = wppMsgTemplate,
                        toastMsgNoWpp = toastMsgNoWpp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                // ... (el resto del botón)
            ) {
                Text(
                    stringResource(R.string.detail_boton_whatsapp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// --- ¡AQUÍ RECIBIMOS LAS STRINGS! ---
// La función ahora acepta las strings como parámetros normales
private fun openWhatsApp(
    context: Context,
    phoneNumber: String?,
    productName: String,
    toastMsgNoNum: String, // <-- Parámetro nuevo
    wppMsgTemplate: String, // <-- Parámetro nuevo
    toastMsgNoWpp: String // <-- Parámetro nuevo
) {
    if (phoneNumber.isNullOrBlank()) {
        Toast.makeText(context, toastMsgNoNum, Toast.LENGTH_SHORT).show() // <-- Se usa el parámetro
        return
    }

    // Usamos String.format para meter el nombre del producto en el template
    val message = String.format(wppMsgTemplate, productName)

    try {
        val intent = Intent(Intent.ACTION_VIEW)
        val url = "https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(message)}"
        intent.data = Uri.parse(url)
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, toastMsgNoWpp, Toast.LENGTH_SHORT).show() // <-- Se usa el parámetro
    }
}