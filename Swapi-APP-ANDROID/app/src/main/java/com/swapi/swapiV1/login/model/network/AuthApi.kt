package com.swapi.swapiV1.login.model.network

import com.swapi.swapiV1.login.model.dto.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("/api/auth/login")
    suspend fun login(@Body req: LoginRequest): Response<LoginResponse>

    @POST("/api/auth/register")
    suspend fun register(@Body req: RegisterRequest): Response<RegisterResponse>

    // Se reutiliza LoginResponse porque la estructura JSON de respuesta es idéntica
    // (success, message, token, user), aunque conceptualmente sea una verificación.
    @POST("/api/auth/verify")
    suspend fun verify(@Body req: VerifyRequest): Response<LoginResponse>
}

object AuthApiImpl {
    /**
     * IMPORTANTE CONFIGURACIÓN DE RED:
     * - Si usas el Emulador de Android Studio: "http://10.0.2.2:3000/"
     * - Si usas un celular físico por USB/Wifi: Usa la IP local de tu PC (ej. "http://192.168.1.50:3000/")
     * - Nunca uses "localhost" aquí, porque para el celular, "localhost" es él mismo, no tu PC.
     */
    private const val BASE_URL = "http://10.0.2.2:3000/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        // Muestra todo el contenido del JSON en el Logcat.
        // Nota: Cuidado en producción, esto mostrará contraseñas en los logs.
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // 'by lazy' asegura que la instancia se cree solo la primera vez que se llame, ahorrando recursos.
    val service: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }
}