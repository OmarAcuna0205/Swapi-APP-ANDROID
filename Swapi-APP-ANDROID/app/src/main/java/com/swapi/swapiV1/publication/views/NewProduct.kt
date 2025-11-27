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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.swapi.swapiV1.R
import com.swapi.swapiV1.publication.viewmodel.NewPublicationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPublicationView(
    navController: NavController,
    viewModel: NewPublicationViewModel = viewModel() // 1. Inyectamos el ViewModel aquí
) {
    // Variables de estado y contexto
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val publishSuccess by viewModel.publishSuccess.collectAsStateWithLifecycle()

    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // 2. Escuchamos el resultado: Si fue exitoso, cerramos la pantalla
    LaunchedEffect(publishSuccess) {
        if (publishSuccess == true) {
            Toast.makeText(context, "¡Publicado con éxito!", Toast.LENGTH_LONG).show()
            navController.popBackStack() // Regresamos al feed y se actualiza
            viewModel.resetState()
        } else if (publishSuccess == false) {
            Toast.makeText(context, "Error al publicar. Revisa tu conexión.", Toast.LENGTH_LONG).show()
            viewModel.resetState()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> if (uri != null) selectedImageUri = uri }
    )

    // Mapeo: Lo que ve el usuario -> Lo que espera el backend
    val categoriasMap = mapOf(
        stringResource(R.string.ventas_title) to "ventas",
        stringResource(R.string.rentas_title) to "rentas",
        stringResource(R.string.new_pub_categoria_info) to "anuncios",
        stringResource(R.string.servicios_title) to "servicios"
    )
    val categoriasVisuales = categoriasMap.keys.toList()

    val swapiBrandColor = Color(0xFF4A8BFF)

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
            // Indicador de carga al centro si está subiendo
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
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text(stringResource(R.string.new_pub_titulo_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading // Deshabilitar si está cargando
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

                OutlinedTextField(
                    value = precio,
                    onValueChange = { if (it.all { char -> char.isDigit() }) precio = it },
                    label = { Text(stringResource(R.string.new_pub_precio_label)) },
                    prefix = { Text("$") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { if (!isLoading) expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = categoria,
                        onValueChange = {},
                        readOnly = true,
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

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .clickable(enabled = !isLoading) {
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
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = stringResource(R.string.new_pub_imagen_seleccionada_cd),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // 3. Botón CONECTADO al ViewModel
                Button(
                    onClick = {
                        // Validamos que no haya campos vacíos
                        if (titulo.isNotBlank() && precio.isNotBlank() && categoria.isNotBlank()) {

                            // Convertimos el nombre visual ("Ventas") al nombre técnico ("ventas")
                            val backendCategory = categoriasMap[categoria] ?: "ventas"

                            // Llamamos a la función de publicar
                            viewModel.publish(
                                context = context,
                                title = titulo,
                                description = descripcion,
                                price = precio,
                                category = backendCategory,
                                imageUri = selectedImageUri
                            )
                        } else {
                            Toast.makeText(context, "Por favor llena los campos", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(14.dp),
                    enabled = !isLoading // Evita doble click
                ) {
                    if (isLoading) {
                        Text("Publicando...", fontSize = 18.sp)
                    } else {
                        Text(
                            stringResource(R.string.new_pub_boton_publicar),
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}