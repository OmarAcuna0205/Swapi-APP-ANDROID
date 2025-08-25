package com.swapi.androidClassMp1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.swapi.androidClassMp1.utils.datastore.DataStoreManager
import com.swapi.androidClassMp1.firstpartial.onboarding.viewmodel.OnboardingViewModel
import com.swapi.androidClassMp1.firstpartial.onboarding.views.OnboardingView
import com.swapi.androidClassMp1.navigation.TabBarNavigationView
import com.swapi.androidClassMp1.ui.theme.AndroidClassMP1Theme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val ds = DataStoreManager(this)

        setContent {
            AndroidClassMP1Theme {
                val scope = rememberCoroutineScope()
                val vm: OnboardingViewModel = viewModel()

                // Estado nulo mientras se obtiene el valor real del Flow
                val onboardingDone: Boolean? by ds.onboardingDoneFlow.collectAsState(initial = null)

                when (onboardingDone) {
                    null -> SplashLoader() // loader / splash temporal
                    false -> OnboardingView(
                        viewModel = vm,
                        onFinish = {
                            scope.launch { ds.setOnboardingDone(true) }
                        }
                    )
                    true -> TabBarNavigationView()
                }
            }
        }
    }
}

@Composable
private fun SplashLoader() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
