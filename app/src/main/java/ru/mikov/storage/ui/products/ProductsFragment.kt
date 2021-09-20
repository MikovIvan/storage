package ru.mikov.storage.ui.products

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.mikov.storage.R
import ru.mikov.storage.databinding.FragmentProductsBinding


class ProductsFragment : Fragment(R.layout.fragment_products) {

    private val viewModel: ProductViewModel by viewModels()
    private val viewBinding: FragmentProductsBinding by viewBinding()
    private val productsAdapter: ProductsAdapter = ProductsAdapter {
        val action = ProductsFragmentDirections.actionNavProductsToAddProduct(it)
        findNavController().navigate(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        with(viewBinding) {
            rvProducts.apply {
                layoutManager = LinearLayoutManager(context)
                SwipeHelper(viewModel::delete).attachToRecyclerView(this)
                adapter = productsAdapter
            }

            fabAddProduct.setOnClickListener {
                findNavController().navigate(R.id.action_nav_products_to_add_product)
            }
        }

        renderProducts()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_filter, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter -> {
                findNavController().navigate(R.id.action_nav_products_to_filter_product)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun renderProducts() {
        viewModel.productsUsingFlow.onEach {
            productsAdapter.submitList(it.toList())
        }.launchIn(lifecycleScope)
    }

}