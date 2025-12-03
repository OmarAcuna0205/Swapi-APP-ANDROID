package com.swapi.swapiV1.utils

import android.content.Context
import com.swapi.swapiV1.R

object ErrorMessageMapper {
    fun getMessage(context: Context, code: String?): String {
        if (code.isNullOrBlank()) return context.getString(R.string.error_generic)

        val resId = when (code) {
            // --- LOGIN Y REGISTRO ---
            "LOGIN_CAMPOS_OBLIGATORIOS" -> R.string.login_error_campos
            "ERROR_RED" -> R.string.login_error_generico
            "REGISTRO_CAMPOS_OBLIGATORIOS" -> R.string.registro_error_campos
            "ERROR_REGISTRO" -> R.string.error_create_post // O crear uno específico
            "VERIFICACION_CODIGO_VACIO" -> R.string.verificacion_codigo_vacio
            "VERIFICACION_CODIGO_INVALIDO" -> R.string.verificacion_codigo_invalido

            // --- POSTS ---
            "POST_DELETED_SUCCESS" -> R.string.msg_post_deleted_success
            "POST_CREATED_SUCCESS" -> R.string.msg_post_created_success
            "POST_UPDATED_SUCCESS" -> R.string.msg_post_updated_success
            "VERIFICACION_EXITOSA" -> R.string.msg_verification_success
            "USER_SAVED_REMOVED" -> R.string.msg_saved_removed
            "USER_SAVED_ADDED" -> R.string.msg_saved_added

            // Errores Posts
            "PALABRAS_OFENSIVAS" -> R.string.error_offensive_words
            "POST_NO_ENCONTRADO" -> R.string.error_post_not_found
            "NO_TIENES_PERMISOS", "NO_ERES_EL_DUEÑO" -> R.string.error_permission_denied
            "AUTH_USER_NOT_FOUND" -> R.string.error_user_not_found
            "ERROR_CREATE_POST" -> R.string.error_create_post
            "ERROR_DELETE_POST" -> R.string.error_delete_post
            "ERROR_UPDATE_POST" -> R.string.error_update_post
            "ERROR_TOGGLE_SAVE" -> R.string.error_toggle_save

            "ERROR_SERVIDOR_VACIO" -> R.string.repo_error_servidor_vacio
            "LOGIN_CREDENCIALES_INVALIDAS" -> R.string.repo_login_credenciales
            "ERROR_REGISTRO_GENERICO" -> R.string.repo_error_registro
            "ERROR_GENERICO" -> R.string.error_generic
            "ERROR_RED" -> R.string.login_error_generico
            "ERROR_LOAD_HOME" -> R.string.home_error_load
            "ERROR_LOAD_PRODUCT" -> R.string.product_error_load

            else -> 0 // No es un código conocido
        }

        return if (resId != 0) {
            context.getString(resId)
        } else {
            // Si no es un código mapeado, devolvemos el mensaje original (ej. mensaje del backend)
            // en lugar de ocultarlo con un error genérico.
            code
        }
    }
}