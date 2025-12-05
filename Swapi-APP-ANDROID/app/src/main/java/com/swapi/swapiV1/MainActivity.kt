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

/**
 * Punto de entrada principal de la aplicación.
 * Configura la navegación, inicializa dependencias globales y gestiona el flujo inicial (Splash/Onboarding/Login).
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. INICIALIZAR RETROFIT:
        // Es vital configurar el RetrofitProvider con el contexto de la aplicación al inicio.
        // Esto permite que el AuthInterceptor acceda al DataStore para leer/guardar el token JWT.
        RetrofitProvider.setup(applicationContext)

        enableEdgeToEdge() // Habilita el diseño de borde a borde (status bar transparente).

        // Instancia única del gestor de persistencia local.
        val dataStore = DataStoreManager(this)

        setContent {
            SwapiTheme {
                val navController = rememberNavController()
                val scope = rememberCoroutineScope()

                // ViewModel para el Onboarding (lógica de paginación).
                val onboardingViewModel: OnboardingViewModel = viewModel()

                // --- INYECCIÓN DE DEPENDENCIAS (Login) ---
                // CORREGIDO: Ahora AuthRepository se instancia vacío, porque él mismo obtiene
                // la conexión desde RetrofitProvider internamente.
                val authRepository = AuthRepository()

                // Creamos una instancia compartida del LoginViewModel para que los datos persistan
                // a través de los pasos del registro (Email -> Código -> Perfil).
                val loginViewModel: LoginViewModel = viewModel(
                    factory = LoginViewModelFactory(authRepository, dataStore)
                )

                // --- ESTADOS DE SESIÓN (Reactivos) ---
                // Observamos los Flows del DataStore para determinar qué pantalla mostrar al inicio.
                // 'initial = null' nos permite mostrar un Splash mientras se leen los datos del disco.
                val onboardingDone: Boolean? by dataStore.onboardingDoneFlow.collectAsState(initial = null)
                val isLoggedIn: Boolean? by dataStore.isLoggedInFlow.collectAsState(initial = null)

                // LÓGICA DE NAVEGACIÓN INICIAL:
                if (onboardingDone == null || isLoggedIn == null) {
                    // Estado de carga inicial (lectura de preferencias).
                    SplashLoader()
                } else if (onboardingDone == false) {
                    // Si el usuario no ha visto el onboarding, se muestra.
                    OnboardingView(
                        viewModel = onboardingViewModel,
                        onFinish = {
                            // Al terminar, guardamos la bandera y la UI se recompondrá automáticamente.
                            scope.launch { dataStore.setOnboardingDone(true) }
                        }
                    )
                } else {
                    // Si ya vio el onboarding, decidimos si va al Home (tabbar) o al Login.
                    val startDestination = if (isLoggedIn == true) "tabbar" else ScreenNavigation.Login.route

                    // Configuración del Grafo de Navegación principal.
                    NavHost(
                        navController = navController,
                        startDestination = startDestination
                    ) {
                        // --- PANTALLA DE LOGIN ---
                        composable(ScreenNavigation.Login.route) {
                            LoginView(navController, dataStore)
                        }

                        // --- FLUJO DE REGISTRO (Wizard) ---
                        // Comparten el mismo 'loginViewModel' para mantener el estado del formulario.

                        // Paso 1: Ingreso de correo electrónico.
                        composable(ScreenNavigation.SignUpEmail.route) {
                            SignUpEmailView(
                                navHostController = navController,
                                viewModel = loginViewModel
                            )
                        }

                        // Paso 2: Verificación de código OTP.
                        // Recibe el email como argumento para mostrarlo en la UI.
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

                        // Paso 3: Completar perfil (nombre, contraseña, etc.).
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

                        // --- APLICACIÓN PRINCIPAL (Bottom Navigation) ---
                        // Contenedor para las pantallas principales (Home, Ventas, Perfil).
                        composable("tabbar") {
                            TabBarNavigationView(
                                dataStore = dataStore,
                                startDestination = ScreenNavigation.Home.route,
                                onLogout = {
                                    // Lógica de cierre de sesión:
                                    // 1. Limpiar datos locales (token, sesión).
                                    scope.launch {
                                        dataStore.setLoggedIn(false)
                                        dataStore.setUserName("Usuario")
                                        dataStore.saveAccessToken("") // Borrado de seguridad del token
                                    }
                                    // 2. Navegar al Login y limpiar el historial de navegación (Back Stack)
                                    // para que el usuario no pueda volver atrás con el botón físico.
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

/**
 * Componente simple de carga (Spinner) que se muestra mientras se leen las preferencias iniciales.
 * Evita parpadeos o decisiones incorrectas de navegación.
 */
@Composable
private fun SplashLoader() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}