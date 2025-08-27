package com.swapi.androidClassMp1.firstpartial.onboarding.model

import com.swapi.androidClassMp1.R

/**
 * Contenido del onboarding
 * Las paginas que tendra el onboarding
 */
object OnboardingContent {
    val pages = listOf(
        OnboardingPageModel(
            imageRes = R.drawable.onb_1,
            title = "¡Tu Campus, Tu Mercado!",
            description = "Compra, vende, renta o comparte servicios dentro de tu propia comunidad escolar, todo en un solo lugar"
        ),
        OnboardingPageModel(
            imageRes = R.drawable.onb_2,
            title = "Ambiente seguro y confiable",
            description = "Un espacio confiable solo para alumnos y docentes"
        ),
        OnboardingPageModel(
            imageRes = R.drawable.onb_3,
            title = "Impulsa a tu comunidad",
            description = "Apoya a tus compañeros, descubre oportunidades y mantente al día con los anuncios"
        )
    )
}
