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
fun SignUpEmailView(
    navHostController: NavHostController,
    viewModel: LoginViewModel
) {
    // Estado local para control inmediato del texto
    var email by remember { mutableStateOf("") }

    val context = LocalContext.current
    val swapiBrandColor = Color(0xFF4A8BFF)

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
                text = stringResource(id = R.string.signup_email_title),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(id = R.string.signup_email_subtitle),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                ),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { newValue ->
                    email = newValue
                    // Sincronizamos con el ViewModel en tiempo real
                    viewModel.onRegisterEmailChange(newValue)
                },
                label = { Text(stringResource(id = R.string.login_email_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = swapiBrandColor,
                    cursorColor = swapiBrandColor,
                    focusedLabelColor = swapiBrandColor,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    // Validacion de dominio institucional
                    val cleanEmail = email.trim()

                    if (cleanEmail.endsWith("@ulsachihuahua.edu.mx")) {
                        navHostController.navigate(ScreenNavigation.SignUpProfile.createRoute(cleanEmail))
                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.signup_error_domain),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = swapiBrandColor)
            ) {
                Text(
                    stringResource(id = R.string.common_continue),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp
                    ),
                    color = Color.White
                )
            }
        }

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