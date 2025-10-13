package com.swapi.androidClassMp1.home.model.network

import com.swapi.androidClassMp1.home.model.dto.HomeScreenResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// URL base de GitHub Gist
private const val BASE_URL = "https://gist.githubusercontent.com"

// --- CAMBIOS AQUÍ ---
// 1. Creamos el interceptor de logging
private val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY // BODY nos da toda la info
}

// 2. Creamos el cliente de OkHttp y le añadimos el interceptor
private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .build()

// 3. Modificamos Retrofit para que use nuestro nuevo cliente
private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .client(okHttpClient) // <-- AÑADIMOS EL CLIENTE AQUÍ
    .build()
// --- FIN DE LOS CAMBIOS ---


// Interface que define los endpoints de la API
interface HomeApi {
    @GET("/iancorral/8068b8b9513c0f06e7a745c63cfd2787/raw/e9fb2bfa0326038d097dcd82139b6371b1f38607/HomeData.json")
    suspend fun getHomeData(): HomeScreenResponse
}

// Objeto público para acceder a la implementación de la API
object HomeApiImpl {
    val retrofitApi : HomeApi by lazy {
        retrofit.create(HomeApi::class.java)
    }
}