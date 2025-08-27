package com.swapi.androidClassMp1.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font // 👈 OJO: este es el correcto

// 1. Provider para Google Fonts
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = com.swapi.androidClassMp1.R.array.com_google_android_gms_fonts_certs // 👈 usar tu paquete aquí
)

// 2. Nombre de la fuente
val montserratFontName = GoogleFont("Montserrat")

// 3. Familia tipográfica con Montserrat
val Montserrat = FontFamily(
    Font(googleFont = montserratFontName, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = montserratFontName, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = montserratFontName, fontProvider = provider, weight = FontWeight.Bold)
)

// 4. Definir estilos tipográficos
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
