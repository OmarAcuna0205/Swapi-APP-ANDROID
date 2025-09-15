package com.swapi.androidClassMp1.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.swapi.androidClassMp1.ventas.VentasView
import com.swapi.androidClassMp1.rentas.RentasView
import com.swapi.androidClassMp1.home.HomeView
import com.swapi.androidClassMp1.servicios.ServiciosView
import com.swapi.androidClassMp1.anuncios.AnunciosView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabBarNavigationView(
    startDestination: String = ScreenNavigation.Home.route, // <--- recibe inicio
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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Swapi") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF1976D2),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
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
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination, // <-- usa el startDestination recibido
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(ScreenNavigation.Ventas.route) { VentasView() }
            composable(ScreenNavigation.Rentas.route) { RentasView() }
            composable(ScreenNavigation.Home.route) { HomeView() }
            composable(ScreenNavigation.Servicios.route) { ServiciosView() }
            composable(ScreenNavigation.Anuncios.route) { AnunciosView() }
        }
    }
}


