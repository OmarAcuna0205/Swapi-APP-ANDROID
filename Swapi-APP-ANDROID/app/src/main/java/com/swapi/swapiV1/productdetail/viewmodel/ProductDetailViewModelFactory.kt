package com.swapi.swapiV1.productdetail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.swapi.swapiV1.home.model.repository.HomeRepository
import com.swapi.swapiV1.home.model.repository.UserRepository

/**
 * Factory personalizada para crear instancias de ProductDetailViewModel.
 *
 * En Android, por defecto, los ViewModels deben tener constructores vacíos.
 * Como nuestro ProductDetailViewModel necesita recibir datos (el ID del producto) y repositorios
 * al momento de nacer, necesitamos esta Factory para inyectar esas dependencias manualmente.
 */
class ProductDetailViewModelFactory(
    private val productId: String, // El ID que recibimos de la navegación (argumento)
    private val homeRepository: HomeRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {

    /**
     * Método boilerplate (estándar) que construye el ViewModel.
     * Verifica que la clase sea la correcta y retorna la instancia con los parámetros necesarios.
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductDetailViewModel::class.java)) {
            // Aquí es donde ocurre la "inyección" manual de dependencias al constructor
            return ProductDetailViewModel(productId, homeRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}