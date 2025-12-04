package com.swapi.swapiV1.publication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.swapi.swapiV1.home.model.repository.HomeRepository
import com.swapi.swapiV1.home.model.repository.PostRepository

/**
 * Factory personalizada para instanciar EditPostViewModel.
 * Es necesaria para poder inyectar parámetros (postId y repositorios) en el constructor del ViewModel,
 * ya que por defecto Android solo sabe crear ViewModels sin argumentos.
 */
class EditPostViewModelFactory(
    private val postId: String,
    private val homeRepository: HomeRepository,
    private val postRepository: PostRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Verificamos que la clase solicitada sea compatible con EditPostViewModel
        if (modelClass.isAssignableFrom(EditPostViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            // Retornamos la instancia con las dependencias inyectadas manualmente
            return EditPostViewModel(postId, homeRepository, postRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}