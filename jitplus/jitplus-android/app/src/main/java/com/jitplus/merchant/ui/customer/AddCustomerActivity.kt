package com.jitplus.merchant.ui.customer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.jitplus.merchant.R
import com.jitplus.merchant.data.model.Customer
import com.jitplus.merchant.databinding.ActivityAddCustomerBinding
import com.jitplus.merchant.utils.TokenManager
import com.jitplus.merchant.utils.ValidationUtils

class AddCustomerActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAddCustomerBinding
    private val viewModel: CustomerViewModel by viewModels()
    private var merchantId: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeServices()
        checkIntentData()
        setupObservers()
        
        binding.btnCreateCustomer.setOnClickListener { attemptCreateCustomer() }
    }
    
    private fun checkIntentData() {
        val phone = intent.getStringExtra("PHONE_NUMBER")
        if (!phone.isNullOrEmpty()) {
            binding.custPhone.setText(phone)
        }
    }
    
    private fun initializeServices() {
        val tokenManager = TokenManager(this)
        merchantId = tokenManager.getUsername()

        if (merchantId == null) {
            showError(getString(R.string.session_expired))
            finish()
            return
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.errorMessage.observe(this) { error ->
            if (error != null) {
                showError(error)
            }
        }

        viewModel.customerCreated.observe(this) { customer ->
            if (customer != null) {
                val phone = binding.custPhone.text.toString().trim()
                val name = binding.custName.text.toString().trim()
                viewModel.createCard(merchantId!!, customer.id!!, name.ifEmpty { null }, phone)
            }
        }

        viewModel.cardCreated.observe(this) { card ->
            if (card != null) {
                showSuccess(getString(R.string.customer_created_success))
                val phone = binding.custPhone.text.toString().trim()
                navigateToQRCode(card.customerId, phone)
            }
        }
    }
    
    private fun attemptCreateCustomer() {
        val phone = binding.custPhone.text.toString().trim()
        val name = binding.custName.text.toString().trim()
        val email = binding.custEmail.text.toString().trim()
        val consent = binding.cbConsent.isChecked

        if (!validateInputs(phone, email, consent)) return

        val customer = Customer(
            phoneNumber = phone, 
            name = name.ifEmpty { null }, 
            email = email.ifEmpty { null }, 
            consent = consent
        )

        viewModel.registerCustomer(customer)
    }
    
    private fun validateInputs(phone: String, email: String, consent: Boolean): Boolean {
        return when {
            phone.isEmpty() -> {
                showError(getString(R.string.phone_required))
                false
            }
            !ValidationUtils.isValidPhoneNumber(phone) -> {
                showError(getString(R.string.invalid_phone_format))
                false
            }
            email.isNotEmpty() && !ValidationUtils.isValidEmail(email) -> {
                showError(getString(R.string.invalid_email))
                false
            }
            !consent -> {
                showError(getString(R.string.consent_required))
                false
            }
            else -> true
        }
    }
    
    private fun navigateToQRCode(customerId: Long, phoneNumber: String) {
        val intent = Intent(this, QRCodeActivity::class.java).apply {
            putExtra("CUSTOMER_ID", customerId)
            putExtra("MERCHANT_ID", merchantId)
            putExtra("PHONE_NUMBER", phoneNumber)
        }
        startActivity(intent)
        finish()
    }
    
    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnCreateCustomer.isEnabled = !show
        binding.custPhone.isEnabled = !show
        binding.custName.isEnabled = !show
        binding.custEmail.isEnabled = !show
        binding.cbConsent.isEnabled = !show
    }
    
    private fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
