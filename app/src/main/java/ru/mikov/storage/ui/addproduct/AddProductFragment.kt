package ru.mikov.storage.ui.addproduct

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.mikov.storage.R
import ru.mikov.storage.data.local.room.entities.Product
import ru.mikov.storage.databinding.FragmentAddProductBinding


class AddProductFragment : Fragment(R.layout.fragment_add_product) {

    private val viewBinding: FragmentAddProductBinding by viewBinding()
    private val viewModel: AddProductViewModel by viewModels()
    private val args: AddProductFragmentArgs by navArgs()
    private var product: Product? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        product = args.product

        with(viewBinding) {
            product?.let { setFields(it) }

            btnSave.setOnClickListener {
                if (isValidFields()) {
                    if (product != null) updateProduct(product!!) else saveProduct()
                    findNavController().navigate(R.id.action_add_product_to_nav_products)
                } else {
                    showErrors()
                }
            }
        }
    }

    private fun showErrors() {
        with(viewBinding) {
            if (etProductTitle.text.isNullOrBlank()) wrapTitle.error = "Enter title" else wrapTitle.isErrorEnabled = false
            if (etProductAmount.text.isNullOrBlank()) wrapAmount.error = "Enter Amount" else wrapTitle.isErrorEnabled = false
            if (etProductCode.text.isNullOrBlank()) wrapCode.error = "Enter Code" else wrapTitle.isErrorEnabled = false
        }
    }

    private fun saveProduct() {
        with(viewBinding) {
            if (isValidFields()) {
                Product(
                    title = etProductTitle.text.toString(),
                    amount = etProductAmount.text.toString().toInt(),
                    code = etProductCode.text.toString().toInt()
                ).apply { viewModel.saveProduct(this) }
            }
        }
    }

    private fun updateProduct(product: Product) {
        with(viewBinding) {
            if (isValidFields()) {
                product.copy(
                    title = etProductTitle.text.toString(),
                    amount = etProductAmount.text.toString().toInt(),
                    code = etProductCode.text.toString().toInt()
                ).apply { viewModel.updateProduct(this) }
            }
        }
    }

    private fun setFields(product: Product) {
        with(viewBinding) {
            etProductTitle.setText(product.title)
            etProductAmount.setText(product.amount.toString())
            etProductCode.setText(product.code.toString())
        }
    }

    private fun isValidFields(): Boolean {
        with(viewBinding) {
            return when {
                etProductTitle.text.isNullOrBlank() -> false
                etProductAmount.text.isNullOrBlank() -> false
                etProductCode.text.isNullOrBlank() -> false
                else -> true
            }
        }
    }

}