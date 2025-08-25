package com.swapi.androidClassMp1.ids.imc.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class IMCViewModel : ViewModel() {

    var peso = mutableStateOf("")
        private set
    var altura = mutableStateOf("")
        private set
    var resultadoIMC = mutableStateOf("")
        private set
    var error = mutableStateOf("")
    var imcValor = mutableStateOf("")

    fun onPesoChange(newValue: String) {
        peso.value = newValue
    }

    fun onAlturaChange(newValue: String) {
        altura.value = newValue
    }

    fun calcularIMC(imcNormalMsg: String, imcNotNormalMsg: String, errorMsg: String) {
        error.value = ""
        resultadoIMC.value = ""
        imcValor.value = ""

        val pesoKg = peso.value.toFloatOrNull()
        val alturaM = altura.value.toFloatOrNull()

        if (pesoKg == null || alturaM == null || alturaM <= 0) {
            error.value = errorMsg
            return
        }

        val imc = pesoKg / (alturaM * alturaM)
        imcValor.value = "IMC: %.2f".format(imc)

        resultadoIMC.value = if (imc in 19.0..24.9) imcNormalMsg else imcNotNormalMsg
    }
}