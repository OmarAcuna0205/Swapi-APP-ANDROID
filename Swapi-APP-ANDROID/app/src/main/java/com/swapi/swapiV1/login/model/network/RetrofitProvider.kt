package com.swapi.swapiV1.login.model.network

import android.content.Context
import com.swapi.swapiV1.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {
    private var retrofit: Retrofit? = null

    fun setup(context: Context) {
        if (retrofit == null) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(AuthInterceptor(context))
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL) // <--- Usamos la constante
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }
    }

    val authApi: AuthApi
        get() = retrofit!!.create(AuthApi::class.java)

    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit!!.create(serviceClass)
    }
}