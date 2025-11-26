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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.swapi.swapiV1.R
import com.swapi.swapiV1.login.viewmodel.LoginViewModel
import com.swapi.swapiV1.navigation.ScreenNavigation
import com.swapi.swapiV1.utils.dismissKeyboardOnClick

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpCodeView(
    navHostController: NavHostController,
    viewModel: LoginViewModel, // Inyectamos el ViewModel
    email: String
) {
    var code by remember { mutableStateOf("") }
    val uiState by viewModel.ui.collectAsState()
    val context = LocalContext.current
    val swapiBrandColor = Color(0xFF4A8BFF)

    // Escuchar éxito de verificación
    LaunchedEffect(Unit) {
        viewModel.navEvents.collect { event ->
            if (event is LoginViewModel.LoginNavEvent.GoLogin) {
                // Si verifica OK, vamos al Login para que entre
                Toast.makeText(context, "Cuenta verificada. Inicia sesión.", Toast.LENGTH_LONG).show()
                navHostController.navigate(ScreenNavigation.Login.route) {
                    popUpTo(ScreenNavigation.Login.route) { inclusive = true }
                }
            }
        }
    }

    // Escuchar errores (código incorrecto)
    LaunchedEffect(Unit) {
        viewModel.toastEvents.collect { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = stringResource(id = R.string.signup_code_title),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(id = R.string.signup_code_subtitle, email),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                ),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = code,
                onValueChange = { if (it.length <= 6) code = it },
                label = { Text(stringResource(id = R.string.signup_code_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = swapiBrandColor,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    viewModel.onVerifyCode(code) // Llamamos al backend para verificar
                },
                enabled = !uiState.isLoading,
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
                        stringResource(id = R.string.signup_code_button),
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