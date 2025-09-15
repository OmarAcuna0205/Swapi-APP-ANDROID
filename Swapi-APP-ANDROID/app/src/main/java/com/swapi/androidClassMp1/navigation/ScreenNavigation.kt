package com.swapi.androidClassMp1.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class ScreenNavigation(val route: String, val label: String, val icon: ImageVector) {


    object Login : ScreenNavigation("login", "Login", Icons.Default.Person)
    object Home : ScreenNavigation("home", "Home", Icons.Default.Home)
    object Ventas : ScreenNavigation("ventas", "Ventas", Icons.Default.ShoppingCart)
    object Rentas : ScreenNavigation("rentas", "Rentas", Icons.Default.Key)
    object Servicios : ScreenNavigation("servicios", "Servicios", Icons.Default.Build)
    object Anuncios : ScreenNavigation("anuncios", "Anuncios", Icons.Default.Campaign)


}
