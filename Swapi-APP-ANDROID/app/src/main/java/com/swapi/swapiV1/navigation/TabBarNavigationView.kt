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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.swapi.swapiV1.R
import com.swapi.swapiV1.home.views.CategoryProductView
import com.swapi.swapiV1.home.views.HomeView
import com.swapi.swapiV1.productdetail.views.ProductDetailView
import com.swapi.swapiV1.profile.views.MyPostsView
import com.swapi.swapiV1.profile.views.ProfileView
import com.swapi.swapiV1.publication.views.EditPostView
import com.swapi.swapiV1.publication.views.NewPublicationView
import com.swapi.swapiV1.saved.views.SavedPostsView
import com.swapi.swapiV1.utils.datastore.DataStoreManager

@Composable
fun TabBarNavigationView(
    dataStore: DataStoreManager,
    onLogout: () -> Unit,
    startDestination: String = ScreenNavigation.Home.route,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Color de marca (idealmente muévelo a tu Theme.kt)
    val swapiBlue = Color(0xFF448AFF)

    Scaffold(
        // La barra inferior solo se muestra en las pantallas principales (Home y Perfil)
        bottomBar = {
            if (currentRoute == ScreenNavigation.Home.route || currentRoute == ScreenNavigation.Profile.route) {
                Box(modifier = Modifier.fillMaxWidth()) {

                    // 1. La barra horizontal estándar
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp, // Elevación para sombra sutil
                        modifier = Modifier.height(80.dp).align(Alignment.BottomCenter)
                    ) {
                        // Ítem Izquierdo: INICIO
                        NavigationBarItem(
                            selected = currentRoute == ScreenNavigation.Home.route,
                            onClick = {
                                navController.navigate(ScreenNavigation.Home.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(Icons.Default.Home, contentDescription = stringResource(R.string.tab_home)) },
                            label = { Text(stringResource(R.string.tab_home)) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = swapiBlue,
                                selectedTextColor = swapiBlue,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = Color.Transparent
                            )
                        )

                        // Espaciador central transparente para dejar lugar al botón flotante
                        Spacer(modifier = Modifier.weight(1f))

                        // Ítem Derecho: PERFIL
                        NavigationBarItem(
                            selected = currentRoute == ScreenNavigation.Profile.route,
                            onClick = {
                                navController.navigate(ScreenNavigation.Profile.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(Icons.Default.Person, contentDescription = stringResource(R.string.tab_perfil)) },
                            label = { Text(stringResource(R.string.tab_perfil)) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = swapiBlue,
                                selectedTextColor = swapiBlue,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = Color.Transparent
                            )
                        )
                    }

                    // 2. El Botón Flotante (FAB) superpuesto
                    // Usamos offset para subirlo visualmente por encima de la barra
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-30).dp) // Ajuste para que "flote" a la mitad
                    ) {
                        // Círculo blanco exterior (borde)
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .shadow(8.dp, CircleShape)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(4.dp), // Grosor del borde blanco
                            contentAlignment = Alignment.Center
                        ) {
                            // Círculo azul interior (botón real)
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(swapiBlue)
                                    .clickable { navController.navigate(ScreenNavigation.NewPublication.route) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = stringResource(R.string.nav_fab_publicar_cd),
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->
        // --- GRAFO DE NAVEGACIÓN ---
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Pantallas Principales
            composable(ScreenNavigation.Home.route) { HomeView(navController, dataStore) }
            composable(ScreenNavigation.Profile.route) { ProfileView(navController, onLogout, dataStore) }

            // Acciones y Listas
            composable(ScreenNavigation.NewPublication.route) { NewPublicationView(navController) }
            composable(ScreenNavigation.MyPosts.route) { MyPostsView(navController) }
            composable(ScreenNavigation.SavedPosts.route) { SavedPostsView(navController) }

            // Categorías (Reutilizamos la misma vista con diferente parámetro)
            composable(ScreenNavigation.Sales.route) { CategoryProductView(navController, category = "ventas") }
            composable(ScreenNavigation.Rents.route) { CategoryProductView(navController, category = "rentas") }
            composable(ScreenNavigation.Services.route) { CategoryProductView(navController, category = "servicios") }
            composable(ScreenNavigation.Ads.route) { CategoryProductView(navController, category = "anuncios") }

            // Detalles y Edición (Con Argumentos)
            composable(
                route = ScreenNavigation.ProductDetail.route,
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { entry ->
                val id = entry.arguments?.getString("productId")
                requireNotNull(id) { "El productId es obligatorio" }
                ProductDetailView(id, navController)
            }

            composable(
                route = ScreenNavigation.EditPost.route,
                arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { entry ->
                val id = entry.arguments?.getString("postId")
                requireNotNull(id) { "El postId es obligatorio" }
                EditPostView(postId = id, navController = navController)
            }
        }
    }
}