package com.swapi.swapiV1.login.model.network

import com.swapi.swapiV1.login.model.dto.LoginRequest
import com.swapi.swapiV1.login.model.dto.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Interfaz de Retrofit que define los endpoints relacionados con la autenticación.
 * Retrofit usará esta definición para generar el código de red necesario.
 */
interface AuthApi {

    /**
     * Define una petición HTTP POST al endpoint "/api/auth/login".
     * - Es una función 'suspend' para integrarse con Coroutines.
     * - La anotación @Body convierte el objeto 'req' a JSON para enviarlo en el cuerpo de la petición.
     * - Retorna un objeto 'Response' que contiene el resultado completo de la llamada (código, cuerpo, etc.).
     */
    @POST("/api/auth/login")
    suspend fun login(@Body req: LoginRequest): Response<LoginResponse>
}

