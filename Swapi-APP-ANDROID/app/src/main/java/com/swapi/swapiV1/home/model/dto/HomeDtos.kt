package com.swapi.swapiV1.home.model.dto

import com.google.gson.annotations.SerializedName

/**
 * Modelo principal del producto para lectura (GET).
 * Mapea la respuesta completa que viene del servidor.
 */
data class Product(
    // @SerializedName es el puente entre el Backend y Android.
    // El servidor envía "_id" (común en MongoDB), pero en Kotlin queremos usar simplemente "id".
    @SerializedName("_id") val id: String,

    val title: String,
    val description: String,
    val price: Double,
    val category: String,
    val images: List<String>,

    // Objeto anidado: La API entrega la información del autor dentro del objeto producto
    val author: AuthorDto,

    val isActive: Boolean,

    // Mismo caso: el JSON trae "createdAt", pero aquí lo renombramos a "postDate"
    @SerializedName("createdAt") val postDate: String
)

data class AuthorDto(
    @SerializedName("_id") val id: String,
    val firstName: String,
    val email: String,

    // Campos nulleables (? = null): Esto es defensivo.
    // Si el usuario no registró teléfono o apellido, o la API no lo envía,
    // la app no se romperá al intentar parsear un null.
    val phone: String? = null,
    val paternalSurname: String? = null
)

/**
 * Modelo específico para actualizaciones (PUT/PATCH).
 * Es una buena práctica separar esto de la clase Product, ya que al editar
 * NO debemos enviar campos inmutables como el ID, el Autor o la fecha de creación.
 */
data class UpdatePostRequest(
    val title: String,
    val description: String,
    val price: Double,
    val category: String
)