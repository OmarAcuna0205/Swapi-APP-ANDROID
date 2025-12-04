package com.swapi.swapiV1.publication.views

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
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
import com.swapi.swapiV1.publication.viewmodel.NewPublicationViewModel
import com.swapi.swapiV1.utils.ErrorMessageMapper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPublicationView(
    navController: NavController,
    viewModel: NewPublicationViewModel = viewModel()
) {
    // Variables de estado y contexto para acceder a recursos y mostrar Toasts.
    val context = LocalContext.current

    // Observamos los estados del ViewModel de manera segura para el ciclo de vida.
    // 'isLoading' controla la visualización del indicador de progreso.
    // 'publishSuccess' indica si la operación fue exitosa o fallida.
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val publishSuccess by viewModel.publishSuccess.collectAsStateWithLifecycle()

    // Observamos el código de error específico del backend para mostrar mensajes precisos.
    val errorCode by viewModel.errorCode.collectAsStateWithLifecycle()

    // Estados locales para los campos del formulario.
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) } // Controla la visibilidad del menú desplegable.
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) } // URI de la imagen seleccionada.

    // Efecto secundario: Maneja la respuesta de la operación de publicación.
    LaunchedEffect(publishSuccess) {
        if (publishSuccess == true) {
            // Éxito: Mostramos mensaje, refrescamos Home y navegamos atrás.
            val successMsg = context.getString(R.string.msg_post_created_success)
            Toast.makeText(context, successMsg, Toast.LENGTH_LONG).show()

            // Comunicación entre fragmentos: Indicamos al Home que debe recargar la lista.
            navController.previousBackStackEntry?.savedStateHandle?.set("refresh_home", true)
            navController.popBackStack()
            viewModel.resetState() // Limpiamos el estado del ViewModel.
        } else if (publishSuccess == false) {
            // Error: Traducimos el código de error y mostramos un mensaje al usuario.
            val errorMsg = ErrorMessageMapper.getMessage(context, errorCode)
            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()

            viewModel.resetState()
        }
    }

    // Selector de imágenes (Photo Picker):
    // Utiliza el contrato estándar de Android para seleccionar imágenes de la galería de manera segura.
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> if (uri != null) selectedImageUri = uri }
    )

    // Mapa de categorías para la traducción UI <-> Backend.
    val categoriasMap = mapOf(
        stringResource(R.string.ventas_title) to "ventas",
        stringResource(R.string.rentas_title) to "rentas",
        stringResource(R.string.new_pub_categoria_info) to "anuncios",
        stringResource(R.string.servicios_title) to "servicios"
    )
    val categoriasVisuales = categoriasMap.keys.toList()

    val swapiBrandColor = Color(0xFF0064E0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.new_pub_titulo)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back_button_cd)
                        )
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
                        listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    )
                )
                .padding(padding)
        ) {
            // Indicador de carga superpuesto
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = swapiBrandColor
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Campos de texto del formulario
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text(stringResource(R.string.new_pub_titulo_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading // Deshabilita interacción durante la carga
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text(stringResource(R.string.new_pub_descripcion_label)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 4,
                    maxLines = 8,
                    enabled = !isLoading
                )

                // Validación de entrada numérica para el precio
                OutlinedTextField(
                    value = precio,
                    onValueChange = { if (it.all { char -> char.isDigit() }) precio = it },
                    label = { Text(stringResource(R.string.new_pub_precio_label)) },
                    prefix = { Text("$") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                )

                // Menú desplegable para selección de categoría
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { if (!isLoading) expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = categoria,
                        onValueChange = {},
                        readOnly = true, // El usuario debe seleccionar del menú
                        label = { Text(stringResource(R.string.new_pub_categoria_label)) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categoriasVisuales.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    categoria = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // Área de selección de imagen
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .clickable(enabled = !isLoading) {
                            // Lanza el selector de imágenes (solo imágenes)
                            galleryLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri == null) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = stringResource(R.string.new_pub_imagen_cd),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(48.dp)
                        )
                    } else {
                        // Muestra la imagen seleccionada utilizando Coil
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = stringResource(R.string.new_pub_imagen_seleccionada_cd),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // Botón de acción principal
                Button(
                    onClick = {
                        // Validación de campos obligatorios antes de enviar al ViewModel
                        if (titulo.isNotBlank() && precio.isNotBlank() && categoria.isNotBlank()) {
                            val backendCategory = categoriasMap[categoria] ?: "ventas"
                            viewModel.publish(
                                context = context,
                                title = titulo,
                                description = descripcion,
                                price = precio,
                                category = backendCategory,
                                imageUri = selectedImageUri
                            )
                        } else {
                            val validationMsg = context.getString(R.string.new_pub_llenar_campos)
                            Toast.makeText(context, validationMsg, Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(14.dp),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = swapiBrandColor,
                        contentColor = Color.White
                    )
                ) {
                    if (isLoading) {
                        Text(stringResource(R.string.common_publicando), fontSize = 18.sp)
                    } else {
                        Text(
                            stringResource(R.string.new_pub_boton_publicar),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}