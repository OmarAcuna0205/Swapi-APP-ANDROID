package com.swapi.swapiV1.login.model.repository

import com.swapi.swapiV1.login.model.dto.LoginRequest
import com.swapi.swapiV1.login.model.dto.LoginResponse
import com.swapi.swapiV1.login.model.network.AuthApi
import org.json.JSONObject
import java.io.IOException

// Repositorio que maneja la lógica de autenticación.
class AuthRepository(private val api: AuthApi) {

    // Función 'suspend' para realizar la llamada de red sin bloquear el hilo UI.
    suspend fun login(email: String, password: String): LoginResponse {
        return try {
            // Realiza la llamada de red.
            val resp = api.login(LoginRequest(email, password))

            if (resp.isSuccessful) {
                // Caso de éxito (HTTP 2xx): devuelve el cuerpo o un error si está vacío.
                resp.body() ?: LoginResponse(false, "Respuesta vacía del servidor")
            } else {
                // Caso de error (HTTP 4xx, 5xx): intenta parsear el mensaje de error.
                val msg = resp.errorBody()?.string().orEmpty()
                val parsed = try { JSONObject(msg).optString("message", "") } catch (_: Exception) { "" }
                LoginResponse(false, parsed.ifBlank { "Credenciales inválidas" })
            }
        } catch (_: IOException) {
            // Error de conexión (sin internet).
            LoginResponse(false, "Sin conexión. Verifica tu red.")
        } catch (_: Exception) {
            // Cualquier otro error inesperado.
            LoginResponse(false, "Error inesperado")
        }
    }
}
