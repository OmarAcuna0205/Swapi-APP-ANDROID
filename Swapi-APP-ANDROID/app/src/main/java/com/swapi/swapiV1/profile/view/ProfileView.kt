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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.swapi.swapiV1.R
import com.swapi.swapiV1.navigation.ScreenNavigation
import com.swapi.swapiV1.profile.viewmodel.ProfileUiState
import com.swapi.swapiV1.profile.viewmodel.ProfileViewModel

// (Esta data class estaba en tu archivo original, la dejamos)
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
    onLogout: () -> Unit,
    vm: ProfileViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    val context = LocalContext.current

    val activityItems = listOf(
        MenuItem(
            Icons.Default.ListAlt,
            stringResource(R.string.profile_mis_publicaciones),
            stringResource(R.string.profile_mis_publicaciones_sub),
            "my_posts"
        ),
        MenuItem(
            Icons.Default.History,
            stringResource(R.string.profile_historial),
            stringResource(R.string.profile_historial_sub),
            "history"
        )
    )

    val accountItems = listOf(
        MenuItem(
            Icons.Default.Edit,
            stringResource(R.string.profile_editar),
            route = "edit_profile"
        ),
        MenuItem(
            Icons.Default.Shield,
            stringResource(R.string.profile_seguridad),
            stringResource(R.string.profile_seguridad_sub),
            "account_security"
        ),
        MenuItem(
            Icons.AutoMirrored.Filled.Logout,
            stringResource(R.string.profile_cerrar_sesion),
            route = "logout"
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile_titulo)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back_button_cd)
                        )
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
                item { ActionsGrid(navController) } // <-- Error arreglado

                item { SectionHeader(stringResource(R.string.profile_seccion_actividad)) } // <-- Error arreglado
                items(activityItems.size) { index ->
                    val item = activityItems[index]
                    val msgIrA = stringResource(R.string.profile_toast_ir_a, item.title)
                    ProfileListItem(icon = item.icon, title = item.title, subtitle = item.subtitle) { // <-- Error arreglado
                        Toast.makeText(context, msgIrA, Toast.LENGTH_SHORT).show()
                    }
                }

                item { Divider(Modifier.padding(horizontal = 16.dp)) }

                item { SectionHeader(stringResource(R.string.profile_seccion_cuenta)) } // <-- Error arreglado
                items(accountItems.size) { index ->
                    val item = accountItems[index]
                    val msgIrA = stringResource(R.string.profile_toast_ir_a, item.title)
                    ProfileListItem(icon = item.icon, title = item.title, subtitle = item.subtitle) { // <-- Error arreglado
                        if (item.route == "logout") {
                            onLogout()
                        } else {
                            Toast.makeText(context, msgIrA, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}

// --- ¡AQUÍ ESTÁN LOS AYUDANTES QUE FALTABAN! ---

@Composable
private fun ActionsGrid(navController: NavHostController) {
    val context = LocalContext.current

    val actions = listOf(
        stringResource(R.string.profile_guardados) to Icons.Default.BookmarkBorder,
        stringResource(R.string.profile_mensajes) to Icons.Default.ChatBubbleOutline,
        stringResource(R.string.profile_calificaciones) to Icons.Default.StarBorder,
        stringResource(R.string.profile_ayuda) to Icons.Default.HelpOutline
    )
    val msgIrA1 = stringResource(R.string.profile_toast_ir_a, actions[1].first)
    val msgIrA2 = stringResource(R.string.profile_toast_ir_a, actions[2].first)
    val msgIrA3 = stringResource(R.string.profile_toast_ir_a, actions[3].first)

    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ActionCard(title = actions[0].first, icon = actions[0].second, modifier = Modifier.weight(1f)) { // <-- Error arreglado
                navController.navigate(ScreenNavigation.SavedPosts.route)
            }
            ActionCard(title = actions[1].first, icon = actions[1].second, modifier = Modifier.weight(1f)) { // <-- Error arreglado
                Toast.makeText(context, msgIrA1, Toast.LENGTH_SHORT).show()
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ActionCard(title = actions[2].first, icon = actions[2].second, modifier = Modifier.weight(1f)) { // <-- Error arreglado
                Toast.makeText(context, msgIrA2, Toast.LENGTH_SHORT).show()
            }
            ActionCard(title = actions[3].first, icon = actions[3].second, modifier = Modifier.weight(1f)) { // <-- Error arreglado
                Toast.makeText(context, msgIrA3, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
private fun ProfileHeader(state: ProfileUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = stringResource(R.string.profile_foto_cd),
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(state.userName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        TextButton(onClick = { /* TODO: Navegar al perfil público */ }) {
            Text(stringResource(R.string.profile_ver_publico))
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