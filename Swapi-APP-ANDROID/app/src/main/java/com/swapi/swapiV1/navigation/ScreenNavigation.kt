package com.swapi.swapiV1.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Catálogo centralizado de todas las pantallas de la aplicación.
 * Define la ruta técnica (String), la etiqueta visible (Label) y el ícono asociado.
 */
sealed class ScreenNavigation(val route: String, val label: String, val icon: ImageVector) {

    // --- SECCIÓN DE AUTENTICACIÓN ---
    object Login : ScreenNavigation("login", "Login", Icons.Default.Person)
    object SignUpEmail : ScreenNavigation("signup_email", "Registro Email", Icons.Default.Email)

    // Rutas dinámicas que reciben parámetros (argumentos)
    object SignUpCode : ScreenNavigation("signup_code/{email}", "Registro Código", Icons.Default.QrCode) {
        fun createRoute(email: String) = "signup_code/$email"
    }

    object SignUpProfile : ScreenNavigation("signup_profile/{email}", "Registro Perfil", Icons.Default.PersonAdd) {
        fun createRoute(email: String) = "signup_profile/$email"
    }

    // --- SECCIÓN PRINCIPAL (Tabs / BottomBar) ---
    object Home : ScreenNavigation("home", "Inicio", Icons.Default.Home)
    object Profile : ScreenNavigation("profile", "Perfil", Icons.Default.AccountCircle)
    object NewPublication : ScreenNavigation("new_publication", "Publicar", Icons.Default.Add)

    // --- LISTAS Y FILTROS ---
    object SavedPosts : ScreenNavigation("saved_posts", "Guardados", Icons.Default.Bookmark)
    object MyPosts : ScreenNavigation("my_posts", "Mis Publicaciones", Icons.Default.List)
    object Sales : ScreenNavigation("sales", "Ventas", Icons.Default.ShoppingCart)
    object Rents : ScreenNavigation("rents", "Rentas", Icons.Default.Key)
    object Services : ScreenNavigation("services", "Servicios", Icons.Default.Build)
    object Ads : ScreenNavigation("ads", "Anuncios", Icons.Default.Campaign)

    // --- DETALLES Y EDICIÓN ---
    object ProductDetail : ScreenNavigation("product_detail/{productId}", "Detalle", Icons.Default.Store) {
        fun createRoute(productId: String) = "product_detail/$productId"
    }

    object EditPost : ScreenNavigation("edit_post/{postId}", "Editar", Icons.Default.Edit) {
        fun createRoute(postId: String) = "edit_post/$postId"
    }
}