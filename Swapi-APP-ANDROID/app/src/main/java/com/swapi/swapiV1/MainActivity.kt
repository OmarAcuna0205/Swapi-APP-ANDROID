package com.swapi.swapiV1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType // <-- Asegúrate de tener este
import androidx.navigation.compose.*
import androidx.navigation.navArgument // <-- Y este
import com.swapi.swapiV1.login.views.LoginView
import com.swapi.swapiV1.login.views.SignUpCodeView
import com.swapi.swapiV1.login.views.SignUpEmailView
import com.swapi.swapiV1.login.views.SignUpProfileView
import com.swapi.swapiV1.navigation.TabBarNavigationView
import com.swapi.swapiV1.navigation.ScreenNavigation
import com.swapi.swapiV1.onboarding.viewmodel.OnboardingViewModel
import com.swapi.swapiV1.onboarding.views.OnboardingView
import com.swapi.swapiV1.ui.theme.SwapiTheme
import com.swapi.swapiV1.utils.datastore.DataStoreManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // El DataStore se crea una vez aquí y se comparte
        val dataStore = DataStoreManager(this)

        setContent {
            SwapiTheme {
                val navController = rememberNavController()
                val scope = rememberCoroutineScope()
                val onboardingViewModel: OnboardingViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

                // Leemos AMBOS flujos
                val onboardingDone: Boolean? by dataStore.onboardingDoneFlow.collectAsState(initial = null)
                val isLoggedIn: Boolean? by dataStore.isLoggedInFlow.collectAsState(initial = null)

                if (onboardingDone == null || isLoggedIn == null) {
                    SplashLoader()
                }

                else if (onboardingDone == false) {
                    OnboardingView(
                        viewModel = onboardingViewModel,
                        onFinish = { scope.launch { dataStore.setOnboardingDone(true) } }
                    )
                }

                else {

                    val startDestination = if (isLoggedIn == true) "tabbar" else ScreenNavigation.Login.route

                    NavHost(
                        navController = navController,
                        startDestination = startDestination // Asignación segura
                    ) {

                        // --- RUTA 1: LOGIN ---
                        composable(ScreenNavigation.Login.route) {
                            LoginView(navController, dataStore)
                        }

                        // --- INICIO DEL FLUJO DE REGISTRO ---

                        // RUTA 2: REGISTRO (Paso 1: Email)
                        composable(ScreenNavigation.SignUpEmail.route) {
                            SignUpEmailView(navHostController = navController)
                        }

                        // RUTA 3: REGISTRO (Paso 2: Código)
                        composable(
                            route = ScreenNavigation.SignUpCode.route,
                            arguments = listOf(navArgument("email") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val email = backStackEntry.arguments?.getString("email") ?: "error@swapi.com"
                            SignUpCodeView(navHostController = navController, email = email)
                        }

                        // RUTA 4: REGISTRO (Paso 3: Perfil)
                        composable(
                            route = ScreenNavigation.SignUpProfile.route,
                            arguments = listOf(navArgument("email") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val email = backStackEntry.arguments?.getString("email") ?: "error@swapi.com"
                            SignUpProfileView(navHostController = navController, email = email)
                        }

                        // --- FIN DEL FLUJO DE REGISTRO ---

                        // --- RUTA 5: LA APP INTERNA ---
                        composable("tabbar") {
                            TabBarNavigationView(
                                dataStore = dataStore,
                                startDestination = ScreenNavigation.Home.route,
                                onLogout = {
                                    scope.launch {
                                        dataStore.setLoggedIn(false)
                                        dataStore.setUserName("Usuario") // Resetea el nombre
                                    }
                                    navController.navigate(ScreenNavigation.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SplashLoader() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}