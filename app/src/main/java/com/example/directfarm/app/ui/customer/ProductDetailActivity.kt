package com.example.directfarm.app.ui.customer

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.directfarm.R
import com.example.directfarm.databinding.ActivityProductDetailBinding
import com.example.directfarm.data.local.PreferencesManager
import com.example.directfarm.data.model.Order
import com.example.directfarm.data.model.Product
import com.example.directfarm.utils.*
import com.google.android.material.textfield.TextInputEditText

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private val viewModel: CustomerViewModel by viewModels()
    private lateinit var prefsManager: PreferencesManager
    private lateinit var product: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        prefsManager = PreferencesManager(this)
        product = intent.getParcelableExtra(Constants.EXTRA_PRODUCT) ?: return

        setupUI()
        setupObservers()
        setupClickListeners()
    }

    private fun setupUI() {
        binding.apply {
            tvProductName.text = product.name
            tvDescription.text = product.description
            tvCategory.text = product.category
            tvPrice.text = product.getFormattedPrice()
            tvQuantity.text = product.getFormattedQuantity()
            tvFarmerName.text = "Farmer: ${product.farmerName}"
            tvLocation.text = product.farmerLocation
            tvPhone.text = product.farmerPhone

            if (product.imageUrl.isNotEmpty()) {
                Glide.with(this@ProductDetailActivity)
                    .load(product.imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(ivProduct)
            }
        }
    }

    private fun setupObservers() {
        viewModel.operationResult.observe(this) { result ->
            result.onSuccess { message ->
                showToast(message)
                finish()
            }.onFailure { error ->
                showToast(error.message ?: "Failed to place order")
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnPlaceOrder.setOnClickListener {
            showOrderDialog()
        }
    }

    private fun showOrderDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_place_order, null)
        val etQuantity = dialogView.findViewById<TextInputEditText>(R.id.etQuantity)
        val etAddress = dialogView.findViewById<TextInputEditText>(R.id.etAddress)

        AlertDialog.Builder(this)
            .setTitle("Place Order")
            .setView(dialogView)
            .setPositiveButton("Confirm") { _, _ ->
                val quantityStr = etQuantity.text.toString()
                val address = etAddress.text.toString()

                if (quantityStr.isEmpty() || address.isEmpty()) {
                    showToast("Please fill all fields")
                    return@setPositiveButton
                }

                val quantity = quantityStr.toDoubleOrNull() ?: 0.0
                if (quantity <= 0 || quantity > product.availableQuantity) {
                    showToast("Invalid quantity")
                    return@setPositiveButton
                }

                placeOrder(quantity, address)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun placeOrder(quantity: Double, address: String) {
        val user = prefsManager.getUser() ?: return

        val order = Order(
            customerId = user.uid,
            customerName = user.fullName,
            customerPhone = user.phoneNumber,
            farmerId = product.farmerId,
            farmerName = product.farmerName,
            farmerPhone = product.farmerPhone,
            productId = product.id,
            productName = product.name,
            productImageUrl = product.imageUrl,
            quantity = quantity,
            pricePerKg = product.pricePerKg,
            totalPrice = quantity * product.pricePerKg,
            deliveryAddress = address
        )

        viewModel.placeOrder(order)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}