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
import com.swapi.swapiV1.utils.ErrorMessageMapper // IMPORTANTE: Agregado
import com.swapi.swapiV1.utils.dismissKeyboardOnClick

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpProfileView(
    navHostController: NavHostController,
    viewModel: LoginViewModel, // Inyectamos el ViewModel
    email: String
) {
    // Estados locales para los campos de texto
    var name by remember { mutableStateOf("") }
    var paternal by remember { mutableStateOf("") }
    var maternal by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") } // Nota: confirmPassword no se está usando en la lógica actual del VM, pero visualmente está ahí.

    var passwordVisible by remember { mutableStateOf(false) }

    val uiState by viewModel.ui.collectAsState() // Observamos estado de carga
    val context = LocalContext.current
    val swapiBrandColor = Color(0xFF4A8BFF)

    // Escuchar eventos de navegación
    LaunchedEffect(Unit) {
        viewModel.navEvents.collect { event ->
            if (event is LoginViewModel.LoginNavEvent.GoVerifyCode) {
                // Si el registro fue exitoso, vamos a verificar código
                navHostController.navigate(ScreenNavigation.SignUpCode.createRoute(email))
            }
        }
    }

    // Escuchar eventos de toast (Errores)
    LaunchedEffect(Unit) {
        viewModel.toastEvents.collect { msgCode ->
            // CORRECCIÓN: Usamos el Mapper para traducir el código de error
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

            // --- NOMBRE ---
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
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = swapiBrandColor,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
            )

            // --- APELLIDO PATERNO ---
            OutlinedTextField(
                value = paternal,
                onValueChange = {
                    paternal = it
                    viewModel.onRegisterPaternalChange(it)
                },
                // CORRECCIÓN: Usamos stringResource
                label = { Text(stringResource(R.string.signup_lastname_paternal)) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = swapiBrandColor,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
            )

            // --- APELLIDO MATERNO ---
            OutlinedTextField(
                value = maternal,
                onValueChange = {
                    maternal = it
                    viewModel.onRegisterMaternalChange(it)
                },
                // CORRECCIÓN: Usamos stringResource
                label = { Text(stringResource(R.string.signup_lastname_maternal)) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = swapiBrandColor,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
            )

            // --- CELULAR ---
            OutlinedTextField(
                value = phone,
                onValueChange = {
                    phone = it
                    viewModel.onRegisterPhoneChange(it)
                },
                label = { Text(stringResource(id = R.string.signup_profile_phone_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = swapiBrandColor,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
            )

            // --- CONTRASEÑA ---
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
                        Icon(icon, contentDescription = null)
                    }
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = swapiBrandColor,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
            )

            Spacer(Modifier.height(12.dp))

            // --- BOTÓN REGISTRARME ---
            Button(
                onClick = {
                    // Llamamos a la función del ViewModel que hace la petición al backend
                    viewModel.onRegisterUser()
                },
                enabled = !uiState.isLoading, // Deshabilitar si está cargando
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = swapiBrandColor)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        stringResource(id = R.string.signup_profile_button),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 17.sp),
                        color = Color.White
                    )
                }
            }
        }

        IconButton(
            onClick = { navHostController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 48.dp, start = 16.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(id = R.string.common_back_button_cd),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}