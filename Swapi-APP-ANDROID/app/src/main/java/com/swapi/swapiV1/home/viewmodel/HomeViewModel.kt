package com.swapi.swapiV1.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapi.swapiV1.home.model.repository.HomeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: HomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUIState>(HomeUIState.Loading)
    val uiState: StateFlow<HomeUIState> = _uiState.asStateFlow()

    // --- VARIABLES PARA BÚSQUEDA ---
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    // --- VARIABLES PARA REFRESH ---
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadProducts()
    }

    // Ahora es pública para llamarla desde el Refresh
    fun loadProducts() {
        viewModelScope.launch {
            // Si no estamos refrescando (carga inicial), mostramos Loading general
            if (!_isRefreshing.value) {
                _uiState.value = HomeUIState.Loading
            }

            try {
                val products = repository.getProducts()
                _uiState.value = HomeUIState.Success(products)
            } catch (e: Exception) {
                _uiState.value = HomeUIState.Error("Error al cargar: ${e.message}")
            } finally {
                // Siempre apagamos el indicador de refresh
                _isRefreshing.value = false
            }
        }
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun onRefresh() {
        _isRefreshing.value = true
        loadProducts()
    }

    // Ya no necesitamos onToggleSearchBar ni showSearchBar
}