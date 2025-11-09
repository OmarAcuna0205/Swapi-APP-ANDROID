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
import androidx.navigation.compose.rememberNavController
import com.swapi.swapiV1.login.views.LoginView
import com.swapi.swapiV1.navigation.TabBarNavigationView
import com.swapi.swapiV1.navigation.ScreenNavigation
import com.swapi.swapiV1.onboarding.viewmodel.OnboardingViewModel
import com.swapi.swapiV1.onboarding.views.OnboardingView
import com.swapi.swapiV1.ui.theme.SwapiTheme
import com.swapi.swapiV1.utils.datastore.DataStoreManager
import kotlinx.coroutines.launch
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

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

                    val startDestination = if (isLoggedIn == true) "tabbar" else "login"

                    NavHost(
                        navController = navController,
                        startDestination = startDestination // Asignación segura
                    ) {
                        composable("login") {
                            LoginView(navController, dataStore)
                        }
                        composable("tabbar") {
                            TabBarNavigationView(
                                dataStore = dataStore,
                                startDestination = ScreenNavigation.Home.route,
                                // --- CAMBIO: Definimos la lógica de logout ---
                                onLogout = {
                                    scope.launch {
                                        dataStore.setLoggedIn(false)
                                        dataStore.setUserName("Usuario") // Resetea el nombre
                                    }
                                    // Navega al login y BORRA todo el historial
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                                // ---------------------------------------
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