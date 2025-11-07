package com.swapi.swapiV1.home.productdetail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.swapi.swapiV1.home.model.repository.HomeRepository

class ProductDetailViewModelFactory(
    private val productId: String,
    private val repository: HomeRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductDetailViewModel(productId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}