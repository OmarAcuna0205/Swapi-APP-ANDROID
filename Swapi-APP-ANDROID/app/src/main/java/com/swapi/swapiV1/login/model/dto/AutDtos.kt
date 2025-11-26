package com.swapi.swapiV1.login.model.dto

// Clases de datos (Data Transfer Objects) que modelan la información
// para la comunicación con la API durante el login.

/**
 * Modela los datos que la app envía al servidor para solicitar el inicio de sesión.
 */
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * Modela la respuesta que el servidor envía a la app tras el intento de login.
 */
data class LoginResponse(
    val success: Boolean,
    val message: String,
    // El token y el usuario son opcionales (nullable) porque solo se reciben en un login exitoso.
    val token: String? = null,
    val user: User? = null
)

/**
 * Modela la estructura de los datos del usuario.
 */
data class User(
    val _id: String,           // Mongo usa _id, no id
    val firstName: String,     // Coincide con backend
    val paternalSurname: String, // Coincide con backend
    val email: String,
    val role: String? = "student"
)

// En login/model/dto/AutDtos.kt

// 1. Lo que enviamos para registrarnos
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

// 2. La respuesta del registro (backend devuelve user y message)
data class RegisterResponse(
    val success: Boolean?, // Lo agregamos opcional por si el backend no lo manda aun
    val message: String,
    val user: User?
)

// 3. Lo que enviamos para verificar el código
data class VerifyRequest(
    val email: String,
    val code: String
)