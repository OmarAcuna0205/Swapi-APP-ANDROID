package com.swapi.swapiV1.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class ScreenNavigation(val route: String, val label: String, val icon: ImageVector) {
    // ... tus otras rutas ...
    object Login : ScreenNavigation("login", "Login", Icons.Default.Person)
    object SignUpEmail : ScreenNavigation("signup_email", "Registro Email", Icons.Default.Email)
    object SignUpCode : ScreenNavigation("signup_code/{email}", "Registro Código", Icons.Default.QrCode) {
        fun createRoute(email: String) = "signup_code/$email"
    }
    object SignUpProfile : ScreenNavigation("signup_profile/{email}", "Registro Perfil", Icons.Default.PersonAdd) {
        fun createRoute(email: String) = "signup_profile/$email"
    }
    object Home : ScreenNavigation("home", "Home", Icons.Default.Home)
    object Profile : ScreenNavigation("profile", "Profile", Icons.Default.AccountCircle)
    object NewPublication : ScreenNavigation("new_publication", "Create", Icons.Default.Add)
    object SavedPosts : ScreenNavigation("saved_posts", "Guardados", Icons.Default.Bookmark)
    object MyPosts : ScreenNavigation("my_posts", "Mis Publicaciones", Icons.Default.List)
    object Sales : ScreenNavigation("sales", "Sales", Icons.Default.ShoppingCart)
    object Rents : ScreenNavigation("rents", "Rents", Icons.Default.Key)
    object Services : ScreenNavigation("services", "Services", Icons.Default.Build)
    object Ads : ScreenNavigation("ads", "Ads", Icons.Default.Campaign)
    object ProductDetail : ScreenNavigation("product_detail/{productId}", "Detail", Icons.Default.Store) {
        fun createRoute(productId: String) = "product_detail/$productId"
    }

    // --- NUEVA RUTA: Editar ---
    object EditPost : ScreenNavigation("edit_post/{postId}", "Editar", Icons.Default.Edit) {
        fun createRoute(postId: String) = "edit_post/$postId"
    }
}