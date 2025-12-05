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

    val swapiBlue = Color(0xFF448AFF)

    Scaffold(
        bottomBar = {
            if (
                currentRoute == ScreenNavigation.Home.route ||
                currentRoute == ScreenNavigation.Profile.route
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {

                    // ================= NAVBAR TRANSPARENTE =================
                    NavigationBar(
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp,
                        modifier = Modifier
                            .height(80.dp)
                            .align(Alignment.BottomCenter)
                            .background(Color.Transparent)
                    ) {

                        NavigationBarItem(
                            selected = currentRoute == ScreenNavigation.Home.route,
                            onClick = {
                                navController.navigate(ScreenNavigation.Home.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(Icons.Default.Home, contentDescription = null) },
                            label = { Text(stringResource(R.string.tab_home)) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = swapiBlue,
                                selectedTextColor = swapiBlue,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = Color.Transparent
                            )
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        NavigationBarItem(
                            selected = currentRoute == ScreenNavigation.Profile.route,
                            onClick = {
                                navController.navigate(ScreenNavigation.Profile.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(Icons.Default.Person, contentDescription = null) },
                            label = { Text(stringResource(R.string.tab_perfil)) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = swapiBlue,
                                selectedTextColor = swapiBlue,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = Color.Transparent
                            )
                        )
                    }

                    // ================= FAB MÁS SOBRESALIDO =================
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-44).dp) // 🔥 MÁS ARRIBA
                    ) {

                        Box(
                            modifier = Modifier
                                .size(76.dp) // 🔥 MÁS GRANDE
                                .shadow(14.dp, CircleShape) // 🔥 MÁS SOMBRA
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(5.dp),
                            contentAlignment = Alignment.Center
                        ) {

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(swapiBlue)
                                    .clickable {
                                        navController.navigate(ScreenNavigation.NewPublication.route)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = stringResource(R.string.nav_fab_publicar_cd),
                                    tint = Color.White,
                                    modifier = Modifier.size(34.dp)
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
                HomeView(navController, dataStore)
            }

            composable(ScreenNavigation.Profile.route) {
                ProfileView(navController, onLogout, dataStore)
            }

            composable(ScreenNavigation.NewPublication.route) {
                NewPublicationView(navController)
            }

            composable(ScreenNavigation.MyPosts.route) {
                MyPostsView(navController)
            }

            composable(ScreenNavigation.SavedPosts.route) {
                SavedPostsView(navController)
            }

            composable(ScreenNavigation.Sales.route) {
                CategoryProductView(navController, "ventas")
            }

            composable(ScreenNavigation.Rents.route) {
                CategoryProductView(navController, "rentas")
            }

            composable(ScreenNavigation.Services.route) {
                CategoryProductView(navController, "servicios")
            }

            composable(ScreenNavigation.Ads.route) {
                CategoryProductView(navController, "anuncios")
            }

            composable(
                route = ScreenNavigation.ProductDetail.route,
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) {
                val id = it.arguments?.getString("productId")!!
                ProductDetailView(id, navController)
            }

            composable(
                route = ScreenNavigation.EditPost.route,
                arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) {
                val id = it.arguments?.getString("postId")!!
                EditPostView(postId = id, navController = navController)
            }
        }
    }
}