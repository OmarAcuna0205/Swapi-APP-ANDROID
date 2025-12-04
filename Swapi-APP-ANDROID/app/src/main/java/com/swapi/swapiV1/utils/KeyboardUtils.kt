package com.swapi.swapiV1.utils // O donde quieras ponerlo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

/**
 * Este Modifier personalizado cierra el teclado cuando se hace clic
 * en el Composable que lo tiene.
 * * Se usa "composed" para obtener acceso a los controladores (Keyboard/Focus).
 */
fun Modifier.dismissKeyboardOnClick(): Modifier = composed {
    // 1. Obtenemos el controlador del teclado
    val keyboardController = LocalSoftwareKeyboardController.current

    // 2. Obtenemos el manejador de "foco" (quién está seleccionado)
    val focusManager = LocalFocusManager.current

    this.clickable(
        // 3. Hacemos que sea clickeable
        indication = null,

        // ...sin estado de interacción (para que no parezca un botón)
        interactionSource = remember { MutableInteractionSource() }
    ) {
        // 4. Cuando se hace clic en el área de este Modifier:
        keyboardController?.hide() // (A) Escondemos el teclado
        focusManager.clearFocus()  // (B) Le quitamos el foco al TextField
    }
}