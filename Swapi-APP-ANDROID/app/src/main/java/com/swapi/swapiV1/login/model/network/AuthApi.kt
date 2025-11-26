package com.swapi.swapiV1.login.model.network

import com.swapi.swapiV1.login.model.dto.LoginRequest
import com.swapi.swapiV1.login.model.dto.LoginResponse
import com.swapi.swapiV1.login.model.dto.RegisterRequest
import com.swapi.swapiV1.login.model.dto.RegisterResponse
import com.swapi.swapiV1.login.model.dto.VerifyRequest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Interfaz de Retrofit que define los endpoints relacionados con la autenticación.
 */
interface AuthApi {

    // Login existente
    @POST("/api/auth/login")
    suspend fun login(@Body req: LoginRequest): Response<LoginResponse>

    // --- NUEVOS ENDPOINTS PARA EL REGISTRO ---

    // Paso 1 y 2: Enviar datos para crear usuario y mandar correo
    @POST("/api/auth/register")
    suspend fun register(@Body req: RegisterRequest): Response<RegisterResponse>

    // Paso 3: Verificar el código que llegó al correo
    // Nota: Usamos LoginResponse porque el backend devuelve un JSON similar ({ success, message, etc })
    @POST("/api/auth/verify")
    suspend fun verify(@Body req: VerifyRequest): Response<LoginResponse>
}

/**
 * Implementación Singleton de la API de Autenticación.
 * Esto permite crear una instancia de Retrofit específica para el Backend Local.
 */
object AuthApiImpl {
    // IMPORTANTE: Si usas emulador es 10.0.2.2. Si usas celular físico, pon la IP de tu PC (ej. 192.168.1.50)
    private const val BASE_URL = "http://10.0.2.2:3000/"

    // Interceptor para ver los logs en el Logcat (útil para debuggear)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Cliente HTTP con el interceptor
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    // Instancia de Retrofit configurada
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Servicio público listo para usarse en el Repository
    val service: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }
}