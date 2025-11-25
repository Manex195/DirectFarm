package com.example.directfarm.app.ui.farmer

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.directfarm.app.databinding.ActivityFarmerOrdersBinding
import com.example.directfarm.app.data.local.PreferencesManager
import com.example.directfarm.app.data.model.Order
import com.example.directfarm.app.data.model.OrderStatus
import com.example.directfarm.app.ui.adapters.OrderAdapter
import com.example.directfarm.app.utils.*

class FarmerOrdersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFarmerOrdersBinding
    private val viewModel: FarmerViewModel by viewModels()
    private lateinit var prefsManager: PreferencesManager
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFarmerOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        prefsManager = PreferencesManager(this)

        setupRecyclerView()
        setupObservers()
        loadOrders()
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(
            onItemClick = { order -> showOrderDetails(order) },
            showStatusButton = true,
            onStatusClick = { order -> showStatusDialog(order) }
        )

        binding.rvOrders.apply {
            layoutManager = LinearLayoutManager(this@FarmerOrdersActivity)
            adapter = orderAdapter
        }
    }

    private fun setupObservers() {
        viewModel.orders.observe(this) { orders ->
            orderAdapter.submitList(orders)
            binding.tvEmptyState.visibility = if (orders.isEmpty()) {
                android.view.View.VISIBLE
            } else {
                android.view.View.GONE
            }
        }

        viewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) {
                android.view.View.VISIBLE
            } else {
                android.view.View.GONE
            }
        }

        viewModel.operationResult.observe(this) { result ->
            result.onSuccess { message ->
                showToast(message)
                loadOrders()
            }
        }
    }

    private fun loadOrders() {
        val user = prefsManager.getUser()
        user?.let { viewModel.loadOrders(it.uid) }
    }

    private fun showOrderDetails(order: Order) {
        val message = """
            Customer: ${order.customerName}
            Phone: ${order.customerPhone}
            Product: ${order.productName}
            Quantity: ${order.getFormattedQuantity()}
            Total: ${order.getFormattedTotal()}
            Address: ${order.deliveryAddress}
            Status: ${order.status}
            Date: ${order.createdAt.formatDate()}
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Order Details")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showStatusDialog(order: Order) {
        val statuses = OrderStatus.values().map { it.name }.toTypedArray()
        val currentIndex = statuses.indexOf(order.status)

        AlertDialog.Builder(this)
            .setTitle("Update Order Status")
            .setSingleChoiceItems(statuses, currentIndex) { dialog, which ->
                val newStatus = OrderStatus.valueOf(statuses[which])
                viewModel.updateOrderStatus(order.id, newStatus)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}