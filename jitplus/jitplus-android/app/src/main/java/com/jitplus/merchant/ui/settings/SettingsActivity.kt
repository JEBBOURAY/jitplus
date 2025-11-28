package com.jitplus.merchant.ui.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.jitplus.merchant.R
import com.jitplus.merchant.databinding.ActivitySettingsBinding
import com.jitplus.merchant.ui.login.LoginActivity
import com.jitplus.merchant.ui.loyalty.ConfigureProgramActivity
import com.jitplus.merchant.utils.SettingsManager
import com.jitplus.merchant.utils.TokenManager

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var tokenManager: TokenManager
    private lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)
        settingsManager = SettingsManager(this)

        // Display Merchant ID/Username
        val username = tokenManager.getUsername()
        binding.tvMerchantId.text = username ?: getString(R.string.default_merchant_name)

        // Config Program Button
        binding.btnConfigProgramSettings.setOnClickListener {
            startActivity(Intent(this, ConfigureProgramActivity::class.java))
        }

        // Store Info Button
        binding.btnStoreInfo.setOnClickListener {
            startActivity(Intent(this, StoreInfoActivity::class.java))
        }

        // Notifications Switch
        binding.switchNotifications.isChecked = settingsManager.areNotificationsEnabled()
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.setNotificationsEnabled(isChecked)
            val msg = if (isChecked) getString(R.string.notifications_enabled) else getString(R.string.notifications_disabled)
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        // Dark Mode Button
        binding.btnDarkMode.setOnClickListener {
            toggleDarkMode()
        }

        // Logout Button
        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun toggleDarkMode() {
        val isDark = settingsManager.isDarkModeEnabled()
        val newMode = !isDark
        settingsManager.setDarkModeEnabled(newMode)
        
        val mode = if (newMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
        
        recreate() // Restart activity to apply theme
    }

    private fun logout() {
        tokenManager.clearToken()
        Toast.makeText(this, getString(R.string.logout_success), Toast.LENGTH_SHORT).show()
        
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
