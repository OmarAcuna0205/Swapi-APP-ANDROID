package com.swapi.androidClassMp1.home.model.dto

import com.google.gson.annotations.SerializedName

// Estructura principal del JSON
data class HomeScreenResponse(
    @SerializedName("homeScreen") val homeScreen: List<HomeSectionDto>
)

// Representa cada sección con un título y una lista de anuncios
data class HomeSectionDto(
    @SerializedName("sectionTitle") val sectionTitle: String,
    @SerializedName("listings") val listings: List<ListingDto>
)

// Representa un anuncio o publicación individual
data class ListingDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Double,
    @SerializedName("currency") val currency: String,
    @SerializedName("category") val category: String,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("postDate") val postDate: String,
    @SerializedName("user") val user: UserDto
)

// Representa la información del usuario que publica
data class UserDto(
    @SerializedName("userId") val userId: String,
    @SerializedName("name") val name: String,
    @SerializedName("avatarUrl") val avatarUrl: String,
    @SerializedName("contactInfo") val contactInfo: String
)