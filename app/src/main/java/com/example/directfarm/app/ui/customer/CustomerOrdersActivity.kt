package com.example.directfarm.app.ui.customer


import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.directfarm.app.databinding.ActivityCustomerOrdersBinding
import com.example.directfarm.app.data.local.PreferencesManager
import com.example.directfarm.app.data.model.Order
import com.example.directfarm.app.ui.adapters.OrderAdapter
import com.example.directfarm.app.utils.*

class CustomerOrdersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomerOrdersBinding
    private val viewModel: CustomerViewModel by viewModels()
    private lateinit var prefsManager: PreferencesManager
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerOrdersBinding.inflate(layoutInflater)
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
            showStatusButton = false
        )

        binding.rvOrders.apply {
            layoutManager = LinearLayoutManager(this@CustomerOrdersActivity)
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
    }

    private fun loadOrders() {
        val user = prefsManager.getUser()
        user?.let { viewModel.loadOrders(it.uid) }
    }

    private fun showOrderDetails(order: Order) {
        val message = """
            Farmer: ${order.farmerName}
            Phone: ${order.farmerPhone}
            Product: ${order.productName}
            Quantity: ${order.getFormattedQuantity()}
            Total: ${order.getFormattedTotal()}
            Delivery: ${order.deliveryAddress}
            Status: ${order.status}
            Date: ${order.createdAt.formatDate()}
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Order Details")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}