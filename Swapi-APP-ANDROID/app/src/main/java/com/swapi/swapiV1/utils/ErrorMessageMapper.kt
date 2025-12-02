package com.swapi.swapiV1.utils

import android.content.Context
import com.swapi.swapiV1.R

object ErrorMessageMapper {
    fun getMessage(context: Context, code: String?): String {
        val resId = when (code) {
            // Éxitos
            "POST_DELETED_SUCCESS" -> R.string.msg_post_deleted_success
            "POST_CREATED_SUCCESS" -> R.string.msg_post_created_success
            "POST_UPDATED_SUCCESS" -> R.string.msg_post_updated_success
            "VERIFICACION_EXITOSA" -> R.string.msg_verification_success
            "USER_SAVED_REMOVED" -> R.string.msg_saved_removed
            "USER_SAVED_ADDED" -> R.string.msg_saved_added

            // Errores
            "PALABRAS_OFENSIVAS" -> R.string.error_offensive_words
            "POST_NO_ENCONTRADO" -> R.string.error_post_not_found
            "NO_TIENES_PERMISOS", "NO_ERES_EL_DUEÑO" -> R.string.error_permission_denied
            "AUTH_USER_NOT_FOUND" -> R.string.error_user_not_found
            "ERROR_CREATE_POST" -> R.string.error_create_post
            "ERROR_DELETE_POST" -> R.string.error_delete_post
            "ERROR_UPDATE_POST" -> R.string.error_update_post
            "ERROR_TOGGLE_SAVE" -> R.string.error_toggle_save

            // Default
            else -> R.string.error_generic
        }
        return context.getString(resId)
    }
}