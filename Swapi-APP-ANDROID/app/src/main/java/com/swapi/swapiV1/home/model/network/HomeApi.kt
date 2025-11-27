package com.swapi.swapiV1.home.model.network

import com.swapi.swapiV1.home.model.dto.Product
import com.swapi.swapiV1.login.model.network.RetrofitProvider
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query // <--- Nuevo import

interface HomeApi {
    // Ahora aceptamos una categoría opcional (null si queremos todos)
    @GET("api/post")
    suspend fun getPosts(
        @Query("category") category: String? = null
    ): Response<List<Product>>

    @GET("api/post/{id}")
    suspend fun getProduct(@Path("id") id: String): Response<Product>
}

object HomeApiImpl {
    val service: HomeApi by lazy {
        RetrofitProvider.createService(HomeApi::class.java)
    }
}