package com.swapi.androidClassMp1.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.swapi.androidClassMp1.ventas.VentasView
import com.swapi.androidClassMp1.rentas.RentasView
import com.swapi.androidClassMp1.home.HomeView
import com.swapi.androidClassMp1.servicios.ServiciosView
import com.swapi.androidClassMp1.anuncios.AnunciosView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabBarNavigationView(navController: NavHostController = rememberNavController()) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Swapi") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF87CEFA), // Azul
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Ventas") },
                    label = { Text("Ventas") },
                    selected = currentRoute == "ventas",
                    onClick = { navController.navigate("ventas") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Rentas") },
                    label = { Text("Rentas") },
                    selected = currentRoute == "rentas",
                    onClick = { navController.navigate("rentas") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = currentRoute == "home",
                    onClick = { navController.navigate("home") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Build, contentDescription = "Servicios") },
                    label = { Text("Servicios") },
                    selected = currentRoute == "servicios",
                    onClick = { navController.navigate("servicios") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Campaign, contentDescription = "Anuncios") },
                    label = { Text("Anuncios") },
                    selected = currentRoute == "anuncios",
                    onClick = { navController.navigate("anuncios") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "ventas",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("ventas") { VentasView() }
            composable("rentas") { RentasView() }
            composable("home") { HomeView() }
            composable("servicios") { ServiciosView() }
            composable("anuncios") { AnunciosView() }
        }
    }
}
