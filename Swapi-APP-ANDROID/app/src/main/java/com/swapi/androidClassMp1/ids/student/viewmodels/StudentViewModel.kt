package com.swapi.androidClassMp1.ids.student.viewmodels

import androidx.lifecycle.ViewModel
import com.swapi.androidClassMp1.ids.student.models.StudentModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StudentViewModel : ViewModel() {
    private val _students = MutableStateFlow(
        listOf(
            StudentModel("Jesús Omar Acuña Martínez"),
            StudentModel("Alejandro Carrasco Maldonado"),
            StudentModel("Ian Alejandro Corral Marín"),
            StudentModel("Sara Escamilla Enriquez"),
            StudentModel("Luis Angel Hernandez Corrales"),
            StudentModel("Jose Ricardo Holguín Chiquito"),
            StudentModel("Felix Elias Neder Samaniego"),
            StudentModel("Jorge Parra Hidalgo"),
            StudentModel("Yajahira Payán Palma"),
            StudentModel("Miguel Dario Ruiz Olvera"),
            StudentModel("Manuel Vito Saenz Montes")

        )
    )
    val students: StateFlow<List<StudentModel>> = _students
}