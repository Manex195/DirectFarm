package com.example.directfarm.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.directfarm.app.R
import com.example.directfarm.app.data.model.Product
import com.example.directfarm.app.databinding.ItemProductBinding
import com.example.directfarm.app.utils.gone
import com.example.directfarm.app.utils.visible

class ProductAdapter(
    private val onItemClick: (Product) -> Unit,
    private val onEditClick: ((Product) -> Unit)? = null,
    private val onDeleteClick: ((Product) -> Unit)? = null
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.apply {
                tvProductName.text = product.name
                tvPrice.text = product.getFormattedPrice()
                tvQuantity.text = product.getFormattedQuantity()
                tvLocation.text = product.farmerLocation
                tvCategory.text = product.category

                // Load image
                if (product.imageUrl.isNotEmpty()) {
                    Glide.with(root.context)
                        .load(product.imageUrl)
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(ivProduct)
                } else {
                    ivProduct.setImageResource(R.drawable.ic_launcher_background)
                }

                // Show/hide action buttons
                if (onEditClick != null && onDeleteClick != null) {
                    btnEdit.visible()
                    btnDelete.visible()

                    btnEdit.setOnClickListener { onEditClick.invoke(product) }
                    btnDelete.setOnClickListener { onDeleteClick.invoke(product) }
                } else {
                    btnEdit.gone()
                    btnDelete.gone()
                }

                root.setOnClickListener { onItemClick(product) }
            }
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}