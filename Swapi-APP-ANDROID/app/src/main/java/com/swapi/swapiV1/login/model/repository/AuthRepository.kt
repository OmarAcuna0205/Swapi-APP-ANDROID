package com.swapi.swapiV1.login.model.repository

import com.swapi.swapiV1.login.model.dto.*
import com.swapi.swapiV1.login.model.network.AuthApi
import org.json.JSONObject
import java.io.IOException

// Repositorio que maneja la lógica de autenticación.
class AuthRepository(private val api: AuthApi) {

    // ---------------- LOGIN ----------------
    suspend fun login(email: String, password: String): LoginResponse {
        return try {
            val resp = api.login(LoginRequest(email, password))

            if (resp.isSuccessful) {
                resp.body() ?: LoginResponse(false, "Respuesta vacía del servidor")
            } else {
                val msg = resp.errorBody()?.string().orEmpty()
                val parsed = try {
                    JSONObject(msg).optString("message", "")
                } catch (_: Exception) {
                    ""
                }

                LoginResponse(
                    success = false,
                    message = parsed.ifBlank { "Credenciales inválidas" }
                )
            }
        } catch (_: IOException) {
            LoginResponse(false, "Sin conexión. Verifica tu red.")
        } catch (_: Exception) {
            LoginResponse(false, "Error inesperado")
        }
    }

    // ---------------- REGISTER ----------------
    suspend fun register(request: RegisterRequest): Result<RegisterResponse> {
        return try {
            val resp = api.register(request)

            if (resp.isSuccessful && resp.body() != null) {
                Result.success(resp.body()!!)
            } else {
                val errorMsg = resp.errorBody()?.string().orEmpty()
                val parsed = try {
                    JSONObject(errorMsg).optString("message", "")
                } catch (_: Exception) {
                    ""
                }

                Result.failure(
                    Exception(parsed.ifBlank { "Error en registro" })
                )
            }
        } catch (_: IOException) {
            Result.failure(Exception("Sin conexión. Verifica tu red."))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado"))
        }
    }

    // ---------------- VERIFICAR CÓDIGO ----------------
    suspend fun verifyCode(email: String, code: String): Result<Boolean> {
        return try {
            val request = VerifyRequest(email, code)
            val resp = api.verify(request)

            if (resp.isSuccessful) {
                Result.success(true)
            } else {
                val errorMsg = resp.errorBody()?.string().orEmpty()
                val parsed = try {
                    JSONObject(errorMsg).optString("message", "")
                } catch (_: Exception) {
                    ""
                }

                Result.failure(
                    Exception(parsed.ifBlank { "Código incorrecto" })
                )
            }
        } catch (_: IOException) {
            Result.failure(Exception("Sin conexión. Verifica tu red."))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado"))
        }
    }
}
