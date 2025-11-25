package com.example.directfarm.app.ui.auth


import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.directfarm.app.databinding.ActivityLoginBinding
import com.example.directfarm.app.data.local.PreferencesManager
import com.example.directfarm.app.ui.customer.CustomerDashboardActivity
import com.example.directfarm.app.ui.farmer.FarmerDashboardActivity
import com.example.directfarm.app.utils.*

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var prefsManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsManager = PreferencesManager(this)

        setupObservers()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                viewModel.login(email, password)
            }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun setupObservers() {
        viewModel.loginState.observe(this) { result ->
            binding.progressBar.gone()
            binding.btnLogin.isEnabled = true

            result.onSuccess { user ->
                prefsManager.saveUser(user)
                navigateToDashboard(user.isFarmer())
            }.onFailure { error ->
                showToast(error.message ?: "Login failed")
            }
        }

        viewModel.loading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar.visible()
                binding.btnLogin.isEnabled = false
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty() || !email.isValidEmail()) {
            showToast("Please enter a valid email")
            return false
        }
        if (password.isEmpty() || password.length < Constants.MIN_PASSWORD_LENGTH) {
            showToast("Password must be at least 6 characters")
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