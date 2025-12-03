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

    /**
     * Actualiza una publicación existente.
     * Retorna un Pair<Exito, CodigoDeError>.
     * * Si falla, leemos manualmente el cuerpo del error (errorBody) para buscar
     * un campo "code" específico (ej: "PROFANITY_DETECTED").
     */
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
                Pair(true, null)
            } else {
                // Retrofit no convierte automáticamente los errores a objetos.
                // Debemos leer el string crudo y parsearlo con JSONObject.
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

    /**
     * Crea una publicación con imagen (Multipart).
     * Requiere convertir Strings a RequestBody y Uris a Archivos físicos.
     */
    suspend fun createPost(
        context: Context,
        title: String,
        description: String,
        price: String,
        category: String,
        imageUri: Uri?
    ): Pair<Boolean, String?> {
        return try {
            // En Multipart, cada texto debe tener su tipo de medio (text/plain)
            val titlePart = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val descPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val pricePart = price.toRequestBody("text/plain".toMediaTypeOrNull())
            val catPart = category.lowercase().toRequestBody("text/plain".toMediaTypeOrNull())

            var imagePart: MultipartBody.Part? = null

            // Si el usuario seleccionó imagen, debemos transformarla
            if (imageUri != null) {
                val file = uriToFile(context, imageUri)
                if (file != null) {
                    // "image/*" indica que aceptamos jpg, png, etc.
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    // "image" es el nombre del campo que espera el backend (upload.single('image'))
                    imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
                }
            }

            val response = api.createPost(imagePart, titlePart, descPart, pricePart, catPart)

            if (response.isSuccessful) {
                Pair(true, null)
            } else {
                // Parseo manual del error para detectar groserías u otros fallos de negocio
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

    /**
     * Función auxiliar crítica para Android moderno.
     * Las Apps no pueden leer archivos directamente de la galería por seguridad.
     * Esta función copia el contenido de la Uri (puntero lógico) a un archivo temporal
     * en la caché de la app, el cual sí podemos subir a internet.
     */
    private fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val contentResolver = context.contentResolver
            // Crea un archivo vacío temporal
            val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)

            // Abre flujos de entrada (lectura del Uri) y salida (escritura al archivo temp)
            val inputStream = contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(tempFile)

            // Copia los bytes
            inputStream?.copyTo(outputStream)

            // Cierra flujos para liberar memoria
            inputStream?.close()
            outputStream.close()

            tempFile
        } catch (e: Exception) {
            Log.e("PostRepo", "Error convirtiendo Uri a File: ${e.message}")
            null
        }
    }
}