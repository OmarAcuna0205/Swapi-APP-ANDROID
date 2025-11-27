package com.swapi.swapiV1.home.model.dto

import com.google.gson.annotations.SerializedName

// Representa la publicación (El "Post" de MongoDB)
data class Product(
    @SerializedName("_id") val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val category: String,
    val images: List<String>, // Lista de nombres de imagen
    val author: AuthorDto,    // Objeto Autor completo
    val isActive: Boolean,
    @SerializedName("createdAt") val postDate: String
)

// Representa al usuario dentro de la publicación
data class AuthorDto(
    @SerializedName("_id") val id: String,
    val firstName: String,
    val email: String,
    val phone: String? = null,
    val paternalSurname: String? = null
)