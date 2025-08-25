package com.swapi.androidClassMp1.ids.sum.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.swapi.androidClassMp1.ids.sum.viewmodels.SumViewModel

@Composable
fun SumView(viewModel: SumViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = viewModel.num1,
            onValueChange = { viewModel.num1 = it },
            label = { Text("Número 1") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = viewModel.num2,
            onValueChange = { viewModel.num2 = it },
            label = { Text("Número 2") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.calculateSum() }) {
            Text("Sumar")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(viewModel.result, style = TextStyle(fontSize = 20.sp))
    }
}
