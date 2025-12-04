package com.swapi.swapiV1.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.swapi.swapiV1.utils.datastore.DataStoreManager

/**
 * Factory personalizada para instanciar ProfileViewModel.
 * Es necesaria porque nuestro ViewModel requiere recibir 'DataStoreManager' en su constructor
 * para poder acceder a los datos guardados localmente (como el nombre del usuario).
 */
class ProfileViewModelFactory(
    private val dataStore: DataStoreManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Validación estándar para asegurar que estamos creando el ViewModel correcto
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            // Inyección manual de la dependencia (DataStore) al crear la instancia
            return ProfileViewModel(dataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}