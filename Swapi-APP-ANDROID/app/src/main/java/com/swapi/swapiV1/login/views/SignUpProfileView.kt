package com.swapi.swapiV1.login.views

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.swapi.swapiV1.R
import com.swapi.swapiV1.login.viewmodel.LoginViewModel
import com.swapi.swapiV1.navigation.ScreenNavigation
import com.swapi.swapiV1.utils.ErrorMessageMapper
import com.swapi.swapiV1.utils.dismissKeyboardOnClick
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpProfileView(
    navHostController: NavHostController,
    viewModel: LoginViewModel,
    email: String
) {
    // Variables locales para reactividad inmediata en la UI
    var name by remember { mutableStateOf("") }
    var paternal by remember { mutableStateOf("") }
    var maternal by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Control de visibilidad de contraseña
    var passwordVisible by remember { mutableStateOf(false) }

    // Estado global de carga
    val uiState by viewModel.ui.collectAsState()

    val context = LocalContext.current
    val swapiBrandColor = Color(0xFF4A8BFF)

    // 1. ESCUCHA DE NAVEGACIÓN
    LaunchedEffect(Unit) {
        viewModel.navEvents.collectLatest { event ->
            if (event is LoginViewModel.LoginNavEvent.GoVerifyCode) {
                // Si el backend responde OK al registro, pasamos a verificar el código
                navHostController.navigate(ScreenNavigation.SignUpCode.createRoute(email))
            }
        }
    }

    // 2. ESCUCHA DE ERRORES (Toasts)
    LaunchedEffect(Unit) {
        viewModel.toastEvents.collectLatest { msgCode ->
            val message = ErrorMessageMapper.getMessage(context, msgCode)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .dismissKeyboardOnClick(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = stringResource(id = R.string.signup_profile_title),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(id = R.string.signup_profile_subtitle, email),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                ),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            // Defino los colores una vez para reutilizarlos en todos los campos
            val commonTextFieldColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = swapiBrandColor,
                focusedLabelColor = swapiBrandColor,
                cursorColor = swapiBrandColor,
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            // --- CAMPO NOMBRE ---
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    viewModel.onRegisterNameChange(it)
                },
                label = { Text(stringResource(id = R.string.signup_profile_name_label)) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = commonTextFieldColors
            )

            // --- CAMPO APELLIDO PATERNO ---
            OutlinedTextField(
                value = paternal,
                onValueChange = {
                    paternal = it
                    viewModel.onRegisterPaternalChange(it)
                },
                label = { Text(stringResource(R.string.signup_lastname_paternal)) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = commonTextFieldColors
            )

            // --- CAMPO APELLIDO MATERNO ---
            OutlinedTextField(
                value = maternal,
                onValueChange = {
                    maternal = it
                    viewModel.onRegisterMaternalChange(it)
                },
                label = { Text(stringResource(R.string.signup_lastname_maternal)) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = commonTextFieldColors
            )

            // --- CAMPO CELULAR ---
            OutlinedTextField(
                value = phone,
                onValueChange = {
                    // Limitamos a 10 dígitos para evitar números inválidos visualmente
                    if (it.length <= 10) {
                        phone = it
                        viewModel.onRegisterPhoneChange(it)
                    }
                },
                label = { Text(stringResource(id = R.string.signup_profile_phone_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = commonTextFieldColors
            )

            // --- CAMPO CONTRASEÑA ---
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    viewModel.onRegisterPasswordChange(it)
                },
                label = { Text(stringResource(id = R.string.login_password_label)) },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = commonTextFieldColors
            )

            Spacer(Modifier.height(12.dp))

            // --- BOTÓN REGISTRARME ---
            Button(
                onClick = { viewModel.onRegisterUser() },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = swapiBrandColor)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 3.dp
                    )
                } else {
                    Text(
                        stringResource(id = R.string.signup_profile_button),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 17.sp
                        ),
                        color = Color.White
                    )
                }
            }
        }

        // Botón Atrás
        IconButton(
            onClick = { navHostController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 48.dp, start = 16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(id = R.string.common_back_button_cd),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}