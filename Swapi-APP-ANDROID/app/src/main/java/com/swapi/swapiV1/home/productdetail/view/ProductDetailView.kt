package com.swapi.swapiV1.home.productdetail.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.swapi.swapiV1.R
import com.swapi.swapiV1.home.model.dto.Product
import com.swapi.swapiV1.home.model.repository.HomeRepository
import com.swapi.swapiV1.home.productdetail.viewmodel.ProductDetailUiState
import com.swapi.swapiV1.home.productdetail.viewmodel.ProductDetailViewModel
import com.swapi.swapiV1.home.productdetail.viewmodel.ProductDetailViewModelFactory
import androidx.compose.material.icons.filled.Bookmark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailView(
    productId: String,
    navController: NavController
) {
    val factory = ProductDetailViewModelFactory(productId, HomeRepository())
    val viewModel: ProductDetailViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // ✅ NUEVO: escuchamos si está guardado
    val isSaved by viewModel.isSaved.collectAsStateWithLifecycle()

    val brandColor = Color(0xFF0064E0)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            // ✅ ACCIÓN REAL
                            viewModel.toggleSave()
                        }
                    ) {
                        Icon(
                            // ✅ ICONO DINÁMICO
                            imageVector = if (isSaved)
                                Icons.Default.Bookmark
                            else
                                Icons.Default.BookmarkBorder,
                            contentDescription = "Guardar",
                            tint = if (isSaved)
                                brandColor
                            else
                                MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            if (uiState is ProductDetailUiState.Success) {
                val product = (uiState as ProductDetailUiState.Success).product
                val context = LocalContext.current
                val toastMsgNoNum = stringResource(R.string.detail_toast_no_numero)
                val wppMsgTemplate = stringResource(R.string.detail_whatsapp_mensaje)
                val toastMsgNoWpp = stringResource(R.string.detail_toast_no_whatsapp)

                Surface(
                    shadowElevation = 16.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .navigationBarsPadding()
                    ) {
                        Button(
                            onClick = {
                                openWhatsApp(
                                    context,
                                    product.author.phone,
                                    product.title,
                                    toastMsgNoNum,
                                    wppMsgTemplate,
                                    toastMsgNoWpp
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = brandColor)
                        ) {
                            Text(
                                "Enviar mensaje",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is ProductDetailUiState.Loading ->
                    CircularProgressIndicator(color = brandColor)

                is ProductDetailUiState.Error ->
                    Text(state.message, color = MaterialTheme.colorScheme.error)

                is ProductDetailUiState.Success ->
                    ProductContentView(
                        product = state.product,
                        brandColor = brandColor
                    )
            }
        }
    }
}

@Composable
fun ProductContentView(
    product: Product,
    brandColor: Color
) {
    val scrollState = rememberScrollState()

    // Si ya creaste Constants.kt, usa esto:
    // val mainImage = if (product.images.isNotEmpty()) Constants.BASE_URL + "storage/" + product.images[0] else ""

    // Si NO has creado Constants.kt, usa tu url temporal:
    val baseUrl = "http://10.0.2.2:3000/storage/"
    val mainImage = if (product.images.isNotEmpty()) baseUrl + product.images[0] else ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 1. IMAGEN (Limpia, sin contador)
        Box(modifier = Modifier
            .height(350.dp)
            .fillMaxWidth()
            .background(Color.LightGray.copy(alpha = 0.2f)) // Placeholder sutil
        ) {
            AsyncImage(
                model = mainImage,
                contentDescription = product.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // AQUÍ BORRAMOS EL INDICADOR "1 / X"
        }

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 2. TÍTULO
            Text(
                text = product.title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 3. PRECIO
            Text(
                text = "$${product.price}",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onBackground
            )

            // Ubicación
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Chihuahua, Chih.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // --- DIVISOR ---
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(24.dp))

            // 4. DESCRIPCIÓN
            Text(
                text = "Descripción",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )

            // --- DIVISOR ---
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(24.dp))

            // 5. INFORMACIÓN DEL VENDEDOR
            Text(
                text = "Información del vendedor",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = product.author.firstName.take(1).uppercase(),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "${product.author.firstName} ${product.author.paternalSurname ?: ""}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Miembro de la comunidad ULSA",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
            // FIN: Eliminado el Spacer(80.dp) final.
        }
    }
}

private fun openWhatsApp(
    context: Context,
    phoneNumber: String?,
    productName: String,
    toastMsgNoNum: String,
    wppMsgTemplate: String,
    toastMsgNoWpp: String
) {
    if (phoneNumber.isNullOrBlank()) {
        Toast.makeText(context, toastMsgNoNum, Toast.LENGTH_SHORT).show()
        return
    }

    val message = String.format(wppMsgTemplate, productName)

    try {
        val intent = Intent(Intent.ACTION_VIEW)
        val url = "https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(message)}"
        intent.data = Uri.parse(url)
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, toastMsgNoWpp, Toast.LENGTH_SHORT).show()
    }
}