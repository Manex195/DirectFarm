package com.example.directfarm.app.ui.farmer

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.directfarm.R
import com.example.directfarm.app.databinding.ActivityFarmerDashboardBinding
import com.example.directfarm.app.data.local.PreferencesManager
import com.example.directfarm.app.ui.adapters.ProductAdapter
import com.example.directfarm.app.ui.auth.LoginActivity
import com.example.directfarm.app.utils.*

class FarmerDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFarmerDashboardBinding
    private val viewModel: FarmerViewModel by viewModels()
    private lateinit var prefsManager: PreferencesManager
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFarmerDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        prefsManager = PreferencesManager(this)

        setupUI()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        loadData()
    }

    private fun setupUI() {
        val user = prefsManager.getUser()
        binding.tvWelcome.text = "Welcome, ${user?.fullName}"
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(
            onItemClick = { /* View details if needed */ },
            onEditClick = { product ->
                val intent = Intent(this, AddProductActivity::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT, product)
                intent.putExtra(Constants.EXTRA_IS_EDIT_MODE, true)
                startActivity(intent)
            },
            onDeleteClick = { product ->
                viewModel.deleteProduct(product.id)
            }
        )

        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(this@FarmerDashboardActivity)
            adapter = productAdapter
        }
    }

    private fun setupObservers() {
        viewModel.products.observe(this) { products ->
            productAdapter.submitList(products)
            binding.tvEmptyState.visibility = if (products.isEmpty()) {
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
                loadData()
            }.onFailure { error ->
                showToast(error.message ?: "Operation failed")
            }
        }
    }

    private fun setupClickListeners() {
        binding.fabAddProduct.setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }

        binding.cardOrders.setOnClickListener {
            startActivity(Intent(this, FarmerOrdersActivity::class.java))
        }
    }

    private fun loadData() {
        val user = prefsManager.getUser()
        user?.let {
            viewModel.loadProducts(it.uid)
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_dashboard, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                prefsManager.clearUser()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}