package com.swapi.swapiV1.utils

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.swapi.swapiV1.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Actividad inicial de la aplicación (Splash Screen).
 * Muestra el logotipo de Swapi durante unos segundos mientras se inicializan recursos
 * y luego redirige automáticamente a la MainActivity.
 */
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Establecemos el contenido de la pantalla usando Jetpack Compose.
        // SplashScreenView contiene la UI (logo, fondo, etc.).
        setContent { SplashScreenView() }

        // Lanzamos una corrutina vinculada al ciclo de vida de la actividad (lifecycleScope).
        // Esto asegura que si el usuario cierra la app durante el splash, la corrutina se cancele
        // y no intente abrir la MainActivity innecesariamente.
        lifecycleScope.launch {
            // Simulamos un tiempo de carga o espera estética de 1.5 segundos.
            delay(1500)

            // Creamos un Intent para iniciar la actividad principal.
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))

            // Finalizamos esta actividad para sacarla de la pila (Back Stack).
            // Esto evita que el usuario pueda volver al Splash presionando el botón "Atrás" desde el Home.
            finish()
        }
    }
}