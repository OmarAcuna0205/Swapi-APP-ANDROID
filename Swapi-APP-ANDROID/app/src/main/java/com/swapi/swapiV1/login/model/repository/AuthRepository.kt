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
                // Si el body es nulo, devolvemos código de error
                resp.body() ?: LoginResponse(false, "ERROR_SERVIDOR_VACIO")
            } else {
                val msg = resp.errorBody()?.string().orEmpty()
                val parsed = try {
                    JSONObject(msg).optString("message", "")
                } catch (_: Exception) {
                    ""
                }

                LoginResponse(
                    success = false,
                    // Si el backend manda mensaje, intentamos usarlo (o el Mapper lo devolverá tal cual si no es código),
                    // si no, usamos el código de credenciales inválidas por defecto.
                    message = parsed.ifBlank { "LOGIN_CREDENCIALES_INVALIDAS" }
                )
            }
        } catch (_: IOException) {
            LoginResponse(false, "ERROR_RED")
        } catch (_: Exception) {
            LoginResponse(false, "ERROR_GENERICO")
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
                    Exception(parsed.ifBlank { "ERROR_REGISTRO_GENERICO" })
                )
            }
        } catch (_: IOException) {
            Result.failure(Exception("ERROR_RED"))
        } catch (e: Exception) {
            Result.failure(Exception("ERROR_GENERICO"))
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
                    // "VERIFICACION_CODIGO_INVALIDO" ya lo agregamos al Mapper en el paso anterior
                    Exception(parsed.ifBlank { "VERIFICACION_CODIGO_INVALIDO" })
                )
            }
        } catch (_: IOException) {
            Result.failure(Exception("ERROR_RED"))
        } catch (e: Exception) {
            Result.failure(Exception("ERROR_GENERICO"))
        }
    }
}