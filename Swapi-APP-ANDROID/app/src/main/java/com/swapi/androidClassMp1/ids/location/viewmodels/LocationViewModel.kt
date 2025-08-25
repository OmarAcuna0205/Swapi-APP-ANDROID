package com.swapi.androidClassMp1.ids.location.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapi.androidClassMp1.ids.location.LocationApiService
import com.swapi.androidClassMp1.ids.location.LocationRepository
import com.swapi.androidClassMp1.ids.location.models.LocationModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.collections.List

class LocationViewModel : ViewModel() {

    private val apiService = LocationApiService.create()
    private val repository = LocationRepository(apiService)

    private val _locations = MutableStateFlow<List<LocationModel>>(emptyList())
    val locations = _locations.asStateFlow()

    init {
        loadLocations()
    }

    private fun loadLocations() {
        viewModelScope.launch {
            try {
                _locations.value = repository.fetchLocations()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}