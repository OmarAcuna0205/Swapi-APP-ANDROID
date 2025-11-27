package com.swapi.swapiV1.home.model.repository

import android.util.Log
import com.swapi.swapiV1.home.model.dto.Product
import com.swapi.swapiV1.home.model.network.HomeApiImpl

class HomeRepository {
    private val api = HomeApiImpl.service

    // Traer todos
    suspend fun getProducts(): List<Product> {
        return try {
            val response = api.getPosts()
            if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
        } catch (e: Exception) {
            Log.e("HomeRepo", "Error getProducts: ${e.message}")
            emptyList()
        }
    }

    // --- NUEVA FUNCIÓN (La que te faltaba) ---
    suspend fun getProductById(id: String): Product? {
        return try {
            val response = api.getProduct(id)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("HomeRepo", "Error getProductById: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("HomeRepo", "Exception getProductById: ${e.message}")
            null
        }
    }
}