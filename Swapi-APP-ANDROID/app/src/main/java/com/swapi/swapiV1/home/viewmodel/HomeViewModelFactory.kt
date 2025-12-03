package com.swapi.swapiV1.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.swapi.swapiV1.home.model.repository.HomeRepository

/**
 * Fábrica manual para inyectar dependencias en el HomeViewModel.
 * Android maneja el ciclo de vida de los ViewModels internamente, por lo que no podemos
 * hacer simplemente 'new HomeViewModel(repo)'. Debemos darle esta fábrica a Android
 * para que él sepa cómo construirlo cuando sea necesario.
 */
class HomeViewModelFactory(private val repository: HomeRepository) : ViewModelProvider.Factory {

    /**
     * Este método es llamado por el sistema Android cuando solicita un ViewModel.
     * <T> es un tipo genérico que representa cualquier subclase de ViewModel.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Verificamos si la clase solicitada es HomeViewModel (o una subclase)
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {

            // Supresión de advertencia: Sabemos que T es HomeViewModel porque
            // lo acabamos de verificar en el if de arriba, pero el compilador
            // no puede estar 100% seguro debido al "Type Erasure" de Java/Kotlin,
            // así que le decimos "confía en mí".
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        // Si alguien intenta usar esta fábrica para crear otro tipo de ViewModel, fallamos.
        throw IllegalArgumentException("Unknown ViewModel class: Clase desconocida para esta Factory")
    }
}