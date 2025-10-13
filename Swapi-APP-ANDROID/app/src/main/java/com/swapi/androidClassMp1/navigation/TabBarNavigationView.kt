package com.swapi.androidClassMp1.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.swapi.androidClassMp1.anuncios.AnunciosView
import com.swapi.androidClassMp1.components.topbar.SwapiTopBar
import com.swapi.androidClassMp1.home.productdetail.view.ProductDetailScreen
import com.swapi.androidClassMp1.home.views.HomeView
import com.swapi.androidClassMp1.profile.view.ProfileView
import com.swapi.androidClassMp1.rentas.RentasView
import com.swapi.androidClassMp1.servicios.ServiciosView
import com.swapi.androidClassMp1.ventas.views.CrearPublicacionView
import com.swapi.androidClassMp1.ventas.views.VentasView

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

    // Determina si se deben mostrar las barras superior e inferior
    // La pantalla de detalle no las mostrará, lo que le da más espacio.
    val showTopAndBottomBar = currentRoute in tabs.map { it.route }

    Scaffold(
        topBar = {
            if (showTopAndBottomBar) {
                SwapiTopBar(
                    navController = navController,
                    onSearchAction = { query ->
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
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
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
            composable(ScreenNavigation.Ventas.route) { VentasView(navController = navController) }
            composable(ScreenNavigation.Rentas.route) { RentasView() }

            // --- CAMBIO 1: Se pasa el navController a HomeView ---
            composable(ScreenNavigation.Home.route) { HomeView(navController = navController) }

            composable(ScreenNavigation.Servicios.route) { ServiciosView() }
            composable(ScreenNavigation.Anuncios.route) { AnunciosView() }
            composable(ScreenNavigation.Profile.route) { ProfileView(navController) }
            composable(ScreenNavigation.CrearPublicacion.route) {
                CrearPublicacionView (navController)
            }


            // --- CAMBIO 2: Nuevo composable para la pantalla de detalle ---
            composable(
                route = ScreenNavigation.ProductDetail.route,
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId")
                requireNotNull(productId) { "El ID del producto no puede ser nulo" }

                // La pantalla de detalle no está dentro del Scaffold, por lo que no tendrá la barra inferior
                ProductDetailScreen(
                    productId = productId,
                    navController = navController
                )
            }
        }
    }
}