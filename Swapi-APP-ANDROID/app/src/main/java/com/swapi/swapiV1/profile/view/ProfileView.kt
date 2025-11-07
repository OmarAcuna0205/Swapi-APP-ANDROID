package com.swapi.swapiV1.profile.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.swapi.swapiV1.profile.viewmodel.ProfileUiState
import com.swapi.swapiV1.profile.viewmodel.ProfileViewModel

// Estructura de datos para simplificar la creación de menús
data class MenuItem(
    val icon: ImageVector,
    val title: String,
    val subtitle: String? = null,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileView(
    navController: NavHostController,
    vm: ProfileViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    val context = LocalContext.current

    // Definimos las nuevas secciones relevantes para Swapi
    val activityItems = listOf(
        MenuItem(Icons.Default.ListAlt, "Mis publicaciones", "Gestiona todos tus ads", "my_posts"),
        MenuItem(Icons.Default.History, "Historial de actividad", "Ventas, rents y services pasados", "history")
    )

    val accountItems = listOf(
        MenuItem(Icons.Default.Edit, "Editar perfil público", route = "edit_profile"),
        MenuItem(Icons.Default.Shield, "Seguridad de la cuenta", "Verifica tu correo institucional", "account_security"),
        MenuItem(Icons.AutoMirrored.Filled.Logout, "Cerrar Sesión", route = "logout")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil de Swapi") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                item { ProfileHeader(uiState) }
                item { ActionsGrid(navController) }

                // Sección "Mi Actividad"
                item { SectionHeader("Mi Actividad") }
                items(activityItems.size) { index ->
                    val item = activityItems[index]
                    ProfileListItem(icon = item.icon, title = item.title, subtitle = item.subtitle) {
                        Toast.makeText(context, "Ir a ${item.title}", Toast.LENGTH_SHORT).show()
                    }
                }

                item { Divider(Modifier.padding(horizontal = 16.dp)) }

                // Sección "Cuenta"
                item { SectionHeader("Cuenta y Soporte") }
                items(accountItems.size) { index ->
                    val item = accountItems[index]
                    ProfileListItem(icon = item.icon, title = item.title, subtitle = item.subtitle) {
                        if (item.route == "logout") {
                            // Aquí iría la lógica para cerrar sesión
                            Toast.makeText(context, "Cerrando sesión...", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Ir a ${item.title}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionsGrid(navController: NavHostController) {
    val context = LocalContext.current
    // Nuevas acciones principales para Swapi
    val actions = listOf(
        "Guardados" to Icons.Default.BookmarkBorder,
        "Mensajes" to Icons.Default.ChatBubbleOutline,
        "Calificaciones" to Icons.Default.StarBorder,
        "Ayuda" to Icons.Default.HelpOutline
    )

    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ActionCard(title = actions[0].first, icon = actions[0].second, modifier = Modifier.weight(1f)) {
                Toast.makeText(context, "Ir a ${actions[0].first}", Toast.LENGTH_SHORT).show()
            }
            ActionCard(title = actions[1].first, icon = actions[1].second, modifier = Modifier.weight(1f)) {
                Toast.makeText(context, "Ir a ${actions[1].first}", Toast.LENGTH_SHORT).show()
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ActionCard(title = actions[2].first, icon = actions[2].second, modifier = Modifier.weight(1f)) {
                Toast.makeText(context, "Ir a ${actions[2].first}", Toast.LENGTH_SHORT).show()
            }
            ActionCard(title = actions[3].first, icon = actions[3].second, modifier = Modifier.weight(1f)) {
                Toast.makeText(context, "Ir a ${actions[3].first}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

// El resto de componentes reutilizables (ProfileHeader, ActionCard, SectionHeader, ProfileListItem)
// se quedan exactamente igual, ya que solo hemos cambiado los datos que les pasamos.
// No es necesario volver a pegarlos si no les hiciste cambios. Te los incluyo por si acaso.

@Composable
private fun ProfileHeader(state: ProfileUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = state.profileImageUrl),
            contentDescription = "Foto de perfil",
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(8.dp))
        Text(state.userName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        TextButton(onClick = { /* TODO: Navegar al perfil público */ }) {
            Text("Ver mi perfil público")
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(),
            onClick = onClick
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
private fun ProfileListItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null)
    }
}