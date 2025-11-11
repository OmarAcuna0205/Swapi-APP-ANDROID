package com.swapi.swapiV1.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Sealed class to manage the app's navigation routes.
 * Defines each screen with its route, a UI label, and an icon.
 */
sealed class ScreenNavigation(val route: String, val label: String, val icon: ImageVector) {
    // --- Main and Auth Screens ---
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
    object NewPublication : ScreenNavigation("new_publication", "Create", Icons.Default.Add) // <-- Corregido

    object SavedPosts : ScreenNavigation("saved_posts", "Guardados", Icons.Default.Bookmark)

    // --- Category Screens (accessed from Home) ---
    object Sales : ScreenNavigation("sales", "Sales", Icons.Default.ShoppingCart) // <-- Corregido
    object Rents : ScreenNavigation("rents", "Rents", Icons.Default.Key)
    object Services : ScreenNavigation("services", "Services", Icons.Default.Build) // <-- Corregido
    object Ads : ScreenNavigation("ads", "Ads", Icons.Default.Campaign) // <-- Corregido

    // --- Detail screen with dynamic argument ---
    // The route contains "{productId}" to indicate it's a parameter.
    object ProductDetail : ScreenNavigation("product_detail/{productId}", "Detail", Icons.Default.Store) { // <-- Label corregida
        /**
         * Helper function to build the detail route with a specific product ID.
         * @param productId The ID of the product to display.
         * @return The complete route to navigate to the detail screen.
         */
        fun createRoute(productId: String) = "product_detail/$productId"
    }
}