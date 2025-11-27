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

    init {
        loadProduct()
    }

    private fun loadProduct() {
        viewModelScope.launch {
            _uiState.value = EditUiState.Loading
            // Usamos HomeRepository para traer los detalles actuales
            val product = homeRepository.getProductById(postId)
            if (product != null) {
                _uiState.value = EditUiState.Success(product)
            } else {
                _uiState.value = EditUiState.Error("No se encontró la publicación")
            }
        }
    }

    fun saveChanges(title: String, description: String, price: String, category: String) {
        viewModelScope.launch {
            _uiState.value = EditUiState.Loading
            val priceDouble = price.toDoubleOrNull() ?: 0.0

            val success = postRepository.updatePost(
                id = postId,
                title = title,
                description = description,
                price = priceDouble,
                category = category
            )
            _updateSuccess.value = success
        }
    }

    fun resetState() {
        _updateSuccess.value = null
    }
}