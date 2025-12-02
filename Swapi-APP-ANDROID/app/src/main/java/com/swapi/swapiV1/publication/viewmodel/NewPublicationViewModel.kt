package com.swapi.swapiV1.publication.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapi.swapiV1.home.model.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewPublicationViewModel : ViewModel() {

    private val repository = PostRepository()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _publishSuccess = MutableStateFlow<Boolean?>(null)
    val publishSuccess: StateFlow<Boolean?> = _publishSuccess

    // ✅ SOLO CÓDIGO DE ERROR (ej. "PALABRAS_OFENSIVAS")
    private val _errorCode = MutableStateFlow<String?>(null)
    val errorCode: StateFlow<String?> = _errorCode

    fun publish(
        context: Context,
        title: String,
        description: String,
        price: String,
        category: String,
        imageUri: Uri?
    ) {
        if (title.isBlank() || price.isBlank() || category.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true
            _errorCode.value = null

            // Repo regresa: Pair<success, errorCode?>
            val result = repository.createPost(
                context,
                title,
                description,
                price,
                category,
                imageUri
            )

            _isLoading.value = false

            if (result.first) {
                _publishSuccess.value = true
            } else {
                _publishSuccess.value = false
                _errorCode.value = result.second
            }
        }
    }

    fun resetState() {
        _publishSuccess.value = null
        _errorCode.value = null
    }
}
