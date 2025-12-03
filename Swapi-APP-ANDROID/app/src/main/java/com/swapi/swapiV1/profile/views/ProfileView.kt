package com.swapi.swapiV1.profile.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.swapi.swapiV1.R
import com.swapi.swapiV1.navigation.ScreenNavigation
import com.swapi.swapiV1.profile.viewmodel.ProfileUiState
import com.swapi.swapiV1.profile.viewmodel.ProfileViewModel
import com.swapi.swapiV1.profile.viewmodel.ProfileViewModelFactory
import com.swapi.swapiV1.ui.theme.SwapiBlueLight
import com.swapi.swapiV1.utils.datastore.DataStoreManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileView(
    navController: NavHostController,
    onLogout: () -> Unit,
    dataStore: DataStoreManager
) {
    val viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(dataStore)
    )
    val uiState by viewModel.uiState.collectAsState()

    // Definimos el color para bordes y texto
    val softenedBlueLight = SwapiBlueLight.copy(alpha = 0.9f)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_atras_cd))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1. Cabecera (Avatar y Nombre)
            Spacer(modifier = Modifier.height(10.dp))
            ProfileHeaderBig(uiState, softenedBlueLight)

            Spacer(modifier = Modifier.height(40.dp))

            // 2. Sección de Actividad
            Text(
                // CORRECCIÓN AQUÍ: Usamos stringResource
                text = stringResource(R.string.profile_tu_actividad),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tarjeta: Mis Publicaciones (Estilo Outlined)
                DashboardCard(
                    title = stringResource(R.string.profile_mis_publicaciones),
                    icon = Icons.Default.Layers,
                    modifier = Modifier.weight(1f),
                    // Fondo "vacío" (Surface)
                    containerColor = MaterialTheme.colorScheme.surface,
                    // Color para Borde, Icono y Texto
                    contentColor = softenedBlueLight
                ) { navController.navigate(ScreenNavigation.MyPosts.route) }

                // Tarjeta: Guardados (Estilo Outlined)
                DashboardCard(
                    title = stringResource(R.string.profile_guardados),
                    icon = Icons.Default.BookmarkBorder,
                    modifier = Modifier.weight(1f),
                    // Fondo "vacío" (Surface)
                    containerColor = MaterialTheme.colorScheme.surface,
                    // Color para Borde, Icono y Texto
                    contentColor = softenedBlueLight
                ) { navController.navigate(ScreenNavigation.SavedPosts.route) }
            }

            // 3. Espaciador flexible (empuja hacia abajo)
            Spacer(modifier = Modifier.weight(1f))

            // 4. Botón Cerrar Sesión
            LogoutButton(onLogout = onLogout)

            // 5. Espacio inferior AUMENTADO
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// --- Componentes Auxiliares ---

@Composable
private fun ProfileHeaderBig(state: ProfileUiState, borderColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Avatar: Borde con el color suavizado
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(110.dp)
                .border(
                    BorderStroke(3.dp, borderColor),
                    CircleShape
                )
        ) {
            if (state.userName.isNotEmpty()) {
                Text(
                    text = state.userName.take(1).uppercase(),
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 48.sp
                    ),
                    color = SwapiBlueLight
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(0.8f),
                    tint = SwapiBlueLight
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp,
                color = SwapiBlueLight
            )
        } else {
            Text(
                text = state.userName,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(6.dp))

            // Badge
            Surface(
                color = SwapiBlueLight.copy(alpha = 0.1f),
                shape = RoundedCornerShape(50),
            ) {
                Text(
                    // CORRECCIÓN AQUÍ: Usamos stringResource
                    text = stringResource(R.string.profile_miembro_verificado),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                    color = SwapiBlueLight,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun DashboardCard(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(24.dp),
        // AQUI ESTÁ EL CAMBIO PRINCIPAL: Agregamos borde
        border = BorderStroke(2.dp, contentColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor, // Icono toma el color del borde
                modifier = Modifier.size(34.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                ),
                color = contentColor, // Texto toma el color del borde
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LogoutButton(onLogout: () -> Unit) {
    OutlinedButton(
        onClick = onLogout,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.error
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.profile_cerrar_sesion),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}