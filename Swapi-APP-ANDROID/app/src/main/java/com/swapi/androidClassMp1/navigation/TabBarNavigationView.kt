package com.swapi.androidClassMp1.navigation

import SecondPartialView
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController
import com.swapi.androidClassMp1.firstpartial.FirstPartialView
import com.swapi.androidClassMp1.ids.imc.views.IMCView
import com.swapi.androidClassMp1.ids.IdsView
import com.swapi.androidClassMp1.ids.location.views.LocationListScreen
import com.swapi.androidClassMp1.ids.login.views.LoginView
import com.swapi.androidClassMp1.ids.student.views.StudentView
import com.swapi.androidClassMp1.ids.sum.views.SumView
import com.swapi.androidClassMp1.ids.temperature.views.TempView
import com.swapi.androidClassMp1.thirdpartial.ThirdPartialView
import androidx.compose.ui.graphics.Color


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabBarNavigationView(navController: NavHostController = rememberNavController()) {
    val items = listOf(
        ScreenNavigation.Ids,
        ScreenNavigation.FirstPartial,
        ScreenNavigation.SecondPartial,
        ScreenNavigation.ThirdPartial
    )

    // Mapa de títulos por ruta (incluye tabs y pantallas internas)
    val routeTitles = remember {
        mapOf(
            ScreenNavigation.Ids.route to ScreenNavigation.Ids.label,
            ScreenNavigation.FirstPartial.route to ScreenNavigation.FirstPartial.label,
            ScreenNavigation.SecondPartial.route to ScreenNavigation.SecondPartial.label,
            ScreenNavigation.ThirdPartial.route to ScreenNavigation.ThirdPartial.label,

            // Rutas internas (ajusta a tus strings preferidos)
            ScreenNavigation.IMC.route to "IMC",
            ScreenNavigation.Login.route to "Login",
            ScreenNavigation.Sum.route to "Suma",
            ScreenNavigation.Temperature.route to "Temperatura",
            ScreenNavigation.StudentList.route to "Estudiantes",
            ScreenNavigation.Locations.route to "Ubicaciones"
        )
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    // Si usas nested graphs, puedes leer la jerarquía; aquí basta con la route actual:
    val currentRoute = navBackStackEntry?.destination?.route
    val currentTitle = routeTitles[currentRoute] ?: ""

    Scaffold(
        topBar = {
            // Puedes usar SmallTopAppBar o CenterAlignedTopAppBar
            CenterAlignedTopAppBar(
                title = { Text(text = "Android Omar Acuña 13097") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF87CEFA), // Azul
                titleContentColor = Color.White
            )
            )
        },
        bottomBar = {
            NavigationBar {
                items.forEach { screen ->
                    val selected = currentRoute == screen.route
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = selected,
                        onClick = {
                            if (!selected) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ScreenNavigation.Ids.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(ScreenNavigation.Ids.route) { IdsView(navController) }
            composable(ScreenNavigation.FirstPartial.route) { FirstPartialView() }
            composable(ScreenNavigation.SecondPartial.route) { SecondPartialView() }
            composable(ScreenNavigation.ThirdPartial.route) { ThirdPartialView(navController) }

            // Rutas internas
            composable(ScreenNavigation.IMC.route) { IMCView() }
            composable(ScreenNavigation.Login.route) { LoginView() }
            composable(ScreenNavigation.Sum.route) { SumView() }
            composable(ScreenNavigation.Temperature.route) { TempView() }
            composable(ScreenNavigation.StudentList.route) { StudentView() }
            composable(ScreenNavigation.Locations.route) { LocationListScreen() }
        }
    }
}
