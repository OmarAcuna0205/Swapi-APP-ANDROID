package com.swapi.androidClassMp1.login.model.repository

import com.swapi.androidClassMp1.login.model.dto.LoginRequest
import com.swapi.androidClassMp1.login.model.dto.LoginResponse
import com.swapi.androidClassMp1.login.model.network.AuthApi
import org.json.JSONObject
import java.io.IOException

class AuthRepository(private val api: AuthApi) {

    suspend fun login(email: String, password: String): LoginResponse {
        return try {
            val resp = api.login(LoginRequest(email, password))
            if (resp.isSuccessful) {
                resp.body() ?: LoginResponse(false, "Respuesta vacía del servidor")
            } else {
                val msg = resp.errorBody()?.string().orEmpty()
                val parsed = try { JSONObject(msg).optString("message", "") } catch (_: Exception) { "" }
                LoginResponse(false, parsed.ifBlank { "Credenciales inválidas" })
            }
        } catch (_: IOException) {
            LoginResponse(false, "Sin conexión. Verifica tu red.")
        } catch (_: Exception) {
            LoginResponse(false, "Error inesperado")
        }
    }
}
