package com.swapi.swapiV1.login.model.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Objeto singleton que configura y provee una única instancia de Retrofit para toda la app.
object RetrofitProvider {
    // URL base para todas las peticiones de la API.
    private const val BASE_URL = "https://eleventenbackend.onrender.com"

    // Interceptor para registrar en Logcat los detalles de las peticiones y respuestas HTTP.
    // Es muy útil para depurar (debug).
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Cliente HTTP personalizado (OkHttp) que utiliza el interceptor de logging.
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    // Instancia de Retrofit configurada.
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL) // 1. Define la URL base.
        .addConverterFactory(GsonConverterFactory.create()) // 2. Agrega un convertidor para JSON (Gson).
        .client(client) // 3. Asigna el cliente OkHttp personalizado.
        .build()

    // Crea la implementación de la interfaz 'AuthApi' para que el resto de la app pueda usarla.
    val authApi: AuthApi = retrofit.create(AuthApi::class.java)
}