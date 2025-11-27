package com.swapi.swapiV1.home.model.network

import com.swapi.swapiV1.home.model.dto.Product
import com.swapi.swapiV1.login.model.network.RetrofitProvider
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path // <--- Importante

interface HomeApi {
    @GET("api/post")
    suspend fun getPosts(): Response<List<Product>>

    // --- NUEVA FUNCIÓN ---
    // Pide un post específico por su ID
    @GET("api/post/{id}")
    suspend fun getProduct(@Path("id") id: String): Response<Product>
}

object HomeApiImpl {
    val service: HomeApi by lazy {
        RetrofitProvider.createService(HomeApi::class.java)
    }
}