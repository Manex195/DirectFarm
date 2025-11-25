package com.example.directfarm.app.ui.farmer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.directfarm.R
import com.example.directfarm.app.databinding.ActivityAddProductBinding
import com.example.directfarm.app.data.local.PreferencesManager
import com.example.directfarm.app.data.model.Product
import com.example.directfarm.app.utils.*

class AddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding
    private val viewModel: FarmerViewModel by viewModels()
    private lateinit var prefsManager: PreferencesManager
    private var selectedImageUri: Uri? = null
    private var isEditMode = false
    private var existingProduct: Product? = null

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            binding.ivProductImage.setImageURI(selectedImageUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        prefsManager = PreferencesManager(this)

        isEditMode = intent.getBooleanExtra(Constants.EXTRA_IS_EDIT_MODE, false)
        existingProduct = intent.getParcelableExtra(Constants.EXTRA_PRODUCT)

        setupUI()
        setupCategorySpinner()
        setupObservers()
        setupClickListeners()

        if (isEditMode && existingProduct != null) {
            loadProductData(existingProduct!!)
        }
    }

    private fun setupUI() {
        title = if (isEditMode) "Edit Product" else "Add Product"
        binding.btnSave.text = if (isEditMode) "Update" else "Add Product"
    }

    private fun setupCategorySpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            Product.CATEGORIES
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.operationResult.observe(this) { result ->
            binding.progressBar.gone()
            binding.btnSave.isEnabled = true

            result.onSuccess { message ->
                showToast(message)
                finish()
            }.onFailure { error ->
                showToast(error.message ?: "Operation failed")
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            imagePickerLauncher.launch(intent)
        }

        binding.btnSave.setOnClickListener {
            saveProduct()
        }
    }

    private fun loadProductData(product: Product) {
        binding.apply {
            etProductName.setText(product.name)
            etDescription.setText(product.description)
            etPrice.setText(product.pricePerKg.toString())
            etQuantity.setText(product.availableQuantity.toString())

            val categoryIndex = Product.CATEGORIES.indexOf(product.category)
            if (categoryIndex >= 0) {
                spinnerCategory.setSelection(categoryIndex)
            }

            if (product.imageUrl.isNotEmpty()) {
                Glide.with(this@AddProductActivity)
                    .load(product.imageUrl)
                    .into(ivProductImage)
            }
        }
    }

    private fun saveProduct() {
        val name = binding.etProductName.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val priceStr = binding.etPrice.text.toString().trim()
        val quantityStr = binding.etQuantity.text.toString().trim()
        val category = binding.spinnerCategory.selectedItem.toString()

        if (!validateInput(name, description, priceStr, quantityStr)) return

        val price = priceStr.toDouble()
        val quantity = quantityStr.toDouble()
        val user = prefsManager.getUser() ?: return

        val product = if (isEditMode && existingProduct != null) {
            existingProduct!!.copy(
                name = name,
                description = description,
                category = category,
                pricePerKg = price,
                availableQuantity = quantity
            )
        } else {
            Product(
                farmerId = user.uid,
                farmerName = user.fullName,
                farmerLocation = user.location,
                farmerPhone = user.phoneNumber,
                name = name,
                description = description,
                category = category,
                pricePerKg = price,
                availableQuantity = quantity
            )
        }

        binding.progressBar.visible()
        binding.btnSave.isEnabled = false

        if (isEditMode) {
            viewModel.updateProduct(product, selectedImageUri)
        } else {
            viewModel.addProduct(product, selectedImageUri)
        }
    }

    private fun validateInput(name: String, desc: String, price: String, quantity: String): Boolean {
        if (name.isEmpty()) {
            showToast("Please enter product name")
            return false
        }
        if (desc.isEmpty()) {
            showToast("Please enter description")
            return false
        }
        if (price.isEmpty() || price.toDoubleOrNull() == null || price.toDouble() <= 0) {
            showToast("Please enter valid price")
            return false
        }
        if (quantity.isEmpty() || quantity.toDoubleOrNull() == null || quantity.toDouble() <= 0) {
            showToast("Please enter valid quantity")
            return false
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}