package com.swapi.swapiV1.publication.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapi.swapiV1.home.model.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de la lógica para crear una nueva publicación.
 * Gestiona el formulario de creación, la validación básica y la comunicación con el repositorio.
 */
class NewPublicationViewModel : ViewModel() {

    // Repositorio para realizar la petición POST al servidor.
    // Nota: Idealmente debería inyectarse en el constructor (como en EditPostViewModel),
    // pero instanciarlo aquí es aceptable para arquitecturas más simples.
    private val repository = PostRepository()

    // Estado de carga para mostrar un indicador visual (ProgressBar) mientras se sube la imagen/datos.
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Estado de éxito de la publicación (true = creado, false = error, null = estado inicial).
    private val _publishSuccess = MutableStateFlow<Boolean?>(null)
    val publishSuccess: StateFlow<Boolean?> = _publishSuccess

    // Almacena el código de error específico si la creación falla (ej. "PALABRAS_OFENSIVAS").
    private val _errorCode = MutableStateFlow<String?>(null)
    val errorCode: StateFlow<String?> = _errorCode

    /**
     * Inicia el proceso de publicación.
     * Realiza validaciones previas y lanza la corrutina para la operación de red.
     */

    fun publish(
        context: Context,
        title: String,
        description: String,
        price: String,
        category: String,
        imageUri: Uri?
    ) {
        // Validación básica: Campos obligatorios no pueden estar vacíos.
        if (title.isBlank() || price.isBlank() || category.isBlank()) return

        viewModelScope.launch {
            // Indicamos inicio de carga y limpiamos errores previos
            _isLoading.value = true
            _errorCode.value = null

            // Llamada al repositorio. createPost maneja internamente la conversión de la imagen (Uri -> File/Multipart).
            // Retorna un Pair<Boolean, String?> indicando éxito y posible código de error.
            val result = repository.createPost(
                context,
                title,
                description,
                price,
                category,
                imageUri
            )

            // Finalizamos carga
            _isLoading.value = false

            // Procesamos el resultado
            if (result.first) {
                _publishSuccess.value = true
            } else {
                _publishSuccess.value = false
                _errorCode.value = result.second // Capturamos el error para mostrarlo en la UI
            }
        }
    }

    fun resetState() {
        _publishSuccess.value = null
        _errorCode.value = null
    }
}