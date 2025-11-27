package com.swapi.swapiV1.home.viewmodel

import com.swapi.swapiV1.home.model.dto.Product

sealed interface HomeUIState {
    data object Loading : HomeUIState
    // Ahora guardamos una lista de Productos, no de Secciones
    data class Success(val products: List<Product>) : HomeUIState
    data class Error(val message: String) : HomeUIState
}