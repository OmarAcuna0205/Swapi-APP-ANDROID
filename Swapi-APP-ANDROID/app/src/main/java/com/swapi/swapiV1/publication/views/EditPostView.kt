package com.swapi.swapiV1.publication.views

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.swapi.swapiV1.home.model.repository.HomeRepository
import com.swapi.swapiV1.home.model.repository.PostRepository
import com.swapi.swapiV1.publication.viewmodel.EditPostViewModel
import com.swapi.swapiV1.publication.viewmodel.EditPostViewModelFactory
import com.swapi.swapiV1.publication.viewmodel.EditUiState
import com.swapi.swapiV1.utils.Constants
import com.swapi.swapiV1.utils.ErrorMessageMapper
import java.text.DecimalFormat

/**
 * @param postId ID único del producto a editar, recibido desde la navegación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPostView(
    postId: String,
    navController: NavController
) {
    // Inyección de dependencias manual para el ViewModel
    val factory = EditPostViewModelFactory(postId, HomeRepository(), PostRepository())
    val viewModel: EditPostViewModel = viewModel(factory = factory)

    // Observamos los estados reactivos del ViewModel (Carga de datos y Resultado de actualización)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val updateSuccess by viewModel.updateSuccess.collectAsStateWithLifecycle()
    val errorCode by viewModel.errorCode.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val brandColor = Color(0xFF0064E0)

    // Estados locales para el formulario. Se inicializan vacíos y se llenan al cargar los datos.
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var currentImageUrl by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }

    // Mapa para traducir entre lo que ve el usuario (Español) y lo que espera el backend (claves en minúsculas)
    val categoriasMap = mapOf(
        stringResource(R.string.ventas_title) to "ventas",
        stringResource(R.string.rentas_title) to "rentas",
        stringResource(R.string.new_pub_categoria_info) to "anuncios",
        stringResource(R.string.servicios_title) to "servicios"
    )
    // Mapa inverso para precargar el Dropdown correctamente
    val categoriasBackendMap = categoriasMap.entries.associate { (k, v) -> v to k }
    val categoriasVisuales = categoriasMap.keys.toList()

    // EFECTO DE CARGA INICIAL:
    // Cuando el estado cambia a Success (datos recibidos del backend), llenamos los campos del formulario.
    // El "if (titulo.isEmpty())" evita sobrescribir lo que el usuario ya haya editado si hay recomposiciones.
    LaunchedEffect(uiState) {
        if (uiState is EditUiState.Success) {
            val product = (uiState as EditUiState.Success).product
            if (titulo.isEmpty()) {
                titulo = product.title
                descripcion = product.description

                // Formateamos el precio para quitar decimales innecesarios (.00)
                val df = DecimalFormat("#.##")
                precio = df.format(product.price)

                // Mapeamos la categoría del backend a la etiqueta visual
                val catBackend = product.category.lowercase()
                categoria = categoriasBackendMap[catBackend] ?: catBackend.replaceFirstChar { it.uppercase() }

                if (product.images.isNotEmpty()) {
                    currentImageUrl = Constants.BASE_URL + "storage/" + product.images[0]
                }
            }
        }
    }

    // EFECTO DE RESULTADO DE ACTUALIZACIÓN:
    // Reacciona si la operación de guardar fue exitosa o falló.
    LaunchedEffect(updateSuccess) {
        if (updateSuccess == true) {
            val msg = ErrorMessageMapper.getMessage(context, "POST_UPDATED_SUCCESS")
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

            // Indicamos a la pantalla anterior (Mis Posts) que debe refrescarse
            navController.previousBackStackEntry?.savedStateHandle?.set("refresh_home", true)
            navController.popBackStack()
            viewModel.resetState()
        } else if (updateSuccess == false) {
            // Si falló, mostramos el error específico (ej. malas palabras)
            val errorMsg = ErrorMessageMapper.getMessage(context, errorCode ?: "ERROR_UPDATE_POST")
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile_editar)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_back_button_cd))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    )
                )
                .padding(padding)
        ) {
            // Renderizado condicional según el estado de carga
            when (val state = uiState) {
                is EditUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = brandColor)
                }
                is EditUiState.Error -> {
                    val msg = ErrorMessageMapper.getMessage(context, state.msg)
                    Text(text = msg, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                }
                is EditUiState.Success -> {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Campo Título
                        OutlinedTextField(
                            value = titulo,
                            onValueChange = { titulo = it },
                            label = { Text(stringResource(R.string.new_pub_titulo_label)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Campo Descripción (multilínea)
                        OutlinedTextField(
                            value = descripcion,
                            onValueChange = { descripcion = it },
                            label = { Text(stringResource(R.string.new_pub_descripcion_label)) },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 4,
                            maxLines = 8
                        )

                        // Campo Precio (solo números y punto)
                        OutlinedTextField(
                            value = precio,
                            onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) precio = it },
                            label = { Text(stringResource(R.string.new_pub_precio_label)) },
                            prefix = { Text("$") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Selector de Categoría (Dropdown)
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = categoria,
                                onValueChange = {},
                                readOnly = true, // Es de solo lectura porque se usa el menú
                                label = { Text(stringResource(R.string.new_pub_categoria_label)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                categoriasVisuales.forEach { option ->
                                    DropdownMenuItem(text = { Text(option) }, onClick = { categoria = option; expanded = false })
                                }
                            }
                        }

                        // Vista previa de la imagen actual (si existe)
                        if (currentImageUrl.isNotEmpty()) {
                            Text(
                                stringResource(R.string.edit_imagen_actual),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Box(
                                modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                AsyncImage(model = currentImageUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Botón de Guardar
                        Button(
                            onClick = {
                                if (titulo.isNotBlank() && precio.isNotBlank() && categoria.isNotBlank()) {
                                    // Traducimos la categoría visual de vuelta a la clave del backend
                                    val catBackend = categoriasMap[categoria] ?: "ventas"
                                    viewModel.saveChanges(titulo, descripcion, precio, catBackend)
                                } else {
                                    Toast.makeText(context, context.getString(R.string.edit_completa_campos), Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(55.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = brandColor, contentColor = Color.White)
                        ) {
                            Text(stringResource(R.string.common_guardar_cambios), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}