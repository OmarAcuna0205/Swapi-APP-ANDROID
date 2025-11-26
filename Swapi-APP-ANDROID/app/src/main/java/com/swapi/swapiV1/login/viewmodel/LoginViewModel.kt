package com.swapi.swapiV1.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapi.swapiV1.login.model.dto.RegisterRequest
import com.swapi.swapiV1.login.model.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repo: AuthRepository) : ViewModel() {

    // ---------------- UI STATE ----------------
    private val _ui = MutableStateFlow(LoginUiState())
    val ui: StateFlow<LoginUiState> = _ui

    // ---------------- TOAST EVENTS ----------------
    private val _toastEvents = Channel<String>(Channel.BUFFERED)
    val toastEvents = _toastEvents.receiveAsFlow()

    // ---------------- NAV EVENTS ----------------
    sealed interface LoginNavEvent {
        data class GoHome(val userName: String?) : LoginNavEvent
        object GoVerifyCode : LoginNavEvent
        object GoLogin : LoginNavEvent
    }

    private val _navEvents = Channel<LoginNavEvent>(Channel.BUFFERED)
    val navEvents = _navEvents.receiveAsFlow()

    // ---------------- LOGIN INPUTS ----------------
    fun onEmailChange(v: String) {
        _ui.value = _ui.value.copy(email = v)
    }

    fun onPasswordChange(v: String) {
        _ui.value = _ui.value.copy(password = v)
    }

    // ---------------- REGISTRO (ESTADO TEMPORAL) ----------------
    private var registerEmail = ""
    private var registerName = ""
    private var registerPaternal = ""
    private var registerMaternal = ""
    private var registerPassword = ""
    private var registerPhone = ""
    private var registerAge = 20
    private var registerGender = "Masculino"

    fun onRegisterEmailChange(v: String) { registerEmail = v }
    fun onRegisterNameChange(v: String) { registerName = v }
    fun onRegisterPaternalChange(v: String) { registerPaternal = v }
    fun onRegisterMaternalChange(v: String) { registerMaternal = v }
    fun onRegisterPasswordChange(v: String) { registerPassword = v }
    fun onRegisterPhoneChange(v: String) { registerPhone = v }

    // ---------------- LOGIN ----------------
    fun login() {
        val email = _ui.value.email.trim()
        val password = _ui.value.password

        if (email.isBlank() || password.isBlank()) {
            viewModelScope.launch {
                _toastEvents.send("Email y password son obligatorios")
            }
            return
        }

        _ui.value = _ui.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val res = repo.login(email, password)

                if (res.success) {
                    _navEvents.send(
                        LoginNavEvent.GoHome(res.user?.firstName)
                    )
                } else {
                    _toastEvents.send(
                        res.message.ifBlank { "Login fallido" }
                    )
                }
            } catch (_: Exception) {
                _toastEvents.send("Error de red o servidor")
            } finally {
                _ui.value = _ui.value.copy(isLoading = false)
            }
        }
    }

    // ---------------- REGISTRAR USUARIO ----------------
    fun onRegisterUser() {
        if (
            registerEmail.isBlank() ||
            registerPassword.isBlank() ||
            registerName.isBlank()
        ) {
            viewModelScope.launch {
                _toastEvents.send("Completa todos los campos obligatorios")
            }
            return
        }

        _ui.value = _ui.value.copy(isLoading = true)

        viewModelScope.launch {
            val request = RegisterRequest(
                email = registerEmail,
                firstName = registerName,
                paternalSurname = registerPaternal,
                maternalSurname = registerMaternal,
                password = registerPassword,
                age = registerAge,
                gender = registerGender,
                phone = registerPhone
            )

            val result = repo.register(request)

            result
                .onSuccess {
                    _navEvents.send(LoginNavEvent.GoVerifyCode)
                }
                .onFailure {
                    _toastEvents.send(it.message ?: "Error en registro")
                }

            _ui.value = _ui.value.copy(isLoading = false)
        }
    }

    // ---------------- VERIFICAR CÓDIGO ----------------
    fun onVerifyCode(code: String) {
        if (code.isBlank()) {
            viewModelScope.launch {
                _toastEvents.send("Ingresa el código")
            }
            return
        }

        _ui.value = _ui.value.copy(isLoading = true)

        viewModelScope.launch {
            val result = repo.verifyCode(registerEmail, code)

            result
                .onSuccess {
                    _navEvents.send(LoginNavEvent.GoLogin)
                }
                .onFailure {
                    _toastEvents.send("Código inválido")
                }

            _ui.value = _ui.value.copy(isLoading = false)
        }
    }
}
