package com.swapi.swapiV1.login.model.network

import com.swapi.swapiV1.login.model.dto.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("api/auth/login")
    suspend fun login(@Body req: LoginRequest): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(@Body req: RegisterRequest): Response<RegisterResponse>

    @POST("api/auth/verify")
    suspend fun verify(@Body req: VerifyRequest): Response<LoginResponse>
}