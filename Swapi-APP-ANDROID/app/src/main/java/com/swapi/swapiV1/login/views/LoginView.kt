package com.swapi.swapiV1.login.views

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.swapi.swapiV1.R
import com.swapi.swapiV1.login.model.network.RetrofitProvider
import com.swapi.swapiV1.login.model.repository.AuthRepository
import com.swapi.swapiV1.login.viewmodel.LoginViewModel
import com.swapi.swapiV1.login.viewmodel.LoginViewModelFactory
import com.swapi.swapiV1.utils.datastore.DataStoreManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun LoginView(
    navHostController: NavHostController,
    dataStore: DataStoreManager
) {
    val context = LocalContext.current.applicationContext
    val repo = remember { AuthRepository(RetrofitProvider.authApi) }
    val vm: LoginViewModel = viewModel(factory = LoginViewModelFactory(repo))
    val ui by vm.ui.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun showToastSafe(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(vm) {
        vm.toastEvents.collectLatest { msg ->
            showToastSafe(msg) // 👉 Solo toast de errores
        }
    }

    // --- BLOQUE MODIFICADO ---
    // Este LaunchedEffect ahora recibe el evento `GoHome` con el nombre
    // y lo guarda en el DataStore.
    LaunchedEffect(Unit) {
        vm.navEvents.collectLatest { event ->
            when (event) {
                is LoginViewModel.LoginNavEvent.GoHome -> {
                    scope.launch {
                        dataStore.setLoggedIn(true)
                        // Aquí guardamos el nombre que viene del ViewModel
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
    // ------------------------

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.swapi),
                contentDescription = stringResource(id = R.string.login_logo_cd),
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = ui.email,
                onValueChange = vm::onEmailChange,
                label = { Text(stringResource(id = R.string.login_email_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = ui.password,
                onValueChange = vm::onPasswordChange,
                label = { Text(stringResource(id = R.string.login_password_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default,
                keyboardActions = KeyboardActions(onDone = { vm.login() }),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon =
                        if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            icon,
                            contentDescription = stringResource(id = R.string.login_toggle_password_cd)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { vm.login() },
                enabled = !ui.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (ui.isLoading) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(id = R.string.login_signing_in))
                } else Text(stringResource(id = R.string.login_button_text))
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = { /* Face ID placeholder */ },
                enabled = !ui.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.Face,
                    contentDescription = stringResource(id = R.string.login_face_id_cd),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(stringResource(id = R.string.login_face_id_button))
            }

            Spacer(Modifier.height(16.dp))

            TextButton(onClick = {
                navHostController.navigate("signup")
            }) {
                Text(stringResource(id = R.string.login_signup_prompt))
            }
        }
    }
}


