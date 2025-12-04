package com.swapi.swapiV1.profile.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.swapi.swapiV1.R
import com.swapi.swapiV1.home.model.dto.Product
import com.swapi.swapiV1.utils.Constants
import java.text.NumberFormat
import java.util.Locale

/**
 * Tarjeta personalizada para la sección "Mis Publicaciones".
 * A diferencia de las tarjetas normales, esta incluye controles de gestión (Editar/Eliminar).
 *
 * @param product Datos de la publicación a mostrar.
 * @param onEditClick Acción al presionar el botón de lápiz.
 * @param onDeleteClick Acción al presionar el botón de basura.
 */
@Composable
fun MyPostCard(
    product: Product,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onClick: () -> Unit
) {
    // Configuración regional para formatear el precio en pesos mexicanos (MXN) sin decimales.
    val format = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    format.maximumFractionDigits = 0

    val priceColor = Color(0xFF448AFF)

    // Construcción segura de la URL de la imagen (usa la primera del array si existe).
    val imageUrl = if (product.images.isNotEmpty()) Constants.BASE_URL + "storage/" + product.images[0] else ""

    // Lógica para internacionalización: convierte el string de la categoría que viene del backend
    // en un recurso de texto traducible (R.string).
    val categoryLabel = when(product.category.lowercase()) {
        "ventas" -> stringResource(R.string.ventas_title)
        "rentas" -> stringResource(R.string.rentas_title)
        "servicios" -> stringResource(R.string.servicios_title)
        "anuncios" -> stringResource(R.string.anuncios_title)
        else -> product.category
    }

    val borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    val cardBackgroundColor = MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Sección superior: Imagen y etiqueta de categoría
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Color.Gray.copy(alpha = 0.1f))
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Etiqueta flotante para identificación rápida de la categoría
                Surface(
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(bottomStart = 8.dp),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = categoryLabel.uppercase(),
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Sección inferior: Datos y acciones
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
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

                Spacer(Modifier.height(4.dp))

                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(16.dp))

                // Fila con el precio y los botones de acción (Editar y Eliminar)
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
                        color = priceColor
                    )

                    // Grupo de botones de gestión
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = onEditClick,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(R.string.action_editar_cd),
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier.size(36.dp)
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