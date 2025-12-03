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
import androidx.compose.ui.res.stringResource // IMPORTANTE
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.swapi.swapiV1.R // IMPORTANTE
import com.swapi.swapiV1.home.productdetail.view.ProductDetailView
import com.swapi.swapiV1.home.views.CategoryProductView
import com.swapi.swapiV1.home.views.HomeView
import com.swapi.swapiV1.profile.views.MyPostsView
import com.swapi.swapiV1.profile.views.ProfileView
import com.swapi.swapiV1.publication.views.EditPostView
import com.swapi.swapiV1.publication.views.NewPublicationView
import com.swapi.swapiV1.saved.views.SavedPostsView
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
                        // --- ITEM INICIO ---
                        NavigationBarItem(
                            selected = currentRoute == ScreenNavigation.Home.route,
                            onClick = {
                                navController.navigate(ScreenNavigation.Home.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            // CORRECCIÓN: stringResource
                            icon = { Icon(Icons.Default.Home, contentDescription = stringResource(R.string.tab_home)) },
                            label = { Text(stringResource(R.string.tab_home)) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = swapiBlue,
                                selectedTextColor = swapiBlue,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = Color.Transparent
                            )
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        // --- ITEM PERFIL ---
                        NavigationBarItem(
                            selected = currentRoute == ScreenNavigation.Profile.route,
                            onClick = {
                                navController.navigate(ScreenNavigation.Profile.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            // CORRECCIÓN: stringResource
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

                    // --- BOTÓN FLOTANTE CENTRAL ---
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-48).dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .shadow(20.dp, CircleShape, ambientColor = Color.Black.copy(0.6f))
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
                                    .clickable { navController.navigate(ScreenNavigation.NewPublication.route) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    // CORRECCIÓN: stringResource
                                    contentDescription = stringResource(R.string.nav_fab_publicar_cd),
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
            composable(ScreenNavigation.Home.route) { HomeView(navController, dataStore) }
            composable(ScreenNavigation.Profile.route) { ProfileView(navController, onLogout, dataStore) }
            composable(ScreenNavigation.NewPublication.route) { NewPublicationView(navController) }
            composable(ScreenNavigation.MyPosts.route) { MyPostsView(navController) }

            composable(ScreenNavigation.Sales.route) { CategoryProductView(navController, category = "ventas") }
            composable(ScreenNavigation.Rents.route) { CategoryProductView(navController, category = "rentas") }
            composable(ScreenNavigation.Services.route) { CategoryProductView(navController, category = "servicios") }
            composable(ScreenNavigation.Ads.route) { CategoryProductView(navController, category = "anuncios") }

            composable(ScreenNavigation.SavedPosts.route) { SavedPostsView(navController) }

            composable(
                route = ScreenNavigation.ProductDetail.route,
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId")
                requireNotNull(productId)
                ProductDetailView(productId, navController)
            }

            composable(
                route = ScreenNavigation.EditPost.route,
                arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId")
                requireNotNull(postId)
                EditPostView(postId = postId, navController = navController)
            }
        }
    }
}