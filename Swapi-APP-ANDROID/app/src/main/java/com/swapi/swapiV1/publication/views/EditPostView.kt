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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.swapi.swapiV1.home.model.repository.HomeRepository
import com.swapi.swapiV1.home.model.repository.PostRepository
import com.swapi.swapiV1.publication.viewmodel.EditPostViewModel
import com.swapi.swapiV1.publication.viewmodel.EditPostViewModelFactory
import com.swapi.swapiV1.publication.viewmodel.EditUiState
import com.swapi.swapiV1.utils.Constants
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPostView(
    postId: String,
    navController: NavController
) {
    val factory = EditPostViewModelFactory(postId, HomeRepository(), PostRepository())
    val viewModel: EditPostViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val updateSuccess by viewModel.updateSuccess.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Variables de estado para el formulario
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var currentImageUrl by remember { mutableStateOf("") }

    // Control del dropdown
    var expanded by remember { mutableStateOf(false) }
    val categoriasVisuales = listOf("Ventas", "Rentas", "Servicios", "Anuncios")

    // --- PRECARGA DE DATOS ---
    LaunchedEffect(uiState) {
        if (uiState is EditUiState.Success) {
            val product = (uiState as EditUiState.Success).product
            // Solo llenamos si está vacío para no sobrescribir lo que el usuario esté editando
            if (titulo.isEmpty()) {
                titulo = product.title
                descripcion = product.description

                // Formato de precio sin decimales .0
                val df = DecimalFormat("#.##")
                precio = df.format(product.price)

                // Capitalizar primera letra de categoría
                categoria = product.category.replaceFirstChar { it.uppercase() }

                if (product.images.isNotEmpty()) {
                    currentImageUrl = Constants.BASE_URL + "storage/" + product.images[0]
                }
            }
        }
    }

    // --- RESPUESTA DE ACTUALIZACIÓN ---
    LaunchedEffect(updateSuccess) {
        if (updateSuccess == true) {
            Toast.makeText(context, "Publicación actualizada correctamente", Toast.LENGTH_SHORT).show()
            navController.popBackStack() // Volver atrás
            viewModel.resetState()
        } else if (updateSuccess == false) {
            Toast.makeText(context, "Error al actualizar la publicación", Toast.LENGTH_SHORT).show()
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Publicación") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        },
        containerColor = androidx.compose.ui.graphics.Color.Transparent
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
            when (val state = uiState) {
                is EditUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is EditUiState.Error -> {
                    Text(
                        text = state.msg,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is EditUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = titulo,
                            onValueChange = { titulo = it },
                            label = { Text("Título") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = descripcion,
                            onValueChange = { descripcion = it },
                            label = { Text("Descripción") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 120.dp),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 4,
                            maxLines = 8
                        )

                        OutlinedTextField(
                            value = precio,
                            onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) precio = it },
                            label = { Text("Precio") },
                            prefix = { Text("$") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = categoria,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Categoría") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
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

                        // Imagen (Solo visualización)
                        if (currentImageUrl.isNotEmpty()) {
                            Text(
                                "Imagen actual (no editable por ahora)",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                AsyncImage(
                                    model = currentImageUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (titulo.isNotBlank() && precio.isNotBlank() && categoria.isNotBlank()) {
                                    viewModel.saveChanges(
                                        title = titulo,
                                        description = descripcion,
                                        price = precio,
                                        category = categoria // El repositorio la pasará a minúsculas
                                    )
                                } else {
                                    Toast.makeText(context, "Por favor completa los campos", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Guardar Cambios", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}