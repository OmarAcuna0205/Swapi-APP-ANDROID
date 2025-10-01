package com.swapi.androidClassMp1.home.model.repository

import com.swapi.androidClassMp1.home.model.dto.HomeScreenResponse
import com.swapi.androidClassMp1.home.model.network.HomeApi

// Renombré tu archivo a HomeRepository.kt por convención
class HomeRepository(private val homeApi: HomeApi) {
    suspend fun getHomeScreenData(): HomeScreenResponse {
        return homeApi.getHomeData()
    }
}