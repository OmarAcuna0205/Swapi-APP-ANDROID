package com.swapi.swapiV1.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapi.swapiV1.home.model.dto.Product
import com.swapi.swapiV1.home.model.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// UI State
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

    // New state to notify the View when a post is deleted
    private val _deleteSuccess = MutableStateFlow(false)
    val deleteSuccess: StateFlow<Boolean> = _deleteSuccess.asStateFlow()

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
                    _uiState.value = MyPostsUiState.Error("Error loading posts.")
                }
            } catch (e: Exception) {
                _uiState.value = MyPostsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            val success = repository.deletePost(postId)
            if (success) {
                loadMyPosts() // Reload local list
                _deleteSuccess.value = true // Notify UI to trigger Home refresh
            } else {
                // Handle error if needed
                loadMyPosts()
            }
        }
    }

    fun resetDeleteState() {
        _deleteSuccess.value = false
    }
}