package com.swapi.swapiV1.login.model.network

import android.content.Context
import com.swapi.swapiV1.utils.Constants
import com.swapi.swapiV1.utils.datastore.DataStoreManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Proveedor Singleton para la configuración global de red.
 * Centraliza la creación del cliente HTTP y la inyección del token.
 */
object RetrofitProvider {

    private var retrofit: Retrofit? = null

    /**
     * Inicializa la configuración de red.
     * Debe llamarse una única vez al arrancar la app (ej. en la clase Application o MainActivity).
     */
    fun setup(context: Context) {
        if (retrofit == null) {
            // Configuración de logs para depuración
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            // Instanciamos el gestor de datos para pasárselo al interceptor
            val dataStoreManager = DataStoreManager(context)

            // Construcción del cliente HTTP con los interceptores
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(AuthInterceptor(dataStoreManager)) // Inyección del token automática
                .build()

            // Construcción de la instancia de Retrofit
            retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }
    }

    /**
     * Acceso directo a la API de autenticación.
     */
    val authApi: AuthApi
        get() = getRetrofitOrThrow().create(AuthApi::class.java)

    /**
     * Método genérico para crear cualquier otro servicio/interfaz de la app (ej. UserApi, ProductsApi).
     */
    fun <T> createService(serviceClass: Class<T>): T {
        return getRetrofitOrThrow().create(serviceClass)
    }

    // Método helper para evitar el uso peligroso de '!!' y dar un error claro si falta inicializar.
    private fun getRetrofitOrThrow(): Retrofit {
        return retrofit ?: throw IllegalStateException("RetrofitProvider no inicializado. Debes llamar a RetrofitProvider.setup(context) antes de usarlo.")
    }
}