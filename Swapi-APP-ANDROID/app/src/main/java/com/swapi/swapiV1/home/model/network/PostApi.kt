package com.swapi.swapiV1.home.model.network

import com.swapi.swapiV1.home.model.dto.Product
import com.swapi.swapiV1.home.model.dto.UpdatePostRequest
import com.swapi.swapiV1.login.model.network.RetrofitProvider
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface PostApi {

    /**
     * Crea un post subiendo una imagen y datos de texto simultáneamente.
     * Se usa @Multipart para dividir la petición en partes, permitiendo enviar
     * archivos binarios pesados junto con texto.
     */
    @Multipart
    @POST("api/post")
    suspend fun createPost(
        // La imagen es opcional (?) y viaja como un bloque de datos binarios
        @Part image: MultipartBody.Part?,

        // En peticiones Multipart, el texto no se envía como String simple,
        // sino envuelto en RequestBody para especificar su tipo de contenido (text/plain).
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("price") price: RequestBody,
        @Part("category") category: RequestBody
    ): Response<Product>

    @GET("api/post/my-posts")
    suspend fun getMyPosts(): Response<List<Product>>

    // Devuelve Response<Void> porque al borrar no esperamos datos de vuelta,
    // solo un código de éxito (como 200 o 204).
    @DELETE("api/post/{id}")
    suspend fun deletePost(@Path("id") id: String): Response<Void>

    /**
     * Actualiza la información del post.
     * A diferencia de createPost, aquí usamos @Body porque enviamos un objeto JSON limpio
     * (UpdatePostRequest), sin archivos binarios mezclados.
     */
    @PUT("api/post/{id}")
    suspend fun updatePost(
        @Path("id") id: String,
        @Body body: UpdatePostRequest
    ): Response<Product>
}

object PostApiImpl {
    val service: PostApi by lazy {
        RetrofitProvider.createService(PostApi::class.java)
    }
}