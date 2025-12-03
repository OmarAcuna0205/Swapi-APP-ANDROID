package com.swapi.swapiV1.login.model.repository

import com.swapi.swapiV1.login.model.dto.*
import com.swapi.swapiV1.login.model.network.AuthApi
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException

class AuthRepository(private val api: AuthApi) {

    // ---------------- LOGIN ----------------
    // Nota: Mantenemos el retorno directo de LoginResponse por compatibilidad con tu código actual,
    // pero idealmente debería retornar Result<LoginResponse> como en el registro.
    suspend fun login(email: String, password: String): LoginResponse {
        return try {
            val response = api.login(LoginRequest(email, password))

            if (response.isSuccessful) {
                // Si el servidor responde 200 OK pero el cuerpo está vacío, devolvemos error
                response.body() ?: LoginResponse(false, "ERROR_SERVIDOR_VACIO")
            } else {
                // Extraemos el mensaje de error del JSON del servidor
                val errorMsg = parseErrorBody(response)
                LoginResponse(success = false, message = errorMsg.ifBlank { "LOGIN_CREDENCIALES_INVALIDAS" })
            }
        } catch (e: IOException) {
            // Error de conexión (sin internet, timeout)
            LoginResponse(false, "ERROR_RED")
        } catch (e: Exception) {
            // Error de código (parseo, nulos, etc.)
            LoginResponse(false, "ERROR_GENERICO")
        }
    }

    // ---------------- REGISTER ----------------
    suspend fun register(request: RegisterRequest): Result<RegisterResponse> {
        return try {
            val response = api.register(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = parseErrorBody(response)
                Result.failure(Exception(errorMsg.ifBlank { "ERROR_REGISTRO_GENERICO" }))
            }
        } catch (e: IOException) {
            Result.failure(Exception("ERROR_RED"))
        } catch (e: Exception) {
            Result.failure(Exception("ERROR_GENERICO"))
        }
    }

    // ---------------- VERIFICAR CÓDIGO ----------------
    suspend fun verifyCode(email: String, code: String): Result<Boolean> {
        return try {
            val response = api.verify(VerifyRequest(email, code))

            if (response.isSuccessful) {
                // Si el código es correcto, el backend devuelve 200 OK
                Result.success(true)
            } else {
                val errorMsg = parseErrorBody(response)
                Result.failure(Exception(errorMsg.ifBlank { "VERIFICACION_CODIGO_INVALIDO" }))
            }
        } catch (e: IOException) {
            Result.failure(Exception("ERROR_RED"))
        } catch (e: Exception) {
            Result.failure(Exception("ERROR_GENERICO"))
        }
    }

    // ---------------- HELPER PRIVADO ----------------
    /**
     * Extrae el mensaje "message" del JSON de error que devuelve el backend.
     * Retrofit no convierte automáticamente los cuerpos de error, hay que hacerlo manual.
     */
    private fun parseErrorBody(response: Response<*>): String {
        return try {
            val errorJson = response.errorBody()?.string() ?: return ""
            JSONObject(errorJson).optString("message", "")
        } catch (e: Exception) {
            ""
        }
    }
}