package com.swapi.swapiV1.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapi.swapiV1.home.model.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: HomeRepository
) : ViewModel() {

    // Estado principal de la UI (Carga, Éxito con productos, Error)
    private val _uiState = MutableStateFlow<HomeUIState>(HomeUIState.Loading)
    val uiState: StateFlow<HomeUIState> = _uiState.asStateFlow()

    // --- NUEVAS VARIABLES PARA EL BUSCADOR ---
    // Controla si la barrita de búsqueda está visible o no
    private val _showSearchBar = MutableStateFlow(false)
    val showSearchBar: StateFlow<Boolean> = _showSearchBar.asStateFlow()

    // Controla el texto que el usuario escribe
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = HomeUIState.Loading
            try {
                val products = repository.getProducts()
                _uiState.value = HomeUIState.Success(products)
            } catch (e: Exception) {
                _uiState.value = HomeUIState.Error("Error al cargar: ${e.message}")
            }
        }
    }

    // --- FUNCIONES QUE FALTABAN ---

    // Se llama cada vez que escribes una letra
    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    // Se llama al picarle a la lupa para mostrar/ocultar
    fun onToggleSearchBar() {
        _showSearchBar.value = !_showSearchBar.value
        if (!_showSearchBar.value) {
            // Si la cierran, limpiamos el texto para que vuelvan a salir todos los productos
            _searchText.value = ""
        }
    }

    // Se llama al darle "Enter" o "Buscar" en el teclado
    fun onSearchSubmit() {
        // Por ahora el filtrado es local (en HomeView), así que aquí no necesitamos
        // llamar al backend, pero podrías agregar lógica extra si quisieras.
        println("Buscando: ${_searchText.value}")
    }
}