package com.example.directfarm.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.directfarm.R
import com.example.directfarm.app.data.model.Order
import com.example.directfarm.app.databinding.ItemOrderBinding
import com.example.directfarm.app.utils.formatDate
import com.example.directfarm.app.utils.gone
import com.example.directfarm.app.utils.visible

class OrderAdapter(
    private val onItemClick: (Order) -> Unit,
    private val showStatusButton: Boolean = false,
    private val onStatusClick: ((Order) -> Unit)? = null
) : ListAdapter<Order, OrderAdapter.OrderViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderViewHolder(
        private val binding: ItemOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.apply {
                tvOrderId.text = "Order #${order.id.take(8)}"
                tvProductName.text = order.productName
                tvQuantity.text = order.getFormattedQuantity()
                tvTotalPrice.text = order.getFormattedTotal()
                tvStatus.text = order.status
                tvDate.text = order.createdAt.formatDate()

                tvCustomerName.text = if (showStatusButton) {
                    "Customer: ${order.customerName}"
                } else {
                    "Farmer: ${order.farmerName}"
                }

                if (order.productImageUrl.isNotEmpty()) {
                    Glide.with(root.context)
                        .load(order.productImageUrl)
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(ivProduct)
                }

                if (showStatusButton && onStatusClick != null) {
                    btnUpdateStatus.visible()
                    btnUpdateStatus.setOnClickListener { onStatusClick.invoke(order) }
                } else {
                    btnUpdateStatus.gone()
                }

                root.setOnClickListener { onItemClick(order) }
            }
        }
    }

    class OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }
}