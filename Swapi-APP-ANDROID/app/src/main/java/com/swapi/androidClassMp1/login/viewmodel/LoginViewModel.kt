package com.swapi.androidClassMp1.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapi.androidClassMp1.login.model.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repo: AuthRepository) : ViewModel() {

    private val _ui = MutableStateFlow(LoginUiState())
    val ui: StateFlow<LoginUiState> = _ui

    private val _toastEvents = Channel<String>(Channel.BUFFERED)
    val toastEvents = _toastEvents.receiveAsFlow()

    sealed interface LoginNavEvent { data object GoHome : LoginNavEvent }
    private val _navEvents = Channel<LoginNavEvent>(Channel.BUFFERED)
    val navEvents = _navEvents.receiveAsFlow()

    fun onEmailChange(v: String) { _ui.value = _ui.value.copy(email = v) }
    fun onPasswordChange(v: String) { _ui.value = _ui.value.copy(password = v) }

    fun login() {
        val email = _ui.value.email.trim()
        val password = _ui.value.password
        if (email.isBlank() || password.isBlank()) {
            viewModelScope.launch { _toastEvents.send("Email y password son obligatorios") }
            return
        }

        _ui.value = _ui.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val res = repo.login(email, password)
                if (res.success) {
                    _toastEvents.send("Login exitoso. Bienvenido ${res.user?.name ?: ""}")
                    _navEvents.send(LoginNavEvent.GoHome)
                } else {
                    _toastEvents.send(res.message.ifBlank { "Login fallido" })
                }
            } catch (e: Exception) {
                _toastEvents.send("Error de red/servidor")
            } finally {
                _ui.value = _ui.value.copy(isLoading = false)
            }
        }
    }
}


