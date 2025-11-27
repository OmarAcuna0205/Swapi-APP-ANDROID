package com.swapi.swapiV1.login.model.network

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {
    // TU IP LOCAL
    private const val BASE_URL = "http://192.168.1.69:3000/" // <--- Confirma que siga siendo esta

    private var retrofit: Retrofit? = null

    fun setup(context: Context) {
        if (retrofit == null) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            // AQUI AGREGAMOS EL AUTH INTERCEPTOR
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(AuthInterceptor(context)) // <--- ¡ESTO FALTABA!
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }
    }

    // Funciones seguras para obtener las APIs
    val authApi: AuthApi
        get() = retrofit!!.create(AuthApi::class.java)

    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit!!.create(serviceClass)
    }
}