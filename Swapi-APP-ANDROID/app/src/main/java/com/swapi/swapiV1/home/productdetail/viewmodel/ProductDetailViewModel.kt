package com.swapi.swapiV1.home.productdetail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapi.swapiV1.home.model.dto.Product
import com.swapi.swapiV1.home.model.repository.HomeRepository
import com.swapi.swapiV1.home.model.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProductDetailUiState {
    object Loading : ProductDetailUiState()
    data class Success(val product: Product) : ProductDetailUiState()
    data class Error(val message: String) : ProductDetailUiState()
}

class ProductDetailViewModel(
    private val productId: String,
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val userRepository = UserRepository()

    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Loading)
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    init {
        loadProductAndCheckSavedStatus()
    }

    private fun loadProductAndCheckSavedStatus() {
        viewModelScope.launch {
            _uiState.value = ProductDetailUiState.Loading
            try {
                // --- CORRECCIÓN AQUÍ: Usamos 'getProductById' que es como se llama en tu Repo ---
                val product = homeRepository.getProductById(productId)

                if (product != null) {
                    _uiState.value = ProductDetailUiState.Success(product)

                    val mySavedPosts = userRepository.getSavedPosts()
                    val isInMyList = mySavedPosts?.any { it.id == productId } == true
                    _isSaved.value = isInMyList

                } else {
                    _uiState.value = ProductDetailUiState.Error("Producto no encontrado")
                }
            } catch (e: Exception) {
                _uiState.value = ProductDetailUiState.Error("Error: ${e.message}")
            }
        }
    }

    fun toggleSave() {
        viewModelScope.launch {
            val response = userRepository.toggleSave(productId)

            if (response != null) {
                _isSaved.value = response.saved
            } else {
                // Opcional: Manejar error
            }
        }
    }
}