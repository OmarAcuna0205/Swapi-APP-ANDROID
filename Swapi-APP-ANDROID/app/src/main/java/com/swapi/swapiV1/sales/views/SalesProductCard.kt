package com.swapi.swapiV1.sales.views

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
fun SaleProductCard(
    product: Product,
    onClick: () -> Unit
) {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    format.maximumFractionDigits = 0

    // Usamos la constante BASE_URL. Asegúrate de que Constants.kt tenga tu IP correcta.
    val imageUrl = if (product.images.isNotEmpty()) Constants.BASE_URL + "storage/" + product.images[0] else ""

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(100),
        label = "cardScale"
    )

    // Colores más formales
    val borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    val cardBackgroundColor = MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(12.dp)) // Borde fino y elegante
            .clip(RoundedCornerShape(12.dp)) // Bordes menos redondos para ser más formal
            .clickable {
                isPressed = true
                onClick()
            },
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // SIN SOMBRA
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Imagen limpia sin degradados encima
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp) // Buena altura para mostrar el producto
                    .background(Color.Gray.copy(alpha = 0.1f)) // Placeholder sutil si carga lento
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Contenido textual minimalista
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Etiqueta del Vendedor (Pequeña y discreta)
                Text(
                    text = "Vendedor: ${product.author.firstName}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(8.dp))

                // Título
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(12.dp))

                // Fila de Precio y Acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = format.format(product.price),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.primary // Color de acento para el precio
                    )

                    // Icono minimalista sin círculo de fondo pesado
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Ver detalles",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}