package com.swapi.swapiV1.utils

import android.content.Context
import com.swapi.swapiV1.R

/**
 * Objeto Singleton (Utility) encargado de traducir los códigos de error técnicos
 * provenientes del backend (o excepciones locales) a mensajes amigables y localizados para el usuario.
 * Centraliza la lógica de mapeo para mantener la consistencia en toda la app.
 */
object ErrorMessageMapper {

    /**
     * Obtiene el mensaje de error localizado correspondiente a un código dado.
     */
    fun getMessage(context: Context, code: String?): String {
        // Fallback inmediato para códigos nulos o vacíos.
        if (code.isNullOrBlank()) return context.getString(R.string.error_generic)

        // Mapeo exhaustivo de códigos de error a recursos de string (IDs).
        val resId = when (code) {
            // --- SECCIÓN: AUTH (Login, Registro, Verificación) ---
            "LOGIN_CAMPOS_OBLIGATORIOS" -> R.string.login_error_campos
            "ERROR_RED" -> R.string.login_error_generico
            "REGISTRO_CAMPOS_OBLIGATORIOS" -> R.string.registro_error_campos
            "ERROR_REGISTRO" -> R.string.error_create_post // Reutilizamos mensaje genérico de creación
            "VERIFICACION_CODIGO_VACIO" -> R.string.verificacion_codigo_vacio
            "VERIFICACION_CODIGO_INVALIDO" -> R.string.verificacion_codigo_invalido

            // --- SECCIÓN: MENSAJES DE ÉXITO (Feedback positivo) ---
            "POST_DELETED_SUCCESS" -> R.string.msg_post_deleted_success
            "POST_CREATED_SUCCESS" -> R.string.msg_post_created_success
            "POST_UPDATED_SUCCESS" -> R.string.msg_post_updated_success
            "VERIFICACION_EXITOSA" -> R.string.msg_verification_success
            "USER_SAVED_REMOVED" -> R.string.msg_saved_removed
            "USER_SAVED_ADDED" -> R.string.msg_saved_added

            // --- SECCIÓN: POSTS (Errores específicos de publicaciones) ---
            "PALABRAS_OFENSIVAS" -> R.string.error_offensive_words // Filtro de contenido
            "POST_NO_ENCONTRADO" -> R.string.error_post_not_found
            "NO_TIENES_PERMISOS", "NO_ERES_EL_DUEÑO" -> R.string.error_permission_denied
            "AUTH_USER_NOT_FOUND" -> R.string.error_user_not_found
            "ERROR_CREATE_POST" -> R.string.error_create_post
            "ERROR_DELETE_POST" -> R.string.error_delete_post
            "ERROR_UPDATE_POST" -> R.string.error_update_post
            "ERROR_TOGGLE_SAVE" -> R.string.error_toggle_save

            // --- SECCIÓN: REPOSITORIO / RED (Errores técnicos) ---
            "ERROR_SERVIDOR_VACIO" -> R.string.repo_error_servidor_vacio
            "LOGIN_CREDENCIALES_INVALIDAS" -> R.string.repo_login_credenciales
            "ERROR_REGISTRO_GENERICO" -> R.string.repo_error_registro
            "ERROR_GENERICO" -> R.string.error_generic
            "ERROR_RED" -> R.string.login_error_generico
            "ERROR_LOAD_HOME" -> R.string.home_error_load
            "ERROR_LOAD_PRODUCT" -> R.string.product_error_load

            else -> 0 // 0 indica que no se encontró un recurso asociado al código.
        }

        // Si encontramos un ID válido, retornamos el string localizado.
        // Si no, devolvemos el código original para facilitar la depuración en desarrollo.
        return if (resId != 0) {
            context.getString(resId)
        } else {
            code
        }
    }
}