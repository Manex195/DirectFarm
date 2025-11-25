package com.example.directfarm.app.ui.common

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.directfarm.app.R
import com.example.directfarm.app.data.local.PreferencesManager
import com.example.directfarm.app.ui.auth.LoginActivity
import com.example.directfarm.app.ui.customer.CustomerDashboardActivity
import com.example.directfarm.app.ui.farmer.FarmerDashboardActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var prefsManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        prefsManager = PreferencesManager(this)

        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, 2000) // 2 seconds splash
    }

    private fun navigateToNextScreen() {
        val intent = if (prefsManager.isLoggedIn()) {
            val user = prefsManager.getUser()
            if (user?.isFarmer() == true) {
                Intent(this, FarmerDashboardActivity::class.java)
            } else {
                Intent(this, CustomerDashboardActivity::class.java)
            }
        } else {
            Intent(this, LoginActivity::class.java)
        }

        startActivity(intent)
        finish()
    }
}