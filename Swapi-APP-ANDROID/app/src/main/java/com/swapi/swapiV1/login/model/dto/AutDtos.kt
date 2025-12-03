package com.swapi.swapiV1.login.model.dto

// --- SECCIÓN DE LOGIN ---

/**
 * Estructura para enviar las credenciales al servidor.
 */
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * Respuesta del servidor tras intentar loguearse.
 * Nota: 'token' y 'user' son nulos si el login falla (success = false).
 */
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null,
    val user: User? = null
)

// --- SECCIÓN DE USUARIO ---

/**
 * Representación del usuario en la aplicación.
 */
data class User(
    // Se usa '_id' en lugar de 'id' para mapear automáticamente con el identificador único de MongoDB.
    val _id: String,
    val firstName: String,
    val paternalSurname: String,
    val email: String,
    // Valor por defecto 'student' para evitar nulos si el backend no envía este campo.
    val role: String? = "student"
)

// --- SECCIÓN DE REGISTRO Y VERIFICACIÓN ---

data class RegisterRequest(
    val email: String,
    val firstName: String,
    val paternalSurname: String,
    val maternalSurname: String,
    val password: String,
    val age: Int,
    val gender: String,
    val phone: String
)

data class RegisterResponse(
    // Es nullable (?) por seguridad, en caso de que el backend cambie el formato de respuesta o no envíe el booleano explícitamente.
    val success: Boolean?,
    val message: String,
    val user: User?
)

/**
 * Usado para enviar el código de verificación (OTP) que llega por correo.
 */
data class VerifyRequest(
    val email: String,
    val code: String
)