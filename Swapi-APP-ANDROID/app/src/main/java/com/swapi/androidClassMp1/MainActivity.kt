package com.swapi.androidClassMp1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.swapi.androidClassMp1.login.views.LoginView
import com.swapi.androidClassMp1.navigation.TabBarNavigationView
import com.swapi.androidClassMp1.navigation.ScreenNavigation
import com.swapi.androidClassMp1.onboarding.viewmodel.OnboardingViewModel
import com.swapi.androidClassMp1.onboarding.views.OnboardingView
import com.swapi.androidClassMp1.ui.theme.AndroidClassMP1Theme
import com.swapi.androidClassMp1.utils.datastore.DataStoreManager
import kotlinx.coroutines.launch
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val dataStore = DataStoreManager(this)

        setContent {
            AndroidClassMP1Theme {
                val navController = rememberNavController()
                val scope = rememberCoroutineScope()
                val onboardingViewModel: OnboardingViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

                val onboardingDone: Boolean? by dataStore.onboardingDoneFlow.collectAsState(initial = null)
                val isLoggedIn: Boolean? by dataStore.isLoggedInFlow.collectAsState(initial = null)

                when (onboardingDone) {
                    null -> SplashLoader()
                    false -> OnboardingView(
                        viewModel = onboardingViewModel,
                        onFinish = { scope.launch { dataStore.setOnboardingDone(true) } }
                    )
                    true -> {
                        NavHost(
                            navController = navController,
                            startDestination = if (isLoggedIn == true) "tabbar" else "login"
                        ) {
                            composable("login") { LoginView(navController, dataStore) }
                            composable("tabbar") { TabBarNavigationView(startDestination = ScreenNavigation.Home.route) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SplashLoader() {
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
