package com.swapi.swapiV1.login.model.network

import com.swapi.swapiV1.utils.datastore.DataStoreManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val dataStoreManager: DataStoreManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // Obtenemos el token almacenado.
        // Usamos runBlocking porque 'intercept' es síncrono, pero DataStore es asíncrono (Flow).
        // Esto pausa momentáneamente el hilo de la red (no el de la UI) hasta tener el dato.
        val token = runBlocking {
            dataStoreManager.getAccessToken().first()
        }

        val requestBuilder = chain.request().newBuilder()

        // Si existe un token válido, lo inyectamos en la cabecera "Authorization".
        if (!token.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}