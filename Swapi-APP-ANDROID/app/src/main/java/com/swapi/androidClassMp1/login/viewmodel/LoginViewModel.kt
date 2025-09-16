package com.swapi.androidClassMp1.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapi.androidClassMp1.login.model.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

// ViewModel que gestiona el estado y la lógica de la pantalla de login.
class LoginViewModel(private val repo: AuthRepository) : ViewModel() {

    // StateFlow para gestionar el ESTADO de la UI (los datos que se pintan en pantalla).
    // Es la forma moderna recomendada por Android para manejar el estado.
    private val _ui = MutableStateFlow(LoginUiState())
    val ui: StateFlow<LoginUiState> = _ui

    // Channel para gestionar EVENTOS de una sola vez, como mostrar un toast.
    // Usar un Channel evita que el toast se muestre de nuevo si la pantalla rota.
    private val _toastEvents = Channel<String>(Channel.BUFFERED)
    val toastEvents = _toastEvents.receiveAsFlow()

    // Define los posibles eventos de navegación de forma segura y explícita.
    sealed interface LoginNavEvent { data object GoHome : LoginNavEvent }
    // Channel para los eventos de navegación.
    private val _navEvents = Channel<LoginNavEvent>(Channel.BUFFERED)
    val navEvents = _navEvents.receiveAsFlow()

    // Funciones llamadas por la UI para actualizar el estado de forma inmutable.
    fun onEmailChange(v: String) { _ui.value = _ui.value.copy(email = v) }
    fun onPasswordChange(v: String) { _ui.value = _ui.value.copy(password = v) }

    // Lógica principal para realizar el login, llamada desde un botón en la UI.
    fun login() {
        val email = _ui.value.email.trim()
        val password = _ui.value.password

        // 1. Validación de campos. Si no son válidos, envía un toast y termina.
        if (email.isBlank() || password.isBlank()) {
            viewModelScope.launch { _toastEvents.send("Email y password son obligatorios") }
            return
        }

        // 2. Actualiza el estado para mostrar un indicador de carga en la UI.
        _ui.value = _ui.value.copy(isLoading = true)

        // 3. Lanza una corrutina para la operación de red, sin bloquear el hilo principal.
        viewModelScope.launch {
            try {
                // Llama al repositorio para realizar la petición de login.
                val res = repo.login(email, password)
                if (res.success) {
                    // En caso de éxito, envía eventos para mostrar un toast y navegar a Home.
                    _toastEvents.send("Login exitoso. Bienvenido ${res.user?.name ?: ""}")
                    _navEvents.send(LoginNavEvent.GoHome)
                } else {
                    // En caso de fallo, envía el mensaje de error del servidor.
                    _toastEvents.send(res.message.ifBlank { "Login fallido" })
                }
            } catch (e: Exception) {
                // Captura cualquier excepción de red o del servidor.
                _toastEvents.send("Error de red/servidor")
            } finally {
                // 4. Se ejecuta SIEMPRE (en éxito o error) para ocultar el indicador de carga.
                _ui.value = _ui.value.copy(isLoading = false)
            }
        }
    }
}