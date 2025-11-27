package com.swapi.swapiV1.home.model.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.swapi.swapiV1.home.model.dto.Product
import com.swapi.swapiV1.home.model.dto.UpdatePostRequest // <--- ¡IMPORTANTE IMPORTAR ESTO!
import com.swapi.swapiV1.home.model.network.PostApiImpl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class PostRepository {
    private val api = PostApiImpl.service

    suspend fun getMyPosts(): List<Product>? {
        return try {
            val response = api.getMyPosts()
            if (response.isSuccessful) response.body() ?: emptyList() else null
        } catch (e: Exception) {
            Log.e("PostRepo", "Exception getMyPosts: ${e.message}")
            null
        }
    }

    suspend fun deletePost(id: String): Boolean {
        return try {
            val response = api.deletePost(id)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("PostRepo", "Exception deletePost: ${e.message}")
            false
        }
    }

    // --- CORREGIDO: Usamos UpdatePostRequest en vez de Map ---
    suspend fun updatePost(
        id: String,
        title: String,
        description: String,
        price: Double,
        category: String
    ): Boolean {
        return try {
            // AQUÍ ESTABA EL ERROR: Usamos la clase, no un mapa
            val requestBody = UpdatePostRequest(
                title = title,
                description = description,
                price = price,
                category = category.lowercase()
            )

            val response = api.updatePost(id, requestBody)

            if (!response.isSuccessful) {
                // Logueamos el error del servidor si falla
                Log.e("PostRepo", "Error update: ${response.code()} - ${response.errorBody()?.string()}")
            }
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("PostRepo", "Exception updatePost: ${e.message}")
            false
        }
    }

    suspend fun createPost(
        context: Context,
        title: String,
        description: String,
        price: String,
        category: String,
        imageUri: Uri?
    ): Boolean {
        return try {
            val titlePart = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val descPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val pricePart = price.toRequestBody("text/plain".toMediaTypeOrNull())
            val catPart = category.lowercase().toRequestBody("text/plain".toMediaTypeOrNull())

            var imagePart: MultipartBody.Part? = null
            if (imageUri != null) {
                val file = uriToFile(context, imageUri)
                if (file != null) {
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
                }
            }

            val response = api.createPost(imagePart, titlePart, descPart, pricePart, catPart)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("PostRepo", "Exception createPost: ${e.message}")
            false
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val contentResolver = context.contentResolver
            val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
            val inputStream = contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(tempFile)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            tempFile
        } catch (e: Exception) {
            null
        }
    }
}