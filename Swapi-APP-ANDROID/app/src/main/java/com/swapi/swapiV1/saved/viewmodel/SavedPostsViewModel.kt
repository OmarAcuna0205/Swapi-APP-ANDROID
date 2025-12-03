package com.swapi.swapiV1.saved.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapi.swapiV1.home.model.dto.Product
import com.swapi.swapiV1.home.model.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SavedUIState {
    object Loading : SavedUIState()
    data class Success(val products: List<Product>) : SavedUIState()
    data class Error(val code: String) : SavedUIState()
}

class SavedPostsViewModel : ViewModel() {
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
                    _uiState.value = SavedUIState.Error("ERROR_GET_SAVED")
                }
            } catch (e: Exception) {
                _uiState.value = SavedUIState.Error("ERROR_GENERICO")
            }
        }
    }

    // --- FUNCIÓN CORREGIDA ---
    fun removeSavedPost(productId: String) {
        viewModelScope.launch {
            // Llamamos al repo. Si devuelve algo distinto de null, es que el HTTP fue 200 OK.
            val response = repository.toggleSave(productId)

            // CORRECCIÓN: Quitamos el check de ".success" que no existe en tu data class.
            // Si response != null, asumimos que el toggle funcionó.
            if (response != null) {
                val currentState = _uiState.value
                if (currentState is SavedUIState.Success) {
                    // Filtramos la lista localmente para quitar el item borrado
                    val updatedList = currentState.products.filter { it.id != productId }
                    _uiState.value = SavedUIState.Success(updatedList)
                }
            }
        }
    }
}