package com.swapi.swapiV1.login.viewmodel

/**
 * Representa una "foto" exacta de cómo se ve la pantalla de Login en un momento dado.
 * En Jetpack Compose, la UI se redibuja cada vez que una propiedad de esta clase cambia.
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    // Campo añadido: necesario para guardar el mensaje de error si el login falla.
    // Es nullable (?) porque al inicio no hay ningún error.
    val errorMessage: String? = null
)