package com.swapi.swapiV1.home.model.repository

import com.swapi.swapiV1.home.model.dto.HomeScreenResponse
import com.swapi.swapiV1.home.model.network.HomeApi
import com.swapi.swapiV1.home.model.dto.ListingDto

class HomeRepository(private val homeApi: HomeApi) {

    // Tu función existente
    suspend fun getHomeScreenData(): HomeScreenResponse {
        return homeApi.getHomeData()
    }

    // --- NUEVA FUNCIÓN ---
    suspend fun getProductById(productId: String): ListingDto? {
        // Obtenemos todos los datos
        val response = getHomeScreenData()
        // Buscamos en todas las secciones y aplanamos las listas de productos
        // Luego encontramos el producto que coincida con el ID
        return response.homeScreen.flatMap { it.listings }.find { it.id == productId }
    }
}