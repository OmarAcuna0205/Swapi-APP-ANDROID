package com.swapi.androidClassMp1.ids.sum.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class SumViewModel : ViewModel() {
    var num1 by mutableStateOf("")
    var num2 by mutableStateOf("")
    var result by mutableStateOf("Resultado: 0")

    fun calculateSum() {
        val value1 = num1.toFloatOrNull() ?: 0f
        val value2 = num2.toFloatOrNull() ?: 0f
        result = "Resultado: ${value1 + value2}"
    }
}