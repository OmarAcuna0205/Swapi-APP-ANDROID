package com.swapi.swapiV1.profile.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.swapi.swapiV1.home.model.dto.Product
import com.swapi.swapiV1.utils.Constants
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MyPostCard(
    product: Product,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onClick: () -> Unit
) {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    format.maximumFractionDigits = 0
    val imageUrl = if (product.images.isNotEmpty()) Constants.BASE_URL + "storage/" + product.images[0] else ""

    val borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
    val cardBackground = MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp) // Un poco más de separación entre tarjetas
            .clip(RoundedCornerShape(16.dp))
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp) // Altura cómoda
        ) {
            // 1. IMAGEN (Izquierda)
            Box(
                modifier = Modifier
                    .width(130.dp) // Imagen cuadrada o casi cuadrada
                    .fillMaxHeight()
                    .background(Color.Gray.copy(alpha = 0.1f))
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Sombra sutil interna para dar profundidad
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.1f))
                            )
                        )
                )
            }

            // 2. CONTENIDO Y ACCIONES (Derecha)
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 2a. Columna de Texto (Centro)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Parte Superior: Categoría y Título
                    Column {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = product.category.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = product.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Parte Inferior: Precio
                    Text(
                        text = format.format(product.price),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // 2b. Columna de Botones (Derecha Extrema)
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween, // Editar arriba, Eliminar abajo
                    horizontalAlignment = Alignment.End
                ) {
                    // Botón Editar
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier
                            .size(34.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Botón Eliminar
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier
                            .size(34.dp)
                            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}