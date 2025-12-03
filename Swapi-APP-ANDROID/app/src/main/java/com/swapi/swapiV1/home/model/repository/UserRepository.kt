package com.swapi.swapiV1.home.model.repository

import android.util.Log
import com.swapi.swapiV1.home.model.dto.Product
import com.swapi.swapiV1.home.model.network.ToggleSaveResponse
import com.swapi.swapiV1.home.model.network.UserApiImpl

class UserRepository {
    private val api = UserApiImpl.service

    /**
     * Obtiene la lista de publicaciones guardadas (favoritos).
     * * Nota de diseño: Retorna List<Product>? (nulleable).
     * - Si retorna null: Hubo un error de conexión (mostrar mensaje de error).
     * - Si retorna lista vacía: La conexión fue exitosa, pero el usuario no tiene favoritos.
     * Esto permite a la UI diferenciar entre "Fallo" y "Vacío".
     */
    suspend fun getSavedPosts(): List<Product>? {
        return try {
            val response = api.getMySavedPosts()
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("UserRepo", "Error servidor getSavedPosts: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("UserRepo", "Excepción red getSavedPosts: ${e.message}")
            null
        }
    }

    /**
     * Acción de guardar/quitar favorito.
     * Retorna el objeto ToggleSaveResponse que nos dirá el estado final (saved: true/false).
     * Si retorna null, significa que la acción falló y la UI no debe cambiar el icono.
     */
    suspend fun toggleSave(postId: String): ToggleSaveResponse? {
        return try {
            val response = api.toggleSavedPost(postId)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("UserRepo", "Error toggleSave: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("UserRepo", "Excepción toggleSave: ${e.message}")
            null
        }
    }
}