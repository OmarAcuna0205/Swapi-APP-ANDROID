package com.swapi.swapiV1.utils.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DS_NAME = "app_prefs_ds"
val Context.dataStore by preferencesDataStore(DS_NAME)

class DataStoreManager(private val context: Context) {

    private object Keys {
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val LOGGED_IN = booleanPreferencesKey("logged_in")
        val USER_NAME = stringPreferencesKey("user_name")

        // --- NUEVA CLAVE PARA EL TOKEN ---
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
    }

    // Flows de lectura
    val onboardingDoneFlow: Flow<Boolean> =
        context.dataStore.data.map { prefs -> prefs[Keys.ONBOARDING_DONE] ?: false }

    val isLoggedInFlow: Flow<Boolean> =
        context.dataStore.data.map { prefs -> prefs[Keys.LOGGED_IN] ?: false }

    val userNameFlow: Flow<String> =
        context.dataStore.data.map { prefs -> prefs[Keys.USER_NAME] ?: "Usuario" }

    // --- NUEVO FLOW PARA LEER TOKEN ---
    // Lo usará el AuthInterceptor para pegarlo en las peticiones
    fun getAccessToken(): Flow<String?> {
        return context.dataStore.data.map { prefs -> prefs[Keys.ACCESS_TOKEN] }
    }

    // Funciones de escritura
    suspend fun setOnboardingDone(done: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ONBOARDING_DONE] = done
        }
    }

    suspend fun setLoggedIn(loggedIn: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.LOGGED_IN] = loggedIn
        }
    }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.USER_NAME] = name
        }
    }

    // --- NUEVA FUNCIÓN PARA GUARDAR TOKEN ---
    // Lo usará el LoginViewModel al entrar y el MainActivity al salir (para borrarlo)
    suspend fun saveAccessToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ACCESS_TOKEN] = token
        }
    }
}