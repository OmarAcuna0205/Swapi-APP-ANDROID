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

    // Patrón "Backing Property":
    // 1. _uiState (Privado y Mutable): Solo el ViewModel puede modificarlo.
    // 2. uiState (Público e Inmutable): La vista solo puede leerlo (StateFlow), protegiendo la integridad de los datos.
    private val _uiState = MutableStateFlow<HomeUIState>(HomeUIState.Loading)
    val uiState: StateFlow<HomeUIState> = _uiState.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        // Carga automática al instanciar el ViewModel (cuando se abre la pantalla)
        loadProducts()
    }

    fun loadProducts() {
        // viewModelScope.launch inicia una corutina que muere automáticamente si el ViewModel se destruye.
        // Esto evita fugas de memoria si el usuario cierra la app mientras se cargan los datos.
        viewModelScope.launch {

            // Lógica de UX (Experiencia de Usuario):
            // Si es una recarga manual (pull-to-refresh), NO ponemos el estado en Loading.
            // Si lo hiciéramos, la pantalla parpadearía en blanco.
            // Solo ponemos Loading si es la primera vez que entramos.
            if (!_isRefreshing.value) {
                _uiState.value = HomeUIState.Loading
            }

            try {
                val products = repository.getProducts()
                _uiState.value = HomeUIState.Success(products)
            } catch (e: Exception) {
                // Usamos un código de error constante para que la UI decida qué texto mostrar
                _uiState.value = HomeUIState.Error("ERROR_LOAD_HOME")
            } finally {
                // El bloque 'finally' es crucial aquí.
                // Se ejecuta SIEMPRE, ya sea que la petición tenga éxito o falle.
                // Asegura que el spinner de "cargando" del pull-to-refresh se oculte.
                _isRefreshing.value = false
            }
        }
    }

    // Función simple para actualizar el texto mientras el usuario escribe (Two-way binding manual)
    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    // Acción provocada por el gesto de deslizar hacia abajo
    fun onRefresh() {
        _isRefreshing.value = true
        loadProducts()
    }
}