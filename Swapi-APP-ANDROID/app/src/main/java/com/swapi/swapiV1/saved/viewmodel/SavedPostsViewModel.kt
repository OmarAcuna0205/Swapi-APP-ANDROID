package com.swapi.swapiV1.saved.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapi.swapiV1.home.model.dto.Product
import com.swapi.swapiV1.home.model.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Representa los estados de la pantalla de Guardados.
 * Permite manejar la carga, el éxito con datos y los errores de forma estructurada.
 */
sealed class SavedUIState {
    object Loading : SavedUIState()
    data class Success(val products: List<Product>) : SavedUIState()
    data class Error(val code: String) : SavedUIState()
}

/**
 * ViewModel para gestionar las publicaciones guardadas por el usuario.
 * Se encarga de cargar la lista y eliminar elementos de guardados.
 */
class SavedPostsViewModel : ViewModel() {
    // Repositorio para interactuar con la API de usuario (guardados).
    private val repository = UserRepository()

    // Estado observable de la UI. Inicialmente en carga.
    private val _uiState = MutableStateFlow<SavedUIState>(SavedUIState.Loading)
    val uiState: StateFlow<SavedUIState> = _uiState.asStateFlow()

    init {
        loadSavedPosts()
    }

    /**
     * Carga la lista de publicaciones guardadas desde el servidor.
     */
    fun loadSavedPosts() {
        viewModelScope.launch {
            _uiState.value = SavedUIState.Loading
            try {
                // Petición al repositorio para obtener los posts
                val savedProducts = repository.getSavedPosts()
                if (savedProducts != null) {
                    _uiState.value = SavedUIState.Success(savedProducts)
                } else {
                    // Si la respuesta es nula, indicamos un error genérico de obtención
                    _uiState.value = SavedUIState.Error("ERROR_GET_SAVED")
                }
            } catch (e: Exception) {
                // Captura de excepciones de red u otros errores inesperados
                _uiState.value = SavedUIState.Error("ERROR_GENERICO")
            }
        }
    }

    /**
     * Elimina una publicación de la lista de guardados.
     * Realiza la petición al servidor y actualiza la lista localmente para una respuesta inmediata.
     */
    fun removeSavedPost(productId: String) {
        viewModelScope.launch {
            // Llamamos al repo para hacer el toggle (quitar guardado).
            // Si devuelve un objeto no nulo, significa que la operación HTTP fue exitosa (200 OK).
            val response = repository.toggleSave(productId)

            // Validación de éxito basada en la respuesta no nula
            if (response != null) {
                val currentState = _uiState.value
                // Solo actualizamos si ya tenemos una lista cargada (Success)
                if (currentState is SavedUIState.Success) {
                    // Filtramos la lista localmente: Creamos una nueva lista excluyendo el item borrado.
                    // Esto evita tener que recargar toda la lista desde el servidor, mejorando la UX.
                    val updatedList = currentState.products.filter { it.id != productId }
                    _uiState.value = SavedUIState.Success(updatedList)
                }
            }
        }
    }
}