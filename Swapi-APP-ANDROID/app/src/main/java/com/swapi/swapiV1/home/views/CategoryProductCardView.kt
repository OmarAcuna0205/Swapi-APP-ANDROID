package com.swapi.swapiV1.home.views

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

// Componente reutilizable para mostrar un producto individual en formato de tarjeta.
// Recibe el objeto de datos 'Product' y una funcion lambda 'onClick' para manejar la navegacion.
@Composable
fun CategoryProductCardView(
    product: Product,
    onClick: () -> Unit
) {
    // Configuracion de formato de moneda para pesos mexicanos (MXN), sin decimales para limpieza visual.
    val format = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    format.maximumFractionDigits = 0

    // Color de acento especifico para el precio
    val priceColor = Color(0xFF448AFF)

    // Construccion segura de la URL de la imagen.
    // Concatenamos la URL base del servidor con la ruta de la primera imagen del array.
    val imageUrl = if (product.images.isNotEmpty()) Constants.BASE_URL + "storage/" + product.images[0] else ""

    // --- LOGICA DE ANIMACION ---
    // Estado para detectar si la tarjeta esta siendo presionada.
    var isPressed by remember { mutableStateOf(false) }

    // Animacion de escala: Reduce ligeramente el tamano (a 0.98) cuando se presiona para dar feedback tactil.
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(100), // Duracion de 100ms para una respuesta rapida
        label = "cardScale"
    )

    val borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    val cardBackgroundColor = MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            // graphicsLayer permite aplicar transformaciones (como escala) de manera eficiente en la GPU
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp)) // Recorta el contenido (incluyendo la imagen) a los bordes redondeados
            .clickable {
                isPressed = true
                onClick()
            },
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Diseño plano (flat design)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Contenedor de la imagen con altura fija
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Color.Gray.copy(alpha = 0.1f)) // Fondo placeholder mientras carga
            ) {
                // AsyncImage (libreria Coil) maneja la carga asincrona, cacheo y decodificacion de la imagen
                AsyncImage(
                    model = imageUrl,
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop, // Recorta la imagen para llenar el contenedor sin deformarse
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Seccion de informacion del producto (Titulo, Autor, Precio)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Logica de visualizacion del autor con valor por defecto si es nulo
                val defaultUser = stringResource(R.string.common_usuario_default)
                val authorName = product.author?.firstName ?: defaultUser
                val prefix = stringResource(R.string.sale_card_user_prefix)

                Text(
                    text = "$prefix $authorName",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1, // Limitamos a una linea para mantener uniformidad en la grilla
                    overflow = TextOverflow.Ellipsis // Agrega "..." si el texto es muy largo
                )

                Spacer(Modifier.height(12.dp))

                // Fila inferior con Precio e Icono de flecha
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

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = stringResource(R.string.sales_card_details_cd),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}