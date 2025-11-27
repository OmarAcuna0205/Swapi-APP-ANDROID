package com.swapi.swapiV1.home.model.network

import com.swapi.swapiV1.home.model.dto.Product
import com.swapi.swapiV1.home.model.dto.UpdatePostRequest
import com.swapi.swapiV1.login.model.network.RetrofitProvider
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface PostApi {
    @Multipart
    @POST("api/post")
    suspend fun createPost(
        @Part image: MultipartBody.Part?,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("price") price: RequestBody,
        @Part("category") category: RequestBody
    ): Response<Product>

    // Obtener solo mis posts
    @GET("api/post/my-posts")
    suspend fun getMyPosts(): Response<List<Product>>

    // Eliminar un post
    @DELETE("api/post/{id}")
    suspend fun deletePost(@Path("id") id: String): Response<Void>

    // --- NUEVO: Actualizar un post ---
    @PUT("api/post/{id}")
    suspend fun updatePost(
        @Path("id") id: String,
        @Body body: UpdatePostRequest // <--- Usamos la clase concreta en vez de Map
    ): Response<Product>
}

object PostApiImpl {
    val service: PostApi by lazy {
        RetrofitProvider.createService(PostApi::class.java)
    }
}