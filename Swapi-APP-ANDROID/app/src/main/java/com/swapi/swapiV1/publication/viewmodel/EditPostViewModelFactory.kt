package com.swapi.swapiV1.publication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.swapi.swapiV1.home.model.repository.HomeRepository
import com.swapi.swapiV1.home.model.repository.PostRepository

class EditPostViewModelFactory(
    private val postId: String,
    private val homeRepository: HomeRepository,
    private val postRepository: PostRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditPostViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditPostViewModel(postId, homeRepository, postRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}