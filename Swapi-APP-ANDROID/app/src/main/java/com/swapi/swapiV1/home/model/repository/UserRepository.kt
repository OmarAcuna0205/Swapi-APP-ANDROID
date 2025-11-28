package com.swapi.swapiV1.home.model.repository

import android.util.Log
import com.swapi.swapiV1.home.model.dto.Product
import com.swapi.swapiV1.home.model.network.ToggleSaveResponse
import com.swapi.swapiV1.home.model.network.UserApiImpl

class UserRepository {
    private val api = UserApiImpl.service

    // Obtener la lista de guardados
    suspend fun getSavedPosts(): List<Product>? {
        return try {
            val response = api.getMySavedPosts()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            Log.e("UserRepo", "Error getSavedPosts: ${e.message}")
            null
        }
    }

    // Darle like/guardar
    suspend fun toggleSave(postId: String): ToggleSaveResponse? {
        return try {
            val response = api.toggleSavedPost(postId)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            Log.e("UserRepo", "Error toggleSave: ${e.message}")
            null
        }
    }
}