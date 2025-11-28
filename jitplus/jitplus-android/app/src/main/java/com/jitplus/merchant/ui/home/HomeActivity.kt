package com.jitplus.merchant.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jitplus.merchant.databinding.ActivityHomeBinding
import com.jitplus.merchant.ui.dashboard.DashboardActivity
import com.jitplus.merchant.ui.loyalty.ConfigureProgramActivity
import com.jitplus.merchant.utils.SettingsManager
import com.jitplus.merchant.utils.TokenManager

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateMerchantName()

        binding.btnConfigProgram.setOnClickListener {
            startActivity(Intent(this, ConfigureProgramActivity::class.java))
        }

        binding.btnAddCustomer.setOnClickListener {
            startActivity(Intent(this, com.jitplus.merchant.ui.customer.AddCustomerActivity::class.java))
        }

        binding.btnSearchCustomer.setOnClickListener {
            startActivity(Intent(this, com.jitplus.merchant.ui.customer.CustomerSearchActivity::class.java))
        }

        binding.btnDashboard.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, com.jitplus.merchant.ui.settings.SettingsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        updateMerchantName()
    }

    private fun updateMerchantName() {
        val tokenManager = TokenManager(this)
        val settingsManager = SettingsManager(this)
        
        val storeName = settingsManager.getStoreName()
        val username = tokenManager.getUsername()
        
        val displayName = if (!storeName.isNullOrEmpty()) storeName else (username ?: getString(com.jitplus.merchant.R.string.default_merchant_name))
        binding.tvMerchantName.text = displayName
    }
}
