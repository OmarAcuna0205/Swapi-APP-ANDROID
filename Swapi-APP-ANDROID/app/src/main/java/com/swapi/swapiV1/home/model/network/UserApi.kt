package com.swapi.swapiV1.home.model.network

import com.swapi.swapiV1.home.model.dto.Product
import com.swapi.swapiV1.login.model.network.RetrofitProvider
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// DTO para la respuesta del toggle (guardar/quitar)
data class ToggleSaveResponse(
    val saved: Boolean,
    val message: String
)

interface UserApi {
    // 1. Obtener mis guardados
    @GET("api/user/saved")
    suspend fun getMySavedPosts(): Response<List<Product>>

    // 2. Guardar o Quitar (Toggle)
    @POST("api/user/save/{postId}")
    suspend fun toggleSavedPost(@Path("postId") postId: String): Response<ToggleSaveResponse>
}

// Singleton para acceder a la API
object UserApiImpl {
    val service: UserApi by lazy {
        RetrofitProvider.createService(UserApi::class.java)
    }
}