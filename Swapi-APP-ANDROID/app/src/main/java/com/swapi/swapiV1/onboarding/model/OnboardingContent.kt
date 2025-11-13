// package com.swapi.swapiV1.onboarding.model
package com.swapi.swapiV1.onboarding.model

import com.swapi.swapiV1.R

/**
 * Contenido del onboarding
 * Las paginas que tendra el onboarding
 */
object OnboardingContent {
    val pages = listOf(
        OnboardingPageModel(
            imageRes = R.drawable.onb_1,
            // CAMBIO: Usamos el ID del recurso
            titleResId = R.string.onboarding_page1_title,
            descriptionResId = R.string.onboarding_page1_desc
        ),
        OnboardingPageModel(
            imageRes = R.drawable.onb_2,
            // CAMBIO: Usamos el ID del recurso
            titleResId = R.string.onboarding_page2_title,
            descriptionResId = R.string.onboarding_page2_desc
        ),
        OnboardingPageModel(
            imageRes = R.drawable.onb_3,
            // CAMBIO: Usamos el ID del recurso
            titleResId = R.string.onboarding_page3_title,
            descriptionResId = R.string.onboarding_page3_desc
        )
    )
}