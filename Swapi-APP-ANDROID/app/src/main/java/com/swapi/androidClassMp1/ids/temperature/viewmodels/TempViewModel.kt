package com.swapi.androidClassMp1.ids.temperature.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class TempViewModel : ViewModel() {
    var Cel = mutableStateOf("")
    var Fahr = mutableStateOf("")
    var msgerror = mutableStateOf("")

    fun convertToFahrenheit(errorMsg: String) {
        try {
            val celsius = Cel.value.toDouble()
            val fahrenheit = (celsius * 9/5) + 32
            Fahr.value = fahrenheit.toString()
            msgerror.value = ""
        } catch (e: NumberFormatException) {
            msgerror.value = errorMsg
            Fahr.value = ""
        }
    }
}
