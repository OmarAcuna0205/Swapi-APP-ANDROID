package com.swapi.androidClassMp1.home.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapi.androidClassMp1.home.model.repository.HomeRepository
import kotlinx.coroutines.launch
import java.io.IOException

class HomeViewModel(private val repository: HomeRepository) : ViewModel() {

    var uiState: HomeUIState by mutableStateOf(HomeUIState.Loading)
        private set

    init {
        fetchHomeData()
    }

    private fun fetchHomeData() {
        viewModelScope.launch {
            uiState = HomeUIState.Loading
            try {
                val response = repository.getHomeScreenData()
                uiState = HomeUIState.Success(response.homeScreen)
            } catch (e: IOException) {
                uiState = HomeUIState.Error("Error de conexión. Revisa tu internet.")
            } catch (e: Exception) {
                uiState = HomeUIState.Error("Ocurrió un error inesperado.")
            }
        }
    }
}