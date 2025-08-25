package com.swapi.androidClassMp1.ids.imc.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.swapi.androidClassMp1.ids.imc.viewmodels.IMCViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.swapi.androidClassMp1.R


@Composable
fun IMCView(viewModel: IMCViewModel = viewModel()) {
    val imcNormalMsg = stringResource(id = R.string.imc_normal)
    val imcNotNormalMsg = stringResource(id = R.string.imc_not_normal)
    val errorMsg = stringResource(id = R.string.error_invalid_input)
    val imcLabel = stringResource(id = R.string.imc_label) // Nueva etiqueta internacionalizada

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.title_imc_calculator),
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.peso.value,
            onValueChange = { viewModel.onPesoChange(it) },
            label = { Text(stringResource(id = R.string.enter_weight)) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.altura.value,
            onValueChange = { viewModel.onAlturaChange(it) },
            label = { Text(stringResource(id = R.string.enter_height)) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.calcularIMC(imcNormalMsg, imcNotNormalMsg, errorMsg) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.calculate_imc))
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (viewModel.error.value.isNotEmpty()) {
            Text(
                text = viewModel.error.value,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (viewModel.imcValor.value.isNotEmpty()) {
            Text(
                text = imcLabel + " " + viewModel.imcValor.value.replace("IMC:", "").trim(),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (viewModel.resultadoIMC.value.isNotEmpty()) {
            Text(
                text = viewModel.resultadoIMC.value,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}