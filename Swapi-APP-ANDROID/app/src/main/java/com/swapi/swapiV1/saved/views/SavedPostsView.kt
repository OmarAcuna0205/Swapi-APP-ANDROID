package com.swapi.swapiV1.saved.views

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.swapi.swapiV1.R
import com.swapi.swapiV1.home.views.CategoryProductCardView
import com.swapi.swapiV1.navigation.ScreenNavigation
import com.swapi.swapiV1.saved.viewmodel.SavedPostsViewModel
import com.swapi.swapiV1.saved.viewmodel.SavedUIState
import com.swapi.swapiV1.utils.ErrorMessageMapper
import com.swapi.swapiV1.utils.dismissKeyboardOnClick

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedPostsView(navController: NavController) {
    // Instanciamos el ViewModel que manejara la logica de negocio de esta pantalla
    val viewModel: SavedPostsViewModel = viewModel()

    // Recolectamos el estado de la UI (Carga, Error, Exito) de forma reactiva.
    // collectAsStateWithLifecycle asegura que la recoleccion se detenga si la app pasa a segundo plano.
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Efecto de lanzamiento unico: Se ejecuta solo la primera vez que se compone la vista
    // para solicitar al servidor la lista de posts guardados mas reciente.
    LaunchedEffect(Unit) {
        viewModel.loadSavedPosts()
    }

    var searchQuery by remember { mutableStateOf("") }
    val swapiBrandColor = Color(0xFF4A8BFF)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                    )
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                SavedTopBar(
                    searchQuery = searchQuery,
                    onQueryChange = { searchQuery = it },
                    brandColor = swapiBrandColor,
                    onBack = { navController.popBackStack() }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .dismissKeyboardOnClick(), // Oculta el teclado al tocar fuera del campo de busqueda
                contentAlignment = Alignment.Center
            ) {
                // Gestion de los estados de la UI usando un 'when' exhaustivo
                when (val state = uiState) {
                    is SavedUIState.Loading -> {
                        CircularProgressIndicator(color = swapiBrandColor)
                    }
                    is SavedUIState.Error -> {
                        // Mapeo del codigo de error del backend a un mensaje amigable para el usuario
                        val errorText = ErrorMessageMapper.getMessage(context, state.code)
                        Text(text = errorText, color = MaterialTheme.colorScheme.error)
                    }
                    is SavedUIState.Success -> {
                        val allListings = state.products

                        // Estado Vacio: Si el usuario no tiene guardados, mostramos un icono y mensaje
                        if (allListings.isEmpty()) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Bookmark,
                                    contentDescription = null,
                                    modifier = Modifier.size(72.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                )
                                Text(stringResource(R.string.saved_empty_state))
                            }
                        } else {
                            // Logica de Filtrado: Filtramos la lista localmente segun el texto de busqueda
                            val filteredListings = if (searchQuery.isBlank()) allListings else {
                                allListings.filter {
                                    it.title.contains(searchQuery, ignoreCase = true)
                                }
                            }

                            // Lista vertical optimizada para renderizar los productos
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(1),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(20.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(filteredListings, key = { it.id }) { product ->

                                    // Validacion de 'Soft Delete':
                                    // Comprobamos si la publicacion sigue activa en el servidor.
                                    val isDeleted = !product.isActive

                                    // Contenedor principal del item para permitir superposicion de elementos
                                    Box(modifier = Modifier.fillMaxWidth()) {

                                        // 1. Tarjeta del Producto
                                        // Aplicamos opacidad (alpha) si esta eliminada para dar feedback visual
                                        Box(modifier = Modifier.alpha(if (isDeleted) 0.6f else 1f)) {
                                            CategoryProductCardView(
                                                product = product,
                                                onClick = {
                                                    // Interceptamos el click:
                                                    // Si esta eliminada, mostramos un Toast informativo en lugar de navegar,
                                                    // evitando errores 404 en la pantalla de detalle.
                                                    if (isDeleted) {
                                                        Toast.makeText(
                                                            context,
                                                            context.getString(R.string.error_post_not_found),
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    } else {
                                                        navController.navigate(ScreenNavigation.ProductDetail.createRoute(product.id))
                                                    }
                                                }
                                            )
                                        }

                                        // 2. Boton de Eliminar Manual
                                        // Este boton solo aparece si la publicacion fue borrada por el autor (isDeleted),
                                        // permitiendo al usuario quitarla de su lista de guardados facilmente.
                                        if (isDeleted) {
                                            IconButton(
                                                onClick = {
                                                    // Llamada al ViewModel para actualizar la lista local y el backend
                                                    viewModel.removeSavedPost(product.id)
                                                    Toast.makeText(
                                                        context,
                                                        context.getString(R.string.msg_saved_removed),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                },
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd) // Posicionamos en la esquina superior derecha
                                                    .padding(8.dp)
                                                    .background(
                                                        MaterialTheme.colorScheme.surface,
                                                        CircleShape
                                                    )
                                                    .size(36.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = stringResource(R.string.action_eliminar_cd),
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
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
private fun SavedTopBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    brandColor: Color,
    onBack: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .windowInsetsPadding(WindowInsets.statusBars),
        shadowElevation = 10.dp,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 20.dp, top = 20.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.common_back_button_cd),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        stringResource(R.string.saved_titulo),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 20.dp),
                placeholder = { Text(stringResource(R.string.saved_buscar_placeholder)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = brandColor,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    disabledIndicatorColor = Color.Transparent
                )
            )
        }
    }
}