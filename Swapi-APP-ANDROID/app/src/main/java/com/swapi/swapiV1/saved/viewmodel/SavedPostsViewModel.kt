package com.swapi.swapiV1.saved.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapi.swapiV1.home.model.dto.Product
import com.swapi.swapiV1.home.model.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Estado de la UI exclusivo para Guardados
sealed class SavedUIState {
    object Loading : SavedUIState()
    data class Success(val products: List<Product>) : SavedUIState()
    data class Error(val message: String) : SavedUIState()
}

class SavedPostsViewModel : ViewModel() {
    // Usamos el UserRepository que conecta con /api/user/saved
    private val repository = UserRepository()

    private val _uiState = MutableStateFlow<SavedUIState>(SavedUIState.Loading)
    val uiState: StateFlow<SavedUIState> = _uiState.asStateFlow()

    init {
        loadSavedPosts()
    }

    fun loadSavedPosts() {
        viewModelScope.launch {
            _uiState.value = SavedUIState.Loading
            try {
                val savedProducts = repository.getSavedPosts()
                if (savedProducts != null) {
                    _uiState.value = SavedUIState.Success(savedProducts)
                } else {
                    _uiState.value = SavedUIState.Error("No se pudieron cargar los guardados")
                }
            } catch (e: Exception) {
                _uiState.value = SavedUIState.Error("Error: ${e.message}")
            }
        }
    }
}