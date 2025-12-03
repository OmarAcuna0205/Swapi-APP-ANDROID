package com.swapi.swapiV1.home.viewmodel

import com.swapi.swapiV1.home.model.dto.Product

/**
 * Define los únicos estados posibles para la pantalla de Inicio.
 * Al usar 'sealed interface', creamos una jerarquía cerrada. Esto es muy potente
 * porque obliga al compilador a asegurarse de que tu interfaz (Compose)
 * maneje TODOS los casos posibles en una sentencia 'when'.
 */
sealed interface HomeUIState {

    // Usamos 'data object' (Singleton) porque el estado de carga es idéntico siempre.
    // No transporta datos variables, solo indica "estoy ocupado".
    // Al ser un objeto, no gasta memoria creando múltiples instancias de 'Loading'.
    data object Loading : HomeUIState

    // Usamos 'data class' porque este estado sí transporta información dinámica:
    // la lista de productos que acabamos de recibir del servidor.
    data class Success(val products: List<Product>) : HomeUIState

    // Transporta el mensaje de error para que la UI decida si mostrar
    // una pantalla completa de error o solo un mensaje flotante (SnackBar).
    data class Error(val message: String) : HomeUIState
}