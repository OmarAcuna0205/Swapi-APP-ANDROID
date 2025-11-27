package com.swapi.swapiV1.home.model.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.swapi.swapiV1.home.model.dto.Product
import com.swapi.swapiV1.home.model.network.PostApiImpl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class PostRepository {
    private val api = PostApiImpl.service

    suspend fun createPost(
        context: Context,
        title: String,
        description: String,
        price: String,
        category: String,
        imageUri: Uri?
    ): Boolean {
        return try {
            // 1. Convertir textos a RequestBody
            val titlePart = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val descPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val pricePart = price.toRequestBody("text/plain".toMediaTypeOrNull())
            // Convertimos la categoría a minúsculas para que coincida con el backend ("Ventas" -> "ventas")
            val catPart = category.lowercase().toRequestBody("text/plain".toMediaTypeOrNull())

            // 2. Convertir imagen a Multipart (Si existe)
            var imagePart: MultipartBody.Part? = null
            if (imageUri != null) {
                val file = uriToFile(context, imageUri) // Función auxiliar abajo
                if (file != null) {
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
                }
            }

            // 3. Enviar al backend
            val response = api.createPost(imagePart, titlePart, descPart, pricePart, catPart)

            if (response.isSuccessful) {
                Log.d("PostRepo", "Publicación creada: ${response.body()}")
                true
            } else {
                Log.e("PostRepo", "Error: ${response.code()} - ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("PostRepo", "Excepción: ${e.message}")
            false
        }
    }

    // Función auxiliar para convertir URI a File real (Android es complicado con esto)
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