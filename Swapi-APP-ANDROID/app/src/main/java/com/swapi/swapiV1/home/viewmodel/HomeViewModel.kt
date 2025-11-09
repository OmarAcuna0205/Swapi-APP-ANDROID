package com.swapi.swapiV1.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapi.swapiV1.home.model.dto.HomeSectionDto
import com.swapi.swapiV1.home.model.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

// (Asumo que tu archivo HomeUIState.kt está en esta misma ruta)
// sealed interface HomeUIState { ... }

class HomeViewModel(private val repository: HomeRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUIState>(HomeUIState.Loading)
    val uiState: StateFlow<HomeUIState> = _uiState.asStateFlow()

    private val _showSearchBar = MutableStateFlow(false)
    val showSearchBar: StateFlow<Boolean> = _showSearchBar.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    init {
        fetchHomeData()
    }

    fun onToggleSearchBar() {
        _showSearchBar.value = !_showSearchBar.value
        // Si se cierra la barra, limpia el texto y restaura los datos
        if (!_showSearchBar.value) {
            _searchText.value = ""
            fetchHomeData() // Vuelve a cargar los datos originales
        }
    }

    // --- CAMBIO PRINCIPAL ---
    // Ahora, cada vez que el texto cambia, se ejecuta la búsqueda.
    fun onSearchTextChange(text: String) {
        _searchText.value = text // 1. Actualiza el texto en la barra

        if (text.isBlank()) {
            // 2a. Si el texto está vacío, restaura la lista original
            fetchHomeData()
        } else {
            // 2b. Si hay texto, ejecuta la lógica de búsqueda INMEDIATAMENTE
            viewModelScope.launch {
                // (No mostramos Loading para que se sienta más rápido)
                try {
                    val searchResults = repository.searchListings(text)
                    _uiState.value = HomeUIState.Success(sections = searchResults)
                } catch (e: Exception) {
                    _uiState.value = HomeUIState.Error("Error al buscar: ${e.message}")
                }
            }
        }
    }

    // Esta función es para la acción del teclado (Enter)
    fun onSearchSubmit() {
        // La búsqueda ya se hizo "en vivo".
        // No necesitamos hacer nada extra, la TopBar ya oculta el teclado.
    }
    // -------------------------

    fun fetchHomeData() {
        // (Esta función se queda igual)
        viewModelScope.launch {
            _uiState.value = HomeUIState.Loading
            try {
                val response = repository.getHomeScreenData()
                _uiState.value = HomeUIState.Success(sections = response.homeScreen)
            } catch (e: IOException) {
                _uiState.value = HomeUIState.Error("Error de conexión. Revisa tu internet.")
            } catch (e: Exception) {
                _uiState.value = HomeUIState.Error("Ocurrió un error inesperado: ${e.message}")
            }
        }
    }
}