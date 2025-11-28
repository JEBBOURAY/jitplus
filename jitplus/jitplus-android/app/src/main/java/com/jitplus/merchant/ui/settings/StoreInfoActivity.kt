package com.jitplus.merchant.ui.settings

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jitplus.merchant.R
import com.jitplus.merchant.databinding.ActivityStoreInfoBinding
import com.jitplus.merchant.utils.SettingsManager

class StoreInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoreInfoBinding
    private lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingsManager = SettingsManager(this)

        // Load saved data
        binding.etStoreName.setText(settingsManager.getStoreName() ?: "")
        binding.etStoreAddress.setText(settingsManager.getStoreAddress() ?: "")
        binding.etStorePhone.setText(settingsManager.getStorePhone() ?: "")

        binding.btnSaveInfo.setOnClickListener {
            saveInfo()
        }
    }

    private fun saveInfo() {
        val name = binding.etStoreName.text.toString().trim()
        val address = binding.etStoreAddress.text.toString().trim()
        val phone = binding.etStorePhone.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, getString(R.string.store_name_required), Toast.LENGTH_SHORT).show()
            return
        }

        settingsManager.setStoreInfo(name, address, phone)
        Toast.makeText(this, getString(R.string.info_saved), Toast.LENGTH_SHORT).show()
        finish()
    }
}
