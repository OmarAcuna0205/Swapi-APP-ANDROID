package com.swapi.swapiV1.login.model.network

import android.content.Context
import com.swapi.swapiV1.utils.datastore.DataStoreManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val dataStore = DataStoreManager(context)

        // Obtenemos el token de forma síncrona (bloqueando este hilo secundario)
        val token = runBlocking {
            dataStore.getAccessToken().first()
        }

        val requestBuilder = chain.request().newBuilder()

        // Si tenemos token, lo pegamos en la cabecera
        if (!token.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}