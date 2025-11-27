package com.swapi.swapiV1.home.model.network

import com.swapi.swapiV1.home.model.dto.Product
import com.swapi.swapiV1.login.model.network.RetrofitProvider
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface PostApi {
    @Multipart // ¡Importante para subir archivos!
    @POST("api/post")
    suspend fun createPost(
        @Part image: MultipartBody.Part?, // La foto (puede ser nula si no suben foto)
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("price") price: RequestBody,
        @Part("category") category: RequestBody
    ): Response<Product>
}

object PostApiImpl {
    val service: PostApi by lazy {
        RetrofitProvider.createService(PostApi::class.java)
    }
}