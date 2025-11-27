package com.swapi.swapiV1.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.swapi.swapiV1.login.model.repository.AuthRepository
import com.swapi.swapiV1.utils.datastore.DataStoreManager

class LoginViewModelFactory(
    private val repository: AuthRepository,
    private val dataStore: DataStoreManager // <-- Agregamos esto
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository, dataStore) as T // <-- Se lo pasamos al VM
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}