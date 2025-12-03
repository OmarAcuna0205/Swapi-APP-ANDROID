package com.swapi.swapiV1.home.model.repository

import android.util.Log
import com.swapi.swapiV1.home.model.dto.Product
import com.swapi.swapiV1.home.model.network.HomeApiImpl

class HomeRepository {
    private val api = HomeApiImpl.service

    /**
     * Obtiene la lista completa de productos.
     * Esta función es "a prueba de fallos": nunca lanzará una excepción a la vista.
     * Si algo sale mal, simplemente devuelve una lista vacía para que la UI no muestre nada
     * pero tampoco se rompa.
     */
    suspend fun getProducts(): List<Product> {
        return try {
            val response = api.getPosts()

            // Verificamos dos cosas:
            // 1. isSuccessful: El servidor respondió con código 200-299.
            // 2. body() != null: El servidor envió datos reales.
            // El operador elvis (?:) devuelve emptyList() si el cuerpo es nulo.
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            // Capturamos errores de red (sin internet, timeout, DNS fallido)
            Log.e("HomeRepo", "Error crítico obteniendo productos: ${e.message}")
            emptyList()
        }
    }

    /**
     * Busca un producto específico por ID.
     * Retorna Product? (nulleable) porque existe la posibilidad de que el ID no exista
     * o que la red falle. La vista deberá manejar este 'null'.
     */
    suspend fun getProductById(id: String): Product? {
        return try {
            val response = api.getProduct(id)
            if (response.isSuccessful) {
                response.body()
            } else {
                // Útil para debug: Si recibes un 404 o 500, lo verás en Logcat
                Log.e("HomeRepo", "Error del servidor (código ${response.code()}) al buscar ID: $id")
                null
            }
        } catch (e: Exception) {
            Log.e("HomeRepo", "Excepción de red al buscar detalle: ${e.message}")
            null
        }
    }
}