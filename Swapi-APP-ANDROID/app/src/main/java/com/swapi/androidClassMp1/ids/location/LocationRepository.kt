package com.swapi.androidClassMp1.ids.location

import com.swapi.androidClassMp1.ids.location.models.LocationModel

class LocationRepository(private val apiService: LocationApiService) {
    suspend fun fetchLocations(): List<LocationModel> {
        return apiService.getLocations()
    }
}