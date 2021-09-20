package ru.mikov.storage.ui.products

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.mikov.storage.data.local.room.entities.Product
import ru.mikov.storage.databinding.ItemProductBinding

class ProductsAdapter(
    private val listener: (Product) -> Unit
) : ListAdapter<Product, ProductViewHolder>(ProductDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemProductBinding.inflate(layoutInflater, parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) =
        holder.bind(getItem(position), listener)

}

class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean = oldItem == newItem
}

class ProductViewHolder(
    private val binding: ItemProductBinding
) : RecyclerView.ViewHolder(binding.root) {

    var item: Product? = null
        private set

    fun bind(
        product: Product,
        listener: (Product) -> Unit
    ) {
        this.item = product
        with(binding) {
            tvProductTitle.text = product.title
            tvProductAmount.text = "Amount ${product.amount}"
            tvProductCode.text = product.code.toString()
        }
        itemView.setOnClickListener { listener.invoke(product) }
    }
}

class SwipeHelper(onSwiped: (Product) -> Unit) : ItemTouchHelper(SwipeCallback(onSwiped))

class SwipeCallback(
    private val onSwiped: (Product) -> Unit,
) : ItemTouchHelper.SimpleCallback(
    0,
    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
) {

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        (viewHolder as? ProductViewHolder)?.item?.let {
            onSwiped(it)
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

}