package ru.mikov.storage.ui.products

import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.mikov.storage.App
import ru.mikov.storage.data.PrefManager
import ru.mikov.storage.data.local.room.entities.Product
import ru.mikov.storage.data.repositories.ProductRepository

class ProductViewModel : ViewModel() {

    private val repository = ProductRepository

    val productsUsingFlow= repository.data.distinctUntilChanged()

    fun delete(product: Product) {
        viewModelScope.launch { repository.delete(product) }
    }

}