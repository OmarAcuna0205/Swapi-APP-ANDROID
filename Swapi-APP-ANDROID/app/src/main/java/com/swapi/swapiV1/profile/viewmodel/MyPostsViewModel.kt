package com.swapi.swapiV1.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapi.swapiV1.home.model.dto.Product
import com.swapi.swapiV1.home.model.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Representa los posibles estados de la interfaz "Mis Publicaciones".
 * El uso de Sealed Class garantiza que la UI solo pueda estar en uno de estos estados a la vez.
 */
sealed class MyPostsUiState {
    object Loading : MyPostsUiState()
    data class Success(val posts: List<Product>) : MyPostsUiState()
    data class Error(val message: String) : MyPostsUiState()
}

/**
 * ViewModel encargado de gestionar la lógica de negocio para las publicaciones del usuario.
 * @param repository Repositorio inyectado para realizar las llamadas a la API (Get/Delete).
 */
class MyPostsViewModel(
    private val repository: PostRepository
) : ViewModel() {

    // Estado principal reactivo. La vista se suscribe a este flujo para actualizarse automáticamente.
    private val _uiState = MutableStateFlow<MyPostsUiState>(MyPostsUiState.Loading)
    val uiState: StateFlow<MyPostsUiState> = _uiState.asStateFlow()

    // Estado específico para notificar eventos de eliminación.
    // Se separa del uiState principal porque es un evento transitorio (side-effect) y no un estado persistente de la pantalla.
    private val _deleteSuccess = MutableStateFlow(false)
    val deleteSuccess: StateFlow<Boolean> = _deleteSuccess.asStateFlow()

    init {
        // Al crearse el ViewModel, iniciamos la carga de datos inmediatamente.
        loadMyPosts()
    }

    /**
     * Ejecuta una corrutina para obtener la lista de posts del usuario desde el backend.
     * Actualiza el _uiState según el resultado de la operación asíncrona.
     */
    fun loadMyPosts() {
        viewModelScope.launch {
            _uiState.value = MyPostsUiState.Loading
            try {
                val posts = repository.getMyPosts()
                if (posts != null) {
                    _uiState.value = MyPostsUiState.Success(posts)
                } else {
                    _uiState.value = MyPostsUiState.Error("Error loading posts.")
                }
            } catch (e: Exception) {
                _uiState.value = MyPostsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Solicita la eliminación lógica de un post por su ID.
     * Si la operación es exitosa, recarga la lista local para reflejar el cambio.
     */
    fun deletePost(postId: String) {
        viewModelScope.launch {
            val success = repository.deletePost(postId)
            if (success) {
                loadMyPosts() // Refrescamos la lista localmente
                _deleteSuccess.value = true // Notificamos a la vista para mostrar feedback (ej. Toast)
            } else {
                // Manejo de error o recarga por seguridad
                loadMyPosts()
            }
        }
    }

    /**
     * Resetea la bandera de eliminación exitosa.
     * Importante llamar a esto después de mostrar el mensaje de éxito para evitar
     * que se vuelva a disparar al rotar la pantalla o recomponer.
     */
    fun resetDeleteState() {
        _deleteSuccess.value = false
    }
}