package com.swapi.androidClassMp1.ids

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.swapi.androidClassMp1.navigation.ScreenNavigation

@Composable
fun IdsView(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { navController.navigate("IMCRoute") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ir a IMC")
        }
        Button(
            onClick = { navController.navigate("LoginRoute") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ir a Login")
        }
        Button(
            onClick = { navController.navigate("SumRoute") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ir a Sum")
        }
        Button(
            onClick = { navController.navigate("TemperatureRoute") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ir a Temperatura")
        }

        Button(
            onClick = { navController.navigate(ScreenNavigation.StudentList.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ir a Lista de Estudiantes")
        }
        Button(
            onClick = { navController.navigate(ScreenNavigation.Locations.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ir a Ubicaciones")
        }
    }
}