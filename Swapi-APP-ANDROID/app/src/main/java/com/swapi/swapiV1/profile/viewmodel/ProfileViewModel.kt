package com.swapi.swapiV1.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapi.swapiV1.utils.datastore.DataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Estado de la UI
data class ProfileUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val profileImageUrl: String = ""
)

class ProfileViewModel(
    private val dataStore: DataStoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Recolectamos el nombre real guardado en el login
            dataStore.userNameFlow.collect { realName ->
                _uiState.value = ProfileUiState(
                    isLoading = false,
                    userName = realName,
                    profileImageUrl = "" // Pendiente hasta que tengas endpoint de avatar
                )
            }
        }
    }
}