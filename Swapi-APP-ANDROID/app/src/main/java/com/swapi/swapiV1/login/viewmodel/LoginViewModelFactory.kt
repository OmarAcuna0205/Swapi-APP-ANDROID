package com.swapi.swapiV1.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.swapi.swapiV1.login.model.repository.AuthRepository

/**
 * Clase "Factory" (fábrica) necesaria para crear instancias de LoginViewModel.
 * Se utiliza porque nuestro LoginViewModel tiene dependencias en su constructor (el AuthRepository),
 * y el sistema necesita que le digamos cómo proveer esas dependencias.
 */
class LoginViewModelFactory(private val repo: AuthRepository) : ViewModelProvider.Factory {

    /**
     * Este método es llamado por el sistema Android cuando necesita crear el ViewModel.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Creamos y devolvemos una instancia de LoginViewModel, pasándole el repositorio.
        // El cast 'as T' es necesario para cumplir con el tipo genérico que requiere la interfaz.
        return LoginViewModel(repo) as T
    }
}
