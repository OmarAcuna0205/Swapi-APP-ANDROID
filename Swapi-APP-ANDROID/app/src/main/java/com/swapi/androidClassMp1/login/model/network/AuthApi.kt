package com.swapi.androidClassMp1.login.model.network

import com.swapi.androidClassMp1.login.model.dto.LoginRequest
import com.swapi.androidClassMp1.login.model.dto.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/api/auth/login")
    suspend fun login(@Body req: LoginRequest): Response<LoginResponse>
}


