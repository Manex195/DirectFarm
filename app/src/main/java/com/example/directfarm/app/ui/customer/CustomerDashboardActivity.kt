package com.example.directfarm.app.ui.customer

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.directfarm.R
import com.example.directfarm.databinding.ActivityCustomerDashboardBinding
import com.example.directfarm.data.local.PreferencesManager
import com.example.directfarm.ui.auth.LoginActivity
import com.example.directfarm.utils.showToast

class CustomerDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomerDashboardBinding
    private val viewModel: CustomerViewModel by viewModels()
    private lateinit var prefsManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        prefsManager = PreferencesManager(this)

        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        val user = prefsManager.getUser()
        binding.tvWelcome.text = "Welcome, ${user?.fullName}"
    }

    private fun setupClickListeners() {
        binding.cardBrowseProducts.setOnClickListener {
            startActivity(Intent(this, ProductListActivity::class.java))
        }

        binding.cardMyOrders.setOnClickListener {
            startActivity(Intent(this, CustomerOrdersActivity::class.java))
        }
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