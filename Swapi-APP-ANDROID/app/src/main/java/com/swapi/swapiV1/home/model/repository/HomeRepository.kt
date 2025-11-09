package com.swapi.swapiV1.home.model.repository

import com.swapi.swapiV1.home.model.dto.HomeScreenResponse
import com.swapi.swapiV1.home.model.network.HomeApi
import com.swapi.swapiV1.home.model.dto.ListingDto
import com.swapi.swapiV1.home.model.dto.HomeSectionDto // <-- CAMBIO: Import corregido

class HomeRepository(private val homeApi: HomeApi) {

    // --- Caché simple ---
    private var cachedResponse: HomeScreenResponse? = null
    // --------------------

    suspend fun getHomeScreenData(): HomeScreenResponse {
        // --- Usar el caché ---
        val cached = cachedResponse
        if (cached != null) {
            return cached
        }

        val response = homeApi.getHomeData()
        cachedResponse = response
        return response
        // ---------------------
    }

    suspend fun getProductById(productId: String): ListingDto? {
        val response = getHomeScreenData()
        // (La lógica que tenías estaba perfecta)
        return response.homeScreen.flatMap { it.listings }.find { it.id == productId }
    }

    // --- CAMBIO: NUEVA FUNCIÓN DE BÚSQUEDA (usa HomeSectionDto) ---
    suspend fun searchListings(query: String): List<HomeSectionDto> {
        // 1. Asegura que el caché esté lleno
        val response = getHomeScreenData()

        // 2. Filtra localmente las secciones y sus publicaciones
        val filteredSections = response.homeScreen.map { section ->
            // Filtra las publicaciones de esta sección
            val filteredListings = section.listings.filter { listing ->
                listing.title.contains(query, ignoreCase = true) ||
                        listing.user.name.contains(query, ignoreCase = true)
            }
            // Regresa una copia de la sección, pero solo con las publicaciones filtradas
            section.copy(listings = filteredListings)
        }.filter {
            // 3. Al final, elimina las secciones que quedaron vacías
            it.listings.isNotEmpty()
        }

        // 4. Si la lista de secciones filtradas está vacía,
        // crea una sección de "Resultados" con *todas* las publicaciones que coincidan
        if (filteredSections.isEmpty()) {
            val allListings = response.homeScreen.flatMap { it.listings }
            val globalSearchListings = allListings.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.user.name.contains(query, ignoreCase = true)
            }
            if (globalSearchListings.isNotEmpty()) {
                // Crea un HomeSectionDto con los resultados
                return listOf(HomeSectionDto(sectionTitle = "Resultados de la búsqueda", listings = globalSearchListings))
            }
        }

        return filteredSections
    }
    // ------------------------------------------
}