package com.swapi.swapiV1.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.swapi.swapiV1.home.productdetail.view.ProductDetailView
import com.swapi.swapiV1.home.views.HomeView
import com.swapi.swapiV1.profile.view.ProfileView
import com.swapi.swapiV1.publication.views.NewPublicationView
import com.swapi.swapiV1.rents.RentsView
// --- CAMBIO AQUÍ: Import para la nueva pantalla ---
import com.swapi.swapiV1.saved.views.SavedPostsView
// --- FIN DEL CAMBIO ---
import com.swapi.swapiV1.sales.views.SalesView
import com.swapi.swapiV1.services.ServicesView
import com.swapi.swapiV1.utils.datastore.DataStoreManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabBarNavigationView(
    dataStore: DataStoreManager,
    onLogout: () -> Unit,
    startDestination: String = ScreenNavigation.Home.route,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val swapiBlue = Color(0xFF448AFF)

    Scaffold(
        topBar = { /* Vacío */ },
        bottomBar = {
            if (currentRoute in listOf(ScreenNavigation.Home.route, ScreenNavigation.Profile.route)) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 0.dp,
                        modifier = Modifier.height(85.dp)
                    ) {
                        // Inicio
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

                        // Perfil
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

                    // Botón central flotante
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-48).dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .shadow(
                                    elevation = 20.dp,
                                    shape = CircleShape,
                                    ambientColor = Color.Black.copy(alpha = 0.6f),
                                    spotColor = Color.Black.copy(alpha = 0.6f)
                                )
                                .clip(CircleShape)
                                .background(Color.White)
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(swapiBlue)
                                    .clickable {
                                        navController.navigate(ScreenNavigation.NewPublication.route)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Publicar",
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(ScreenNavigation.Home.route) {
                HomeView(navController = navController, dataStore = dataStore)
            }
            composable(ScreenNavigation.Profile.route) {
                ProfileView(navController, onLogout = onLogout)
            }
            composable(ScreenNavigation.NewPublication.route) { NewPublicationView(navController) }
            composable(ScreenNavigation.Sales.route) { SalesView(navController = navController) }
            composable(ScreenNavigation.Rents.route) { RentsView() }
            composable(ScreenNavigation.Services.route) { ServicesView() }
            composable(ScreenNavigation.Ads.route) { AdsView() }

            // --- CAMBIO AQUÍ: Ruta añadida para "Guardados" ---
            composable(ScreenNavigation.SavedPosts.route) {
                SavedPostsView(navController = navController)
            }
            // --- FIN DEL CAMBIO ---

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