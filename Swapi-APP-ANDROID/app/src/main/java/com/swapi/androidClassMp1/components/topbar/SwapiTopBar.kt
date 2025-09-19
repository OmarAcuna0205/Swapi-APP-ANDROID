package com.swapi.androidClassMp1.components.topbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle // Icono para perfil
import androidx.compose.material.icons.filled.Search // Icono para búsqueda
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.swapi.androidClassMp1.navigation.ScreenNavigation
import com.swapi.androidClassMp1.R // Asegúrate de importar tu R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwapiTopBar(
    navController: NavHostController,
    onSearchClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Swapi", // Puedes usar un string resource si prefieres
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            // Icono de la izquierda (Perfil)
            IconButton(onClick = { navController.navigate(ScreenNavigation.Profile.route) }) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Ir al perfil" // Accesibilidad
                )
            }
        },
        actions = {
            // Icono de la derecha (Búsqueda)
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar" // Accesibilidad
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}