package com.swapi.swapiV1.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapi.swapiV1.utils.datastore.DataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Representa el estado de la pantalla de Perfil (UI State).
 * Siguiendo el patrón MVI/MVVM, la UI solo debe reaccionar a cambios en este objeto
 * en lugar de manejar múltiples variables sueltas.
 */
data class ProfileUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val profileImageUrl: String = ""
)

/**
 * ViewModel encargado de la lógica de presentación del Perfil.
 * Gestiona la obtención de datos del usuario desde la persistencia local (DataStore).
 */
class ProfileViewModel(
    private val dataStore: DataStoreManager
) : ViewModel() {

    // Backing Property:
    // _uiState es mutable y privado para que solo el ViewModel pueda modificarlo.
    // uiState es público e inmutable (StateFlow) para que la UI solo pueda leerlo (observarlo).
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    /**
     * Carga la información del usuario de forma asíncrona.
     * Utiliza viewModelScope para asegurar que la corrutina se cancele si el ViewModel se destruye.
     */
    private fun loadUserProfile() {
        viewModelScope.launch {
            // Indicamos a la UI que estamos cargando
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Recolectamos el flujo (Flow) del nombre de usuario desde DataStore.
            // 'collect' suspende la ejecución y se reactiva cada vez que el valor cambia en DataStore,
            // garantizando que la UI siempre muestre el dato más actual.
            dataStore.userNameFlow.collect { realName ->
                _uiState.value = ProfileUiState(
                    isLoading = false,
                    userName = realName,
                    profileImageUrl = "" // Pendiente hasta futura implementación de avatar
                )
            }
        }
    }
}