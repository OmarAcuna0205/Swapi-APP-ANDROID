package com.swapi.androidClassMp1.login.model.dto

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
    val id: String,
    val name: String,
    val email: String
)
