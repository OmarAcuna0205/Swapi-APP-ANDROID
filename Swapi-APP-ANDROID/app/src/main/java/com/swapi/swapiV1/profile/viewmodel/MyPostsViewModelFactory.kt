package com.swapi.swapiV1.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.swapi.swapiV1.home.model.repository.PostRepository

/**
 * Factory necesaria para crear el ViewModel 'MyPostsViewModel'.
 * Permite pasarle parámetros (como el repositorio) al constructor,
 * ya que por defecto los ViewModels no aceptan argumentos.
 */
class MyPostsViewModelFactory(
    private val repository: PostRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Verificamos que se esté solicitando el ViewModel correcto antes de crearlo
        if (modelClass.isAssignableFrom(MyPostsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            // Inyectamos el repositorio manualmente al instanciar la clase
            return MyPostsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}