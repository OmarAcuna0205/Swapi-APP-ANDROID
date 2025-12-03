package com.swapi.swapiV1.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapi.swapiV1.login.model.dto.RegisterRequest
import com.swapi.swapiV1.login.model.repository.AuthRepository
import com.swapi.swapiV1.utils.datastore.DataStoreManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repo: AuthRepository,
    private val dataStore: DataStoreManager
) : ViewModel() {

    // --- ESTADO DE LA UI (Persistente) ---
    // Maneja los datos que deben sobrevivir a la rotación de pantalla y redibujar la UI.
    private val _ui = MutableStateFlow(LoginUiState())
    val ui: StateFlow<LoginUiState> = _ui

    // --- EVENTOS DE UN SOLO DISPARO (Efímeros) ---
    // Usamos Channels para cosas que pasan una sola vez: Navegación y Toasts.
    // Si usáramos StateFlow aquí, el Toast se volvería a mostrar al girar la pantalla.
    private val _toastEvents = Channel<String>(Channel.BUFFERED)
    val toastEvents = _toastEvents.receiveAsFlow()

    sealed interface LoginNavEvent {
        data class GoHome(val userName: String?) : LoginNavEvent
        object GoVerifyCode : LoginNavEvent
        object GoLogin : LoginNavEvent
    }

    private val _navEvents = Channel<LoginNavEvent>(Channel.BUFFERED)
    val navEvents = _navEvents.receiveAsFlow()

    // --- VARIABLES TEMPORALES DE REGISTRO ---
    // Almacenan los datos mientras el usuario llena el formulario.
    private var registerEmail = ""
    private var registerName = ""
    private var registerPaternal = ""
    private var registerMaternal = ""
    private var registerPassword = ""
    private var registerPhone = ""
    private var registerAge = 20
    private var registerGender = "Masculino"

    // --- INPUTS (Setters) ---

    // Login
    fun onEmailChange(v: String) { _ui.value = _ui.value.copy(email = v) }
    fun onPasswordChange(v: String) { _ui.value = _ui.value.copy(password = v) }

    // Registro
    fun onRegisterEmailChange(v: String) { registerEmail = v }
    fun onRegisterNameChange(v: String) { registerName = v }
    fun onRegisterPaternalChange(v: String) { registerPaternal = v }
    fun onRegisterMaternalChange(v: String) { registerMaternal = v }
    fun onRegisterPasswordChange(v: String) { registerPassword = v }
    fun onRegisterPhoneChange(v: String) { registerPhone = v }

    // --- LÓGICA DE NEGOCIO ---

    fun login() {
        val email = _ui.value.email.trim()
        val password = _ui.value.password

        if (email.isBlank() || password.isBlank()) {
            sendToast("LOGIN_CAMPOS_OBLIGATORIOS")
            return
        }

        _ui.value = _ui.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val response = repo.login(email, password)

                if (response.success) {
                    // Guardamos sesión localmente antes de navegar
                    response.token?.let { dataStore.saveAccessToken(it) }
                    dataStore.setLoggedIn(true)
                    dataStore.setUserName(response.user?.firstName ?: "Usuario")

                    _navEvents.send(LoginNavEvent.GoHome(response.user?.firstName))
                } else {
                    sendToast(response.message.ifBlank { "Login fallido" })
                }
            } catch (e: Exception) {
                sendToast("ERROR_RED")
            } finally {
                // El bloque finally asegura que el spinner de carga se quite
                // tanto si hubo éxito como si hubo error.
                _ui.value = _ui.value.copy(isLoading = false)
            }
        }
    }

    fun onRegisterUser() {
        if (registerEmail.isBlank() || registerPassword.isBlank() || registerName.isBlank()) {
            sendToast("REGISTRO_CAMPOS_OBLIGATORIOS")
            return
        }

        _ui.value = _ui.value.copy(isLoading = true)

        viewModelScope.launch {
            val request = RegisterRequest(
                email = registerEmail, firstName = registerName, paternalSurname = registerPaternal,
                maternalSurname = registerMaternal, password = registerPassword, age = registerAge,
                gender = registerGender, phone = registerPhone
            )

            val result = repo.register(request)

            result.onSuccess {
                _navEvents.send(LoginNavEvent.GoVerifyCode)
            }.onFailure { error ->
                sendToast(error.message ?: "ERROR_REGISTRO")
            }

            _ui.value = _ui.value.copy(isLoading = false)
        }
    }

    fun onVerifyCode(code: String) {
        if (code.isBlank()) {
            sendToast("VERIFICACION_CODIGO_VACIO")
            return
        }

        _ui.value = _ui.value.copy(isLoading = true)

        viewModelScope.launch {
            val result = repo.verifyCode(registerEmail, code)

            result.onSuccess {
                _navEvents.send(LoginNavEvent.GoLogin)
            }.onFailure {
                sendToast("VERIFICACION_CODIGO_INVALIDO")
            }

            _ui.value = _ui.value.copy(isLoading = false)
        }
    }

    // Helper privado para enviar eventos de toast más limpio
    private fun sendToast(message: String) {
        viewModelScope.launch { _toastEvents.send(message) }
    }
}