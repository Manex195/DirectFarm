package com.example.directfarm.app.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.directfarm.app.databinding.ActivityRegisterBinding
import com.example.directfarm.app.data.model.User
import com.example.directfarm.app.data.model.UserType
import com.example.directfarm.app.data.local.PreferencesManager
import com.example.directfarm.app.ui.customer.CustomerDashboardActivity
import com.example.directfarm.app.ui.farmer.FarmerDashboardActivity
import com.example.directfarm.app.utils.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var prefsManager: PreferencesManager
    private var selectedUserType = UserType.CUSTOMER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsManager = PreferencesManager(this)

        setupObservers()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.rgUserType.setOnCheckedChangeListener { _, checkedId ->
            selectedUserType = if (checkedId == binding.rbFarmer.id) {
                UserType.FARMER
            } else {
                UserType.CUSTOMER
            }
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()
            val fullName = binding.etFullName.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val location = binding.etLocation.text.toString().trim()

            if (validateInput(email, password, confirmPassword, fullName, phone, location)) {
                val user = User(
                    email = email,
                    fullName = fullName,
                    phoneNumber = phone,
                    location = location,
                    userType = selectedUserType.name
                )
                viewModel.register(email, password, user)
            }
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun setupObservers() {
        viewModel.registerState.observe(this) { result ->
            binding.progressBar.gone()
            binding.btnRegister.isEnabled = true

            result.onSuccess { user ->
                prefsManager.saveUser(user)
                navigateToDashboard(user.isFarmer())
            }.onFailure { error ->
                showToast(error.message ?: "Registration failed")
            }
        }

        viewModel.loading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar.visible()
                binding.btnRegister.isEnabled = false
            }
        }
    }

    private fun validateInput(
        email: String, password: String, confirmPassword: String,
        fullName: String, phone: String, location: String
    ): Boolean {
        if (fullName.isEmpty()) {
            showToast("Please enter your full name")
            return false
        }
        if (email.isEmpty() || !email.isValidEmail()) {
            showToast("Please enter a valid email")
            return false
        }
        if (phone.isEmpty() || !phone.isValidPhone()) {
            showToast("Please enter a valid phone number")
            return false
        }
        if (location.isEmpty()) {
            showToast("Please enter your location")
            return false
        }
        if (password.length < Constants.MIN_PASSWORD_LENGTH) {
            showToast("Password must be at least 6 characters")
            return false
        }
        if (password != confirmPassword) {
            showToast("Passwords do not match")
            return false
        }
        return true
    }

    private fun navigateToDashboard(isFarmer: Boolean) {
        val intent = if (isFarmer) {
            Intent(this, FarmerDashboardActivity::class.java)
        } else {
            Intent(this, CustomerDashboardActivity::class.java)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}