package com.swapi.swapiV1.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.swapi.swapiV1.login.model.repository.AuthRepository
import com.swapi.swapiV1.utils.datastore.DataStoreManager

/**
 * Fábrica necesaria porque nuestro LoginViewModel tiene parámetros en el constructor
 * (repo y dataStore). Android no puede inyectarlos automáticamente por sí solo.
 */
class LoginViewModelFactory(
    private val repository: AuthRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            // Aquí inyectamos las dependencias manualmente al crear la instancia
            return LoginViewModel(repository, dataStoreManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}