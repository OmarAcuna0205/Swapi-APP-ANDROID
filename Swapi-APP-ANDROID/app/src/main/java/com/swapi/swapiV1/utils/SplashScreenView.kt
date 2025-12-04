package com.swapi.swapiV1.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swapi.swapiV1.R

/**
 * Componente Composable que renderiza la interfaz visual de la Splash Screen.
 * Muestra el logo de la aplicación centrado en la pantalla junto con el nombre "Swapi".
 */
@Composable
fun SplashScreenView() {
    // Contenedor principal que ocupa todo el tamaño de la pantalla.
    // Usamos un Box para poder centrar el contenido fácilmente en ambos ejes.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background), // Fondo adaptable al tema (claro/oscuro)
        contentAlignment = Alignment.Center
    ) {
        // Columna para apilar verticalmente la imagen y el texto.
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // Imagen del logo de la aplicación.
            Image(
                painter = painterResource(id = R.drawable.swapi),
                contentDescription = "Swapi logo", // Descripción para accesibilidad
                modifier = Modifier.size(160.dp) // Tamaño fijo para el logo
            )

            // Espaciador vertical para separar la imagen del texto.
            Spacer(modifier = Modifier.height(16.dp))

            // Texto con el nombre de la aplicación.
            Text(
                text = "Swapi",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground // Color de texto que contrasta con el fondo
            )
        }
    }
}