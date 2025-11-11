package com.swapi.swapiV1.login.views

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.swapi.swapiV1.R
import com.swapi.swapiV1.login.model.network.RetrofitProvider
import com.swapi.swapiV1.login.model.repository.AuthRepository
import com.swapi.swapiV1.login.viewmodel.LoginViewModel
import com.swapi.swapiV1.login.viewmodel.LoginViewModelFactory
import com.swapi.swapiV1.navigation.ScreenNavigation
import com.swapi.swapiV1.utils.datastore.DataStoreManager
import com.swapi.swapiV1.utils.dismissKeyboardOnClick // ✨ --- ¡IMPORT NUEVO! --- ✨
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginView(
    navHostController: NavHostController,
    dataStore: DataStoreManager
) {
    // --- LÓGICA ---
    val context = LocalContext.current.applicationContext
    val repo = remember { AuthRepository(RetrofitProvider.authApi) }
    val vm: LoginViewModel = viewModel(factory = LoginViewModelFactory(repo))
    val ui by vm.ui.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) } // <-- ¡Línea corregida!
    val scope = rememberCoroutineScope()

    fun showToastSafe(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(vm) {
        vm.toastEvents.collectLatest { msg -> showToastSafe(msg) }
    }

    LaunchedEffect(Unit) {
        vm.navEvents.collectLatest { event ->
            when (event) {
                is LoginViewModel.LoginNavEvent.GoHome -> {
                    scope.launch {
                        dataStore.setLoggedIn(true)
                        dataStore.setUserName(event.userName ?: "Usuario")
                    }
                    navHostController.navigate("tabbar") {
                        popUpTo("login") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }
    }

    // --- INTERFAZ "ASTETIK" V3 ---
    val swapiBrandColor = Color(0xFF4A8BFF)
    val elegantGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.background
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(elegantGradient)
            .dismissKeyboardOnClick(), // ✨ --- ¡MODIFIER APLICADO AQUÍ! --- ✨
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(horizontal = 28.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // --- LOGO ---
            Image(
                painter = painterResource(id = R.drawable.swapi),
                contentDescription = "Logo Swapi",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )

            // --- TÍTULOS ---
            Text(
                text = "Bienvenido a Swapi",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = "Inicia sesión para acceder a tu comunidad",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    fontSize = 15.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            // --- EMAIL ---
            OutlinedTextField(
                value = ui.email,
                onValueChange = vm::onEmailChange,
                label = { Text("Correo institucional") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = swapiBrandColor,
                    cursorColor = swapiBrandColor,
                    focusedLabelColor = swapiBrandColor,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            )

            // --- CONTRASEÑA ---
            OutlinedTextField(
                value = ui.password,
                onValueChange = vm::onPasswordChange,
                label = { Text("Contraseña") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon =
                        if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            icon,
                            contentDescription = "Mostrar contraseña",
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = swapiBrandColor,
                    cursorColor = swapiBrandColor,
                    focusedLabelColor = swapiBrandColor,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                ),
                keyboardActions = KeyboardActions(onDone = { vm.login() })
            )

            Spacer(Modifier.height(12.dp))

            // --- BOTÓN LOGIN ---
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
                    Text("Iniciando...")
                } else {
                    Text(
                        "Iniciar sesión",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 17.sp
                        )
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // --- LINK OLVIDASTE CONTRASEÑA ---
            TextButton(onClick = { /* TODO: Implementar */ }) {
                Text(
                    "¿Olvidaste tu contraseña?",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = swapiBrandColor.copy(alpha = 0.8f)
                    ),
                    textAlign = TextAlign.Center
                )
            }

            // --- DIVIDER "O" ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                    thickness = 1.dp
                )
                Text(
                    text = " O ",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall
                )
                Divider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                    thickness = 1.dp
                )
            }

            // --- BOTÓN SIGNUP ---
            OutlinedButton(
                onClick = { navHostController.navigate(ScreenNavigation.SignUpEmail.route) }, // <-- Esto ya estaba bien
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
                    "Crear cuenta nueva",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp
                    )
                )
            }
        }
    }
}