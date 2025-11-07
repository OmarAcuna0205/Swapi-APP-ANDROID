package com.swapi.swapiV1.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 1. Definimos el estado de la UI
data class ProfileUiState(
    val isLoading: Boolean = true,
    val userName: String = "",
    val profileImageUrl: String = "" // URL para la imagen de perfil
)

// 2. Creamos el ViewModel
class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Simulamos la carga de datos del perfil cuando el ViewModel se crea
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            // Mostramos el indicador de carga
            _uiState.value = ProfileUiState(isLoading = true)

            // Simulamos una llamada a una API (ej. 1.5 segundos de espera)
            delay(1500)

            // Actualizamos el estado con los datos "cargados"
            _uiState.value = ProfileUiState(
                isLoading = false,
                userName = "Ian Corral", // Puedes reemplazarlo con datos reales
                profileImageUrl = "https://example.com/profile.jpg" // URL de imagen de ejemplo
            )
        }
    }
}