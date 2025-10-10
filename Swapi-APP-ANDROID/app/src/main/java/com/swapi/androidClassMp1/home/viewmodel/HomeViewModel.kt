package com.swapi.androidClassMp1.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapi.androidClassMp1.home.model.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class HomeViewModel(private val repository: HomeRepository) : ViewModel() {

    // 🔹 Cambiado a StateFlow
    private val _uiState = MutableStateFlow<HomeUIState>(HomeUIState.Loading)
    val uiState: StateFlow<HomeUIState> = _uiState

    init {
        fetchHomeData()
    }

    private fun fetchHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUIState.Loading
            try {
                val response = repository.getHomeScreenData()
                _uiState.value = HomeUIState.Success(response.homeScreen)
            } catch (e: IOException) {
                _uiState.value = HomeUIState.Error("Error de conexión. Revisa tu internet.")
            } catch (e: Exception) {
                _uiState.value = HomeUIState.Error("Ocurrió un error inesperado.")
            }
        }
    }
}
