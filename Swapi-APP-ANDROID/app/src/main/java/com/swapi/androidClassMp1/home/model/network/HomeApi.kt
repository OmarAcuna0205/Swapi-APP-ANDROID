package com.swapi.androidClassMp1.home.model.network

import com.swapi.androidClassMp1.home.model.dto.HomeScreenResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// URL base de GitHub Gist
private const val BASE_URL = "https://gist.githubusercontent.com"

// Objeto singleton para crear y proveer la instancia de Retrofit
private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

// Interface que define los endpoints de la API
interface HomeApi {
    @GET("/iancorral/8068b8b9513c0f06e7a745c63cfd2787/raw/1b4bee629a614058500936974f8717aea631e5dd/HomeData.json")
    suspend fun getHomeData(): HomeScreenResponse
}

// Objeto público para acceder a la implementación de la API
object HomeApiImpl {
    val retrofitApi : HomeApi by lazy {
        retrofit.create(HomeApi::class.java)
    }
}