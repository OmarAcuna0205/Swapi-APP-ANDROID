package com.swapi.swapiV1.home.model.network

import com.swapi.swapiV1.home.model.dto.Product
import com.swapi.swapiV1.login.model.network.RetrofitProvider
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface HomeApi {

    /**
     * Obtiene la lista de productos.
     * La clave aquí es que 'category' es opcional (String?).
     * Si envías null, Retrofit ignora el filtro y trae todo.
     */
    @GET("api/post")
    suspend fun getPosts(
        @Query("category") category: String? = null
    ): Response<List<Product>>

    /**
     * Obtiene un producto específico por su ID.
     * Usa @Path porque el ID es parte de la ruta de la URL, no un filtro.
     */
    @GET("api/post/{id}")
    suspend fun getProduct(@Path("id") id: String): Response<Product>
}

object HomeApiImpl {
    // Inicialización perezosa (Lazy): El servicio no se crea hasta que se llama
    // por primera vez, ahorrando memoria si el usuario nunca entra a esta sección.
    val service: HomeApi by lazy {
        RetrofitProvider.createService(HomeApi::class.java)
    }
}