package com.swapi.swapiV1.home.model.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.swapi.swapiV1.home.model.dto.Product
import com.swapi.swapiV1.home.model.dto.UpdatePostRequest
import com.swapi.swapiV1.home.model.network.PostApiImpl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject // <--- IMPORTANTE: Para leer el mensaje de error del backend
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

    suspend fun updatePost(
        id: String,
        title: String,
        description: String,
        price: Double,
        category: String
    ): Boolean {
        return try {
            val requestBody = UpdatePostRequest(
                title = title,
                description = description,
                price = price,
                category = category.lowercase()
            )

            val response = api.updatePost(id, requestBody)

            if (!response.isSuccessful) {
                Log.e("PostRepo", "Error update: ${response.code()} - ${response.errorBody()?.string()}")
            }
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("PostRepo", "Exception updatePost: ${e.message}")
            false
        }
    }

    // --- CORREGIDO: Regresa Pair<Boolean, String?> para manejar el mensaje de error ---
    suspend fun createPost(
        context: Context,
        title: String,
        description: String,
        price: String,
        category: String,
        imageUri: Uri?
    ): Pair<Boolean, String?> {
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

            if (response.isSuccessful) {
                Pair(true, null) // Éxito, sin mensaje de error
            } else {
                // Leemos el cuerpo del error para sacar el mensaje "No se permiten palabras..."
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val json = JSONObject(errorBody ?: "")
                    json.optString("message", "Error al publicar")
                } catch (e: Exception) {
                    "Error al publicar"
                }
                Pair(false, errorMessage)
            }
        } catch (e: Exception) {
            Log.e("PostRepo", "Exception createPost: ${e.message}")
            Pair(false, "Error de conexión: ${e.message}")
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