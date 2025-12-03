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
import com.swapi.swapiV1.utils.ErrorMessageMapper
import com.swapi.swapiV1.utils.dismissKeyboardOnClick
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpCodeView(
    navHostController: NavHostController,
    // Recibimos el ViewModel como parámetro. Esto es crucial:
    // Significa que esta pantalla comparte el mismo ViewModel (y por tanto los mismos datos)
    // que la pantalla anterior, o que el grafo de navegación se encarga de proveerlo.
    viewModel: LoginViewModel,
    email: String
) {
    // Estado local para el código (lo que el usuario escribe).
    // No hace falta ponerlo en el ViewModel hasta que se envíe, porque es efímero.
    var code by remember { mutableStateOf("") }

    // Observamos el estado global (isLoading, etc.)
    val uiState by viewModel.ui.collectAsState()

    val context = LocalContext.current
    val swapiBrandColor = Color(0xFF4A8BFF)

    // 1. ESCUCHA DE NAVEGACIÓN (Éxito)
    LaunchedEffect(Unit) {
        // Usamos collectLatest para asegurar que siempre atendemos al evento más reciente
        viewModel.navEvents.collectLatest { event ->
            if (event is LoginViewModel.LoginNavEvent.GoLogin) {
                Toast.makeText(
                    context,
                    context.getString(R.string.signup_success_verify),
                    Toast.LENGTH_LONG
                ).show()

                // Navegamos al Login y borramos la pila para que no pueda volver atrás
                navHostController.navigate(ScreenNavigation.Login.route) {
                    popUpTo(ScreenNavigation.Login.route) { inclusive = true }
                }
            }
        }
    }

    // 2. ESCUCHA DE ERRORES
    LaunchedEffect(Unit) {
        viewModel.toastEvents.collectLatest { msgCode ->
            // El Mapper traduce códigos como "VERIFICACION_CODIGO_INVALIDO" a mensajes legibles
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Título
            Text(
                text = stringResource(id = R.string.signup_code_title),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )

            // Subtítulo mostrando el correo al que se envió el código
            Text(
                text = stringResource(id = R.string.signup_code_subtitle, email),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                ),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            // Campo de entrada del código
            OutlinedTextField(
                value = code,
                onValueChange = { newValue ->
                    // Validación simple: Solo permitimos escribir si son 6 caracteres o menos
                    if (newValue.length <= 6) code = newValue
                },
                label = { Text(stringResource(id = R.string.signup_code_label)) },
                singleLine = true,
                // Configuramos el teclado para que sea numérico
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                // Sintaxis actualizada de Material 3
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = swapiBrandColor,
                    focusedLabelColor = swapiBrandColor,
                    cursorColor = swapiBrandColor,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
            )

            Spacer(Modifier.height(12.dp))

            // Botón de Verificar
            Button(
                onClick = {
                    viewModel.onVerifyCode(code)
                },
                // Deshabilitamos el botón si ya está cargando para evitar doble clic
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
                        stringResource(id = R.string.signup_code_button),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 17.sp
                        ),
                        color = Color.White
                    )
                }
            }
        }

        // Botón Atrás (Flecha superior izquierda)
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