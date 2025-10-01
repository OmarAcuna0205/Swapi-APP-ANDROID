package com.swapi.androidClassMp1.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.swapi.androidClassMp1.anuncios.AnunciosView
import com.swapi.androidClassMp1.components.topbar.SwapiTopBar
import com.swapi.androidClassMp1.home.views.HomeView
import com.swapi.androidClassMp1.profile.view.ProfileView
import com.swapi.androidClassMp1.rentas.RentasView
import com.swapi.androidClassMp1.servicios.ServiciosView
import com.swapi.androidClassMp1.ventas.VentasView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabBarNavigationView(
    startDestination: String = ScreenNavigation.Home.route,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val tabs = listOf(
        ScreenNavigation.Ventas,
        ScreenNavigation.Rentas,
        ScreenNavigation.Home,
        ScreenNavigation.Servicios,
        ScreenNavigation.Anuncios
    )

    val showTopAndBottomBar = currentRoute in tabs.map { it.route }

    Scaffold(
        topBar = {
            if (showTopAndBottomBar) {
                // --- CAMBIO PRINCIPAL AQUÍ ---
                // Se actualiza el parámetro de onSearchClick a onSearchAction
                SwapiTopBar(
                    navController = navController,
                    onSearchAction = { query ->
                        // TODO: Implementa la lógica de búsqueda real aquí.
                        // Este código se ejecutará cuando el usuario presione "Buscar" en el teclado.
                        println("Búsqueda realizada para: $query")
                    }
                )
            }
        },
        bottomBar = {
            if (showTopAndBottomBar) {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                            selected = currentRoute == tab.route,
                            onClick = {
                                navController.navigate(tab.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(ScreenNavigation.Ventas.route) { VentasView() }
            composable(ScreenNavigation.Rentas.route) { RentasView() }
            composable(ScreenNavigation.Home.route) { HomeView() }
            composable(ScreenNavigation.Servicios.route) { ServiciosView() }
            composable(ScreenNavigation.Anuncios.route) { AnunciosView() }

            composable(ScreenNavigation.Profile.route) { ProfileView(navController) }
        }
    }
}