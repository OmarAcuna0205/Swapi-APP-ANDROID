package com.swapi.swapiV1.utils.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey // <-- NUEVO IMPORT
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DS_NAME = "app_prefs_ds"
val Context.dataStore by preferencesDataStore(DS_NAME)

class DataStoreManager(private val context: Context) {

    private object Keys {
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val LOGGED_IN = booleanPreferencesKey("logged_in")
        val USER_NAME = stringPreferencesKey("user_name") // <-- NUEVA KEY
    }

    val onboardingDoneFlow: Flow<Boolean> =
        context.dataStore.data.map { prefs -> prefs[Keys.ONBOARDING_DONE] ?: false }

    val isLoggedInFlow: Flow<Boolean> =
        context.dataStore.data.map { prefs -> prefs[Keys.LOGGED_IN] ?: false }

    // --- NUEVO FLOW ---
    // Flow para leer el nombre del usuario, con un valor por defecto.
    val userNameFlow: Flow<String> =
        context.dataStore.data.map { prefs -> prefs[Keys.USER_NAME] ?: "Usuario" }
    // -------------------

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

    // --- NUEVA FUNCIÓN ---
    // Función para guardar el nombre del usuario
    suspend fun setUserName(name: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.USER_NAME] = name
        }
    }
    // ---------------------
}
