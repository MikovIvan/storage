package ru.mikov.storage.ui.addproduct

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.mikov.storage.data.local.room.entities.Product
import ru.mikov.storage.data.repositories.ProductRepository

class AddProductViewModel : ViewModel() {

    private val repository = ProductRepository

    fun saveProduct(product:Product){
        viewModelScope.launch { repository.saveProduct(product) }
    }

    fun updateProduct(product:Product){
        viewModelScope.launch { repository.update(product) }
    }
}