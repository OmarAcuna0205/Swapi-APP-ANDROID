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

/**
 * Define los estados posibles de la interfaz de edición.
 * Permite manejar la carga inicial de los datos del producto antes de mostrarlos.
 */
sealed class EditUiState {
    object Loading : EditUiState()
    data class Success(val product: Product) : EditUiState()
    data class Error(val msg: String) : EditUiState()
}

/**
 * ViewModel para la pantalla de "Editar Publicación".
 * Gestiona dos flujos principales:
 * 1. Cargar los datos actuales del post (HomeRepository).
 * 2. Enviar los cambios actualizados al servidor (PostRepository).
 */
class EditPostViewModel(
    private val postId: String,
    private val homeRepository: HomeRepository,
    private val postRepository: PostRepository
) : ViewModel() {

    // Estado principal de la UI (Carga de datos inicial)
    private val _uiState = MutableStateFlow<EditUiState>(EditUiState.Loading)
    val uiState: StateFlow<EditUiState> = _uiState.asStateFlow()

    // Estado para notificar si la actualización fue exitosa (evento de un solo disparo)
    private val _updateSuccess = MutableStateFlow<Boolean?>(null)
    val updateSuccess: StateFlow<Boolean?> = _updateSuccess.asStateFlow()

    // Estado específico para capturar códigos de error del backend (ej. "PALABRAS_OFENSIVAS")
    private val _errorCode = MutableStateFlow<String?>(null)
    val errorCode: StateFlow<String?> = _errorCode.asStateFlow()

    init {
        loadProduct()
    }

    /**
     * Carga la información actual del producto para pre-llenar los campos del formulario.
     */
    private fun loadProduct() {
        viewModelScope.launch {
            _uiState.value = EditUiState.Loading
            // Reutilizamos el HomeRepository porque ya tiene la lógica para obtener un producto por ID
            val product = homeRepository.getProductById(postId)
            if (product != null) {
                _uiState.value = EditUiState.Success(product)
            } else {
                _uiState.value = EditUiState.Error("POST_NO_ENCONTRADO")
            }
        }
    }

    /**
     * Envía los datos modificados al servidor.
     * Maneja la conversión de tipos y procesa la respuesta compuesta del repositorio.
     */
    fun saveChanges(title: String, description: String, price: String, category: String) {
        viewModelScope.launch {
            // Conversión segura del precio (String -> Double)
            val priceDouble = price.toDoubleOrNull() ?: 0.0

            // Llamada al repositorio que devuelve un Pair<Boolean, String?>
            // first = éxito (true/false)
            // second = código de error (si falló)
            val result = postRepository.updatePost(
                id = postId,
                title = title,
                description = description,
                price = priceDouble,
                category = category
            )

            // Procesamiento del resultado
            if (result.first) {
                _updateSuccess.value = true
                _errorCode.value = null
            } else {
                _updateSuccess.value = false
                // Capturamos el código de error específico enviado por el backend
                _errorCode.value = result.second ?: "ERROR_UPDATE_POST"
            }
        }
    }

    /**
     * Limpia los estados de éxito y error.
     * Se llama después de que la Vista ha reaccionado (navegado o mostrado el error)
     * para evitar que los eventos se disparen nuevamente al rotar la pantalla.
     */
    fun resetState() {
        _updateSuccess.value = null
        _errorCode.value = null
    }
}