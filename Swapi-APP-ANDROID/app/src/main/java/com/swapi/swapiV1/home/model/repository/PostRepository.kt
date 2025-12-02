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
import org.json.JSONObject
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

    // --- ACTUALIZADO: Ahora devuelve Pair(Exito, CódigoError) ---
    suspend fun updatePost(
        id: String,
        title: String,
        description: String,
        price: Double,
        category: String
    ): Pair<Boolean, String?> {
        return try {
            val requestBody = UpdatePostRequest(
                title = title,
                description = description,
                price = price,
                category = category.lowercase()
            )

            val response = api.updatePost(id, requestBody)

            if (response.isSuccessful) {
                // Éxito
                Pair(true, null)
            } else {
                // Error: Leemos el código del backend
                val errorBody = response.errorBody()?.string()
                val code = try {
                    val json = JSONObject(errorBody ?: "")
                    json.optString("code", "ERROR_UPDATE_POST")
                } catch (e: Exception) {
                    "ERROR_UPDATE_POST"
                }
                Log.e("PostRepo", "Error update: $code")
                Pair(false, code)
            }
        } catch (e: Exception) {
            Log.e("PostRepo", "Exception updatePost: ${e.message}")
            Pair(false, "ERROR_CONNECTION")
        }
    }

    // --- ACTUALIZADO: Lee el campo "code" ---
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
                Pair(true, null)
            } else {
                // Error: Leemos el código para saber si fueron groserías u otra cosa
                val errorBody = response.errorBody()?.string()
                val code = try {
                    val json = JSONObject(errorBody ?: "")
                    json.optString("code", "ERROR_CREATE_POST")
                } catch (e: Exception) {
                    "ERROR_CREATE_POST"
                }
                Pair(false, code)
            }
        } catch (e: Exception) {
            Log.e("PostRepo", "Exception createPost: ${e.message}")
            Pair(false, "ERROR_CONNECTION")
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