package com.swapi.swapiV1.publication.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapi.swapiV1.home.model.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// En NewPublicationViewModel.kt

class NewPublicationViewModel : ViewModel() {
    private val repository = PostRepository()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _publishSuccess = MutableStateFlow<Boolean?>(null)
    val publishSuccess: StateFlow<Boolean?> = _publishSuccess

    // --- NUEVO: Variable para el mensaje de error ---
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

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
            _errorMessage.value = null // Limpiamos errores previos

            // Recibimos el par (exito, mensaje)
            val result = repository.createPost(context, title, description, price, category, imageUri)

            _isLoading.value = false

            if (result.first) {
                _publishSuccess.value = true
            } else {
                _publishSuccess.value = false
                _errorMessage.value = result.second // Guardamos el mensaje del backend
            }
        }
    }

    fun resetState() {
        _publishSuccess.value = null
        _errorMessage.value = null
    }
}