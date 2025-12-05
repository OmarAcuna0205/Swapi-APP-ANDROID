package com.swapi.swapiV1.login.views

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.swapi.swapiV1.R
// import com.swapi.swapiV1.login.model.network.RetrofitProvider <-- Ya no se necesita aquí
import com.swapi.swapiV1.login.model.repository.AuthRepository
import com.swapi.swapiV1.login.viewmodel.LoginViewModel
import com.swapi.swapiV1.login.viewmodel.LoginViewModelFactory
import com.swapi.swapiV1.navigation.ScreenNavigation
import com.swapi.swapiV1.utils.ErrorMessageMapper
import com.swapi.swapiV1.utils.datastore.DataStoreManager
import com.swapi.swapiV1.utils.dismissKeyboardOnClick
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginView(
    navHostController: NavHostController,
    dataStore: DataStoreManager
) {
    val context = LocalContext.current

    // CORREGIDO: AuthRepository() ya no recibe parámetros.
    // Él solito obtiene la API desde RetrofitProvider internamente.
    val repo = remember { AuthRepository() }

    val vm: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(repo, dataStore)
    )

    // Estado de la UI que viene del ViewModel
    val ui by vm.ui.collectAsState()

    // Estado local de la vista (solo afecta visualmente al campo de contraseña)
    var passwordVisible by remember { mutableStateOf(false) }

    // Color de marca local
    val swapiBrandColor = Color(0xFF4A8BFF)

    // 1. ESCUCHA DE MENSAJES (TOASTS)
    LaunchedEffect(vm) {
        vm.toastEvents.collectLatest { msgCode ->
            val message = ErrorMessageMapper.getMessage(context, msgCode)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // 2. ESCUCHA DE NAVEGACIÓN
    LaunchedEffect(Unit) {
        vm.navEvents.collectLatest { event ->
            when (event) {
                is LoginViewModel.LoginNavEvent.GoHome -> {
                    navHostController.navigate("tabbar") {
                        popUpTo(ScreenNavigation.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
                is LoginViewModel.LoginNavEvent.GoVerifyCode -> {
                    // Navegación futura
                }
                else -> {}
            }
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
            // LOGO
            Image(
                painter = painterResource(id = R.drawable.swapi),
                contentDescription = stringResource(id = R.string.login_logo_cd),
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )

            // TÍTULOS
            Text(
                text = stringResource(id = R.string.login_title),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = stringResource(id = R.string.login_subtitle),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    fontSize = 15.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            // CAMPO EMAIL
            OutlinedTextField(
                value = ui.email,
                onValueChange = vm::onEmailChange,
                label = { Text(stringResource(id = R.string.login_email_label)) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = swapiBrandColor,
                    focusedLabelColor = swapiBrandColor,
                    cursorColor = swapiBrandColor,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            )

            // CAMPO PASSWORD
            OutlinedTextField(
                value = ui.password,
                onValueChange = vm::onPasswordChange,
                label = { Text(stringResource(id = R.string.login_password_label)) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
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
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = swapiBrandColor,
                    focusedLabelColor = swapiBrandColor,
                    cursorColor = swapiBrandColor,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                ),
                keyboardActions = KeyboardActions(onDone = { vm.login() })
            )

            Spacer(Modifier.height(12.dp))

            // BOTÓN LOGIN
            Button(
                onClick = { vm.login() },
                enabled = !ui.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = swapiBrandColor,
                    contentColor = Color.White
                )
            ) {
                if (ui.isLoading) {
                    CircularProgressIndicator(
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(stringResource(id = R.string.login_signing_in))
                } else {
                    Text(
                        stringResource(id = R.string.login_button_text),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 17.sp
                        )
                    )
                }
            }

            // DIVISOR OR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                    thickness = 1.dp
                )
                Text(
                    text = stringResource(id = R.string.login_divider_or),
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                    thickness = 1.dp
                )
            }

            // BOTÓN CREAR CUENTA
            OutlinedButton(
                onClick = { navHostController.navigate(ScreenNavigation.SignUpEmail.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.secondary
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = SolidColor(MaterialTheme.colorScheme.secondary)
                )
            ) {
                Text(
                    stringResource(id = R.string.login_create_account),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp
                    )
                )
            }
        }
    }
}