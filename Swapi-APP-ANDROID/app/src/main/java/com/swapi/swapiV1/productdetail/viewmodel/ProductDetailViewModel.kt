package com.swapi.swapiV1.productdetail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapi.swapiV1.home.model.dto.Product
import com.swapi.swapiV1.home.model.repository.HomeRepository
import com.swapi.swapiV1.home.model.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Representa los estados posibles de la pantalla de detalle.
 */
sealed class ProductDetailUiState {
    object Loading : ProductDetailUiState()
    data class Success(val product: Product) : ProductDetailUiState()
    data class Error(val messageCode: String) : ProductDetailUiState()
}

class ProductDetailViewModel(
    private val productId: String,
    private val homeRepository: HomeRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    // Estado principal de la carga del producto
    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Loading)
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    // Estado independiente para el botón de "Guardar/Favorito"
    // Lo separamos para que al dar like no se recargue toda la pantalla, solo el icono.
    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    init {
        loadProductAndCheckSavedStatus()
    }

    private fun loadProductAndCheckSavedStatus() {
        viewModelScope.launch {
            _uiState.value = ProductDetailUiState.Loading
            try {
                // 1. Buscamos el detalle del producto
                val product = homeRepository.getProductById(productId)

                if (product != null) {
                    _uiState.value = ProductDetailUiState.Success(product)

                    // 2. Si el producto existe, verificamos si el usuario ya lo tiene guardado
                    val mySavedPosts = userRepository.getSavedPosts()

                    // Verificamos si este ID existe en la lista de guardados
                    val isInMyList = mySavedPosts?.any { it.id == productId } == true
                    _isSaved.value = isInMyList

                } else {
                    _uiState.value = ProductDetailUiState.Error("POST_NO_ENCONTRADO")
                }
            } catch (e: Exception) {
                _uiState.value = ProductDetailUiState.Error("ERROR_LOAD_PRODUCT")
            }
        }
    }

    fun toggleSave() {
        viewModelScope.launch {
            try {
                // Llamamos al backend para invertir el estado (guardar/borrar)
                val response = userRepository.toggleSave(productId)

                if (response != null) {
                    // Actualizamos el icono con la respuesta real del servidor
                    _isSaved.value = response.saved
                }
            } catch (e: Exception) {
                // Si falla la petición (ej. sin internet), no hacemos nada visualmente
                // para no interrumpir la experiencia, o el icono volverá a su estado original
                // si implementaras una reversión optimista.
            }
        }
    }
}