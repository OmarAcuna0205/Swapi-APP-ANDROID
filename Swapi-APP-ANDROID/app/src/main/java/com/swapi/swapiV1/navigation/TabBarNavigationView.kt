package com.swapi.swapiV1.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.swapi.swapiV1.ads.AdsView
import com.swapi.swapiV1.components.topbar.SwapiTopBar
import com.swapi.swapiV1.home.productdetail.view.ProductDetailView
import com.swapi.swapiV1.home.views.HomeView
import com.swapi.swapiV1.profile.view.ProfileView
import com.swapi.swapiV1.rents.RentsView
import com.swapi.swapiV1.services.ServicesView
import com.swapi.swapiV1.sales.views.NewPublicationView
import com.swapi.swapiV1.sales.views.SalesView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabBarNavigationView(
    startDestination: String = ScreenNavigation.Home.route,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Define en qué rutas se mostrarán las barras de navegación.
    val showBars = currentRoute in listOf(
        ScreenNavigation.Home.route,
        ScreenNavigation.Profile.route,
        ScreenNavigation.Sales.route,
        ScreenNavigation.Rents.route,
        ScreenNavigation.Services.route,
        ScreenNavigation.Ads.route
    )

    val swapiBlue = Color(0xFF448AFF)

    Scaffold(
        topBar = {
            if (showBars) {
                SwapiTopBar(
                    onSearchAction = { query ->
                        println("Búsqueda realizada para: $query")
                    }
                )
            }
        },
        bottomBar = {
            // La barra inferior solo se muestra en las rutas principales.
            if (currentRoute in listOf(ScreenNavigation.Home.route, ScreenNavigation.Profile.route)) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                        modifier = Modifier.height(80.dp)
                    ) {
                        // Ítem 1: Inicio
                        NavigationBarItem(
                            selected = currentRoute == ScreenNavigation.Home.route,
                            onClick = {
                                navController.navigate(ScreenNavigation.Home.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                            label = { Text("Inicio") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = swapiBlue,
                                selectedTextColor = swapiBlue,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = Color.Transparent
                            )
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        // Ítem 2: Perfil
                        NavigationBarItem(
                            selected = currentRoute == ScreenNavigation.Profile.route,
                            onClick = {
                                navController.navigate(ScreenNavigation.Profile.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                            label = { Text("Perfil") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = swapiBlue,
                                selectedTextColor = swapiBlue,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = Color.Transparent
                            )
                        )
                    }

                    // Botón flotante central para "Crear Publicación"
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-28).dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .shadow(
                                    elevation = 8.dp,
                                    shape = CircleShape,
                                    ambientColor = swapiBlue.copy(alpha = 0.3f),
                                    spotColor = swapiBlue.copy(alpha = 0.3f)
                                )
                                .clip(CircleShape)
                                .background(swapiBlue)
                                .clickable { navController.navigate(ScreenNavigation.NewPublication.route) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Publicar",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
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
            // --- Definición de todas las rutas de la aplicación ---
            composable(ScreenNavigation.Home.route) { HomeView(navController = navController) }
            composable(ScreenNavigation.Profile.route) { ProfileView(navController) }
            composable(ScreenNavigation.NewPublication.route) { NewPublicationView(navController) } // <-- CORREGIDO
            composable(ScreenNavigation.Sales.route) { SalesView(navController = navController) }      // <-- CORREGIDO
            composable(ScreenNavigation.Rents.route) { RentsView() }
            composable(ScreenNavigation.Services.route) { ServicesView() }                             // <-- CORREGIDO
            composable(ScreenNavigation.Ads.route) { AdsView() }

            // --- Ruta para la pantalla de detalle del producto ---
            composable(
                route = ScreenNavigation.ProductDetail.route,
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId")
                requireNotNull(productId) { "El ID del producto no puede ser nulo" }
                ProductDetailView(productId = productId, navController = navController)
            }
        }
    }
}