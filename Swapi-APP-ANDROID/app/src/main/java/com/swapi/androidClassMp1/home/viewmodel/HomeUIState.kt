package com.swapi.androidClassMp1.home.viewmodel

import com.swapi.androidClassMp1.home.model.dto.HomeSectionDto

sealed interface HomeUIState {
    data object Loading : HomeUIState
    data class Success(val sections: List<HomeSectionDto>) : HomeUIState
    data class Error(val message: String) : HomeUIState
}