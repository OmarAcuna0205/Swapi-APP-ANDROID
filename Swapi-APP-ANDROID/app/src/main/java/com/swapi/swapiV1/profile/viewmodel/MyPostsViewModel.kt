package com.swapi.swapiV1.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapi.swapiV1.home.model.dto.Product
import com.swapi.swapiV1.home.model.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Estado de la UI
sealed class MyPostsUiState {
    object Loading : MyPostsUiState()
    data class Success(val posts: List<Product>) : MyPostsUiState()
    data class Error(val message: String) : MyPostsUiState()
}

class MyPostsViewModel(
    private val repository: PostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MyPostsUiState>(MyPostsUiState.Loading)
    val uiState: StateFlow<MyPostsUiState> = _uiState.asStateFlow()

    init {
        loadMyPosts()
    }

    fun loadMyPosts() {
        viewModelScope.launch {
            _uiState.value = MyPostsUiState.Loading
            try {
                val posts = repository.getMyPosts()
                if (posts != null) {
                    _uiState.value = MyPostsUiState.Success(posts)
                } else {
                    _uiState.value = MyPostsUiState.Error("Error al cargar publicaciones.")
                }
            } catch (e: Exception) {
                _uiState.value = MyPostsUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            // Podrías poner un estado de carga aquí si quisieras bloquear la UI
            val success = repository.deletePost(postId)
            if (success) {
                // Recargamos la lista para que desaparezca el item borrado
                loadMyPosts()
            } else {
                // Opcional: Manejar error de borrado (toast, snackbar, etc)
                // Por ahora solo recargamos para asegurar consistencia
                loadMyPosts()
            }
        }
    }
}