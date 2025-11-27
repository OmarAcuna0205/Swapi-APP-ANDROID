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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.swapi.swapiV1.login.model.network.AuthApiImpl
import com.swapi.swapiV1.login.model.network.RetrofitProvider
import com.swapi.swapiV1.login.model.repository.AuthRepository
import com.swapi.swapiV1.login.viewmodel.LoginViewModel
import com.swapi.swapiV1.login.viewmodel.LoginViewModelFactory
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

        // 1. INICIALIZAR RETROFIT (Vital para que funcione el Interceptor)
        RetrofitProvider.setup(applicationContext)

        enableEdgeToEdge()

        val dataStore = DataStoreManager(this)

        setContent {
            SwapiTheme {
                val navController = rememberNavController()
                val scope = rememberCoroutineScope()

                // ViewModels
                val onboardingViewModel: OnboardingViewModel = viewModel()

                // --- INYECCIÓN DEL LOGIN VIEWMODEL COMPARTIDO ---
                val authRepository = AuthRepository(AuthApiImpl.service)

                // CORRECCIÓN AQUÍ: Pasamos 'dataStore' a la Factory
                val loginViewModel: LoginViewModel = viewModel(
                    factory = LoginViewModelFactory(authRepository, dataStore)
                )

                // DataStore States
                val onboardingDone: Boolean? by dataStore.onboardingDoneFlow.collectAsState(initial = null)
                val isLoggedIn: Boolean? by dataStore.isLoggedInFlow.collectAsState(initial = null)

                if (onboardingDone == null || isLoggedIn == null) {
                    SplashLoader()
                } else if (onboardingDone == false) {
                    OnboardingView(
                        viewModel = onboardingViewModel,
                        onFinish = { scope.launch { dataStore.setOnboardingDone(true) } }
                    )
                } else {
                    val startDestination = if (isLoggedIn == true) "tabbar" else ScreenNavigation.Login.route

                    NavHost(
                        navController = navController,
                        startDestination = startDestination
                    ) {
                        // --- RUTA 1: LOGIN ---
                        composable(ScreenNavigation.Login.route) {
                            LoginView(navController, dataStore)
                        }

                        // --- RUTAS DE REGISTRO (Shared ViewModel) ---

                        // Paso 1: Email
                        composable(ScreenNavigation.SignUpEmail.route) {
                            SignUpEmailView(
                                navHostController = navController,
                                viewModel = loginViewModel
                            )
                        }

                        // Paso 2: Código
                        composable(
                            route = ScreenNavigation.SignUpCode.route,
                            arguments = listOf(navArgument("email") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            SignUpCodeView(
                                navHostController = navController,
                                viewModel = loginViewModel,
                                email = email
                            )
                        }

                        // Paso 3: Perfil
                        composable(
                            route = ScreenNavigation.SignUpProfile.route,
                            arguments = listOf(navArgument("email") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            SignUpProfileView(
                                navHostController = navController,
                                viewModel = loginViewModel,
                                email = email
                            )
                        }

                        // --- APP PRINCIPAL ---
                        composable("tabbar") {
                            TabBarNavigationView(
                                dataStore = dataStore,
                                startDestination = ScreenNavigation.Home.route,
                                onLogout = {
                                    scope.launch {
                                        dataStore.setLoggedIn(false)
                                        dataStore.setUserName("Usuario")
                                        dataStore.saveAccessToken("") // Limpiamos el token al salir
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