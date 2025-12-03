package com.swapi.swapiV1.productdetail.views

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
import androidx.compose.material.icons.filled.Bookmark
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
import com.swapi.swapiV1.home.model.repository.UserRepository
import com.swapi.swapiV1.productdetail.viewmodel.ProductDetailUiState
import com.swapi.swapiV1.productdetail.viewmodel.ProductDetailViewModel
import com.swapi.swapiV1.productdetail.viewmodel.ProductDetailViewModelFactory
import com.swapi.swapiV1.utils.Constants
import com.swapi.swapiV1.utils.ErrorMessageMapper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailView(
    productId: String,
    navController: NavController
) {
    val context = LocalContext.current

    // Instanciamos los repositorios necesarios
    // Usamos 'remember' para evitar que se re-creen en cada recomposición
    val homeRepository = remember { HomeRepository() }
    val userRepository = remember { UserRepository() }

    // Creamos la fábrica pasando las tres dependencias: ID, HomeRepo y UserRepo
    val factory = ProductDetailViewModelFactory(productId, homeRepository, userRepository)

    val viewModel: ProductDetailViewModel = viewModel(factory = factory)

    // Observamos los estados del ViewModel
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isSaved by viewModel.isSaved.collectAsStateWithLifecycle()

    val brandColor = Color(0xFF0064E0)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {}, // Título vacío para dejar ver la imagen
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_atras_cd),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    // Botón de Guardar/Favorito
                    IconButton(onClick = { viewModel.toggleSave() }) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = stringResource(R.string.detail_guardar),
                            tint = if (isSaved) brandColor else MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            // Solo mostramos el botón de WhatsApp si la carga fue exitosa
            if (uiState is ProductDetailUiState.Success) {
                val product = (uiState as ProductDetailUiState.Success).product

                // Preparamos los textos aquí para no pasarlos uno por uno
                val wppMsgTemplate = stringResource(R.string.detail_whatsapp_mensaje)
                val toastMsgNoNum = stringResource(R.string.detail_toast_no_numero)
                val toastMsgNoWpp = stringResource(R.string.detail_toast_no_whatsapp)

                Surface(
                    shadowElevation = 16.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .navigationBarsPadding() // Respeta la barra de gestos del sistema
                    ) {
                        Button(
                            onClick = {
                                openWhatsApp(context, product.author.phone, product.title, wppMsgTemplate, toastMsgNoNum, toastMsgNoWpp)
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = brandColor)
                        ) {
                            Text(
                                stringResource(R.string.product_enviar_mensaje),
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
                is ProductDetailUiState.Loading -> {
                    CircularProgressIndicator(color = brandColor)
                }
                is ProductDetailUiState.Error -> {
                    // Traducimos el código de error a texto real
                    val errorText = ErrorMessageMapper.getMessage(context, state.messageCode)
                    Text(
                        text = errorText,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                is ProductDetailUiState.Success -> {
                    ProductContentView(product = state.product)
                }
            }
        }
    }
}

@Composable
fun ProductContentView(product: Product) {
    val scrollState = rememberScrollState()
    // Construcción de la URL de la imagen
    val mainImage = if (product.images.isNotEmpty()) Constants.BASE_URL + "storage/" + product.images[0] else ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- IMAGEN PRINCIPAL ---
        Box(
            modifier = Modifier
                .height(350.dp)
                .fillMaxWidth()
                .background(Color.LightGray.copy(alpha = 0.2f))
        ) {
            AsyncImage(
                model = mainImage,
                contentDescription = product.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // --- CONTENIDO ---
        Column(modifier = Modifier.padding(16.dp)) {
            // Título
            Text(
                text = product.title,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, fontSize = 24.sp),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Precio
            Text(
                text = "$${product.price}",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Ubicación (Dummy por ahora)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.product_ubicacion_default),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(24.dp))

            // Descripción
            Text(
                text = stringResource(R.string.detail_descripcion),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(24.dp))

            // Info Vendedor
            Text(
                text = stringResource(R.string.product_info_vendedor),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar con inicial
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

                // Nombre completo
                Column {
                    Text(
                        text = "${product.author.firstName} ${product.author.paternalSurname ?: ""}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = stringResource(R.string.product_miembro_comunidad),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Espacio extra al final para que el botón flotante no tape contenido
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// Lógica de Intent para abrir WhatsApp
private fun openWhatsApp(
    context: Context,
    phoneNumber: String?,
    productName: String,
    msgTemplate: String,
    errorNoNum: String,
    errorNoApp: String
) {
    if (phoneNumber.isNullOrBlank()) {
        Toast.makeText(context, errorNoNum, Toast.LENGTH_SHORT).show()
        return
    }

    // Formateamos el mensaje: "Hola, me interesa tu producto: Laptop..."
    val message = String.format(msgTemplate, productName)

    try {
        val intent = Intent(Intent.ACTION_VIEW)
        // Usamos la API universal de WhatsApp
        val url = "https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(message)}"
        intent.data = Uri.parse(url)
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, errorNoApp, Toast.LENGTH_SHORT).show()
    }
}