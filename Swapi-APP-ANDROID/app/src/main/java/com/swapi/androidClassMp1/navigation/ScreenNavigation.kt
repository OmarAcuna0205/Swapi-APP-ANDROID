package com.swapi.androidClassMp1.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Sealed class para gestionar las rutas de navegación de la aplicación.
 * Define cada pantalla con su ruta, una etiqueta para la UI y un ícono.
 */
sealed class ScreenNavigation(val route: String, val label: String, val icon: ImageVector) {
    // --- Pantallas principales y de autenticación ---
    object Login : ScreenNavigation("login", "Login", Icons.Default.Person)
    object Home : ScreenNavigation("home", "Home", Icons.Default.Home)
    object Profile : ScreenNavigation("profile", "Perfil", Icons.Default.AccountCircle)
    object CrearPublicacion : ScreenNavigation("crear_publicacion", "Crear", Icons.Default.Add)

    // --- Pantallas de categorías (accesibles desde Home) ---
    object Ventas : ScreenNavigation("ventas", "Ventas", Icons.Default.ShoppingCart)
    object Rentas : ScreenNavigation("rentas", "Rentas", Icons.Default.Key)
    object Servicios : ScreenNavigation("servicios", "Servicios", Icons.Default.Build)
    object Anuncios : ScreenNavigation("anuncios", "Anuncios", Icons.Default.Campaign)

    // --- Pantalla de detalle con argumento dinámico ---
    // La ruta contiene "{productId}" para indicar que es un parámetro.
    object ProductDetail : ScreenNavigation("product_detail/{productId}", "Detalle", Icons.Default.Store) {
        /**
         * Función auxiliar para construir la ruta de detalle con un ID de producto específico.
         * @param productId El ID del producto a mostrar.
         * @return La ruta completa para navegar a la pantalla de detalle.
         */
        fun createRoute(productId: String) = "product_detail/$productId"
    }
}