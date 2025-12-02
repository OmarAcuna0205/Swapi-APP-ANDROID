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
    data class Error(val code: String) : SavedUIState() // Cambiamos 'message' por 'code'
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
                    // Enviamos CÓDIGO, no texto
                    _uiState.value = SavedUIState.Error("ERROR_GET_SAVED")
                }
            } catch (e: Exception) {
                _uiState.value = SavedUIState.Error("ERROR_GENERICO")
            }
        }
    }
}