// package com.swapi.swapiV1.onboarding.model
package com.swapi.swapiV1.onboarding.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes // <-- AÑADIR esta importación

/**
 * Este será el modelo de datos de nuestro onboarding
 * Imagen que viene desde el local de la computadora
 * Titulo de la pagina del onboarding
 * Descripción de la funcionaldiad de  la pagina
 */
data class OnboardingPageModel(
    @DrawableRes val imageRes: Int,
    @StringRes val titleResId: Int,       // <-- CAMBIO: de 'title: String'
    @StringRes val descriptionResId: Int  // <-- CAMBIO: de 'description: String'
)