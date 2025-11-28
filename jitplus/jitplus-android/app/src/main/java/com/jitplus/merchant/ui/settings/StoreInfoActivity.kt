package com.jitplus.merchant.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.jitplus.merchant.R
import com.jitplus.merchant.data.model.StoreInfoRequest
import com.jitplus.merchant.databinding.ActivityStoreInfoBinding
import com.jitplus.merchant.ui.login.AuthViewModel
import com.jitplus.merchant.ui.loyalty.ConfigureProgramActivity
import com.jitplus.merchant.utils.TokenManager

class StoreInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoreInfoBinding
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)

        setupShopTypes()
        setupLanguageAndTimezone()
        setupObservers()

        binding.btnContinue.setOnClickListener {
            saveInfo()
        }
    }

    private fun setupShopTypes() {
        val shopTypes = listOf("Café", "Restaurant", "Snack", "Coiffeur", "Boulangerie", "Autre")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, shopTypes)
        binding.actvShopType.setAdapter(adapter)
    }

    private fun setupLanguageAndTimezone() {
        // Language
        val languages = listOf("Français", "Arabe (Bientôt disponible)")
        val langAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, languages)
        binding.actvLanguage.setAdapter(langAdapter)
        binding.actvLanguage.setText(languages[0], false) // Default to French

        binding.actvLanguage.setOnItemClickListener { _, _, position, _ ->
            if (position == 1) {
                Toast.makeText(this, "La langue Arabe sera bientôt disponible", Toast.LENGTH_SHORT).show()
                binding.actvLanguage.setText(languages[0], false)
            }
        }

        // Timezone
        val timezones = java.util.TimeZone.getAvailableIDs().toList().sorted()
        val tzAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, timezones)
        binding.actvTimezone.setAdapter(tzAdapter)
        
        // Default to system timezone or GMT
        val defaultTz = java.util.TimeZone.getDefault().id
        if (timezones.contains(defaultTz)) {
            binding.actvTimezone.setText(defaultTz, false)
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnContinue.isEnabled = !isLoading
        }

        viewModel.errorMessage.observe(this) { error ->
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.updateStoreSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Informations enregistrées", Toast.LENGTH_SHORT).show()
                // Navigate to Configure Program
                val intent = Intent(this, ConfigureProgramActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun saveInfo() {
        val shopName = binding.etShopName.text.toString().trim()
        val shopType = binding.actvShopType.text.toString().trim()
        val city = binding.etCity.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val website = binding.etWebsite.text.toString().trim()
        val instagram = binding.etInstagram.text.toString().trim()
        val language = binding.actvLanguage.text.toString().trim()
        val timezone = binding.actvTimezone.text.toString().trim()

        if (shopName.isEmpty()) {
            binding.tilShopName.error = "Le nom du commerce est requis"
            return
        } else {
            binding.tilShopName.error = null
        }

        if (shopType.isEmpty()) {
            binding.tilShopType.error = "Le type de commerce est requis"
            return
        } else {
            binding.tilShopType.error = null
        }

        if (city.isEmpty()) {
            binding.tilCity.error = "La ville est requise"
            return
        } else {
            binding.tilCity.error = null
        }

        val email = tokenManager.getUsername()
        if (email == null) {
            Toast.makeText(this, "Erreur: Utilisateur non identifié", Toast.LENGTH_SHORT).show()
            return
        }

        val request = StoreInfoRequest(
            email = email,
            shopName = shopName,
            shopType = shopType,
            city = city,
            address = if (address.isEmpty()) null else address,
            website = if (website.isEmpty()) null else website,
            instagram = if (instagram.isEmpty()) null else instagram,
            language = if (language.startsWith("Français")) "FR" else "FR",
            timezone = if (timezone.isEmpty()) null else timezone
        )

        viewModel.updateStoreInfo(request)
    }
}
