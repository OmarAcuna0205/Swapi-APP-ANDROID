package com.swapi.swapiV1.publication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapi.swapiV1.home.model.dto.Product
import com.swapi.swapiV1.home.model.repository.HomeRepository
import com.swapi.swapiV1.home.model.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class EditUiState {
    object Loading : EditUiState()
    data class Success(val product: Product) : EditUiState()
    data class Error(val msg: String) : EditUiState()
}

class EditPostViewModel(
    private val postId: String,
    private val homeRepository: HomeRepository,
    private val postRepository: PostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditUiState>(EditUiState.Loading)
    val uiState: StateFlow<EditUiState> = _uiState.asStateFlow()

    private val _updateSuccess = MutableStateFlow<Boolean?>(null)
    val updateSuccess: StateFlow<Boolean?> = _updateSuccess.asStateFlow()

    // Variable para el código de error
    private val _errorCode = MutableStateFlow<String?>(null)
    val errorCode: StateFlow<String?> = _errorCode.asStateFlow()

    init {
        loadProduct()
    }

    private fun loadProduct() {
        viewModelScope.launch {
            _uiState.value = EditUiState.Loading
            val product = homeRepository.getProductById(postId)
            if (product != null) {
                _uiState.value = EditUiState.Success(product)
            } else {
                _uiState.value = EditUiState.Error("POST_NO_ENCONTRADO")
            }
        }
    }

    fun saveChanges(title: String, description: String, price: String, category: String) {
        viewModelScope.launch {
            // Convertimos el precio
            val priceDouble = price.toDoubleOrNull() ?: 0.0

            // --- CORRECCIÓN AQUÍ ---
            // 'result' es un Pair<Boolean, String?>
            val result = postRepository.updatePost(
                id = postId,
                title = title,
                description = description,
                price = priceDouble,
                category = category
            )

            // Usamos .first para ver si fue exitoso (Boolean)
            if (result.first) {
                _updateSuccess.value = true
                _errorCode.value = null
            } else {
                _updateSuccess.value = false
                // Usamos .second para obtener el código de error (String?)
                _errorCode.value = result.second ?: "ERROR_UPDATE_POST"
            }
        }
    }

    fun resetState() {
        _updateSuccess.value = null
        _errorCode.value = null
    }
}