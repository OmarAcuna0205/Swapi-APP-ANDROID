package com.swapi.swapiV1.home.model.network

import com.swapi.swapiV1.home.model.dto.Product
import com.swapi.swapiV1.login.model.network.RetrofitProvider
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Modelo de respuesta exclusivo para la acción de favoritos.
 * El servidor nos devuelve el estado final ('saved': true/false)
 * para que sepamos si debemos pintar el corazón lleno o vacío en la interfaz.
 */
data class ToggleSaveResponse(
    val saved: Boolean,
    val message: String
)

interface UserApi {

    // Reutilizamos el modelo 'Product' porque, técnicamente,
    // una publicación guardada es idéntica a una publicación del Home.
    @GET("api/user/saved")
    suspend fun getMySavedPosts(): Response<List<Product>>

    /**
     * Endpoint tipo "Toggle" (Interruptor).
     * No tenemos un endpoint para guardar y otro para borrar.
     * Llamamos a este único endpoint y el servidor decide:
     * - Si ya existe en favoritos -> Lo elimina.
     * - Si no existe -> Lo agrega.
     */
    @POST("api/user/save/{postId}")
    suspend fun toggleSavedPost(@Path("postId") postId: String): Response<ToggleSaveResponse>
}

object UserApiImpl {
    val service: UserApi by lazy {
        RetrofitProvider.createService(UserApi::class.java)
    }
}