package com.jitplus.merchant.ui.customer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jitplus.merchant.R
import com.jitplus.merchant.api.ApiClient
import com.jitplus.merchant.api.services.CustomerService
import com.jitplus.merchant.api.services.LoyaltyService
import com.jitplus.merchant.data.model.Customer
import com.jitplus.merchant.data.model.LoyaltyCard
import com.jitplus.merchant.ui.loyalty.LoyaltyCardActivity
import com.jitplus.merchant.utils.ErrorHandler
import com.jitplus.merchant.utils.TokenManager
import com.jitplus.merchant.utils.ValidationUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddCustomerActivity : AppCompatActivity() {
    
    private lateinit var phoneInput: EditText
    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var consentCheck: CheckBox
    private lateinit var createButton: Button
    private lateinit var progressBar: ProgressBar
    private var customerCall: Call<Customer>? = null
    private var cardCall: Call<LoyaltyCard>? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_customer)

        phoneInput = findViewById(R.id.cust_phone)
        nameInput = findViewById(R.id.cust_name)
        emailInput = findViewById(R.id.cust_email)
        consentCheck = findViewById(R.id.cb_consent)
        createButton = findViewById(R.id.btn_create_customer)
        progressBar = findViewById(R.id.progress_bar)

        val tokenManager = TokenManager(this)
        val merchantId = tokenManager.getUsername()

        if (merchantId == null) {
            Toast.makeText(this, "Session expirée. Reconnectez-vous", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val customerService = ApiClient.getClient(this).create(CustomerService::class.java)
        val loyaltyService = ApiClient.getClient(this).create(LoyaltyService::class.java)

        createButton.setOnClickListener {
            attemptCreateCustomer(customerService, loyaltyService, merchantId)
        }
    }
    
    private fun attemptCreateCustomer(customerService: CustomerService, loyaltyService: LoyaltyService, merchantId: String) {
        val phone = phoneInput.text.toString().trim()
        val name = nameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val consent = consentCheck.isChecked

        // Validation
        if (phone.isEmpty()) {
            showError("Numéro de téléphone obligatoire")
            return
        }

        if (!ValidationUtils.isValidPhoneNumber(phone)) {
            showError("Format de téléphone invalide. Utilisez le format français (ex: 0612345678 ou +33612345678)")
            return
        }

        if (email.isNotEmpty() && !ValidationUtils.isValidEmail(email)) {
            showError("Format email invalide")
            return
        }

        if (!consent) {
            showError("Le consentement est obligatoire pour créer une carte de fidélité")
            return
        }

        showLoading(true)
        val customer = Customer(phoneNumber = phone, name = name.ifEmpty { null }, email = email.ifEmpty { null }, consent = consent)

        customerCall = customerService.registerCustomer(customer)
        customerCall?.enqueue(object : Callback<Customer> {
            override fun onResponse(call: Call<Customer>, response: Response<Customer>) {
                if (isDestroyed || isFinishing) return
                
                when {
                    response.isSuccessful && response.body() != null -> {
                        val createdCustomer = response.body()!!
                        createLoyaltyCard(loyaltyService, merchantId, createdCustomer.id!!)
                    }
                    else -> {
                        showLoading(false)
                        val errorMsg = ErrorHandler.getHttpErrorMessage(response.code())
                        ErrorHandler.logError("AddCustomerActivity", "Customer creation failed: ${response.code()}")
                        showError(errorMsg)
                    }
                }
            }

            override fun onFailure(call: Call<Customer>, t: Throwable) {
                if (isDestroyed || isFinishing) return
                
                showLoading(false)
                val errorMsg = ErrorHandler.getNetworkErrorMessage(t)
                ErrorHandler.logError("AddCustomerActivity", "Network error", t)
                showError(errorMsg)
            }
        })
    }

    private fun createLoyaltyCard(loyaltyService: LoyaltyService, merchantId: String, customerId: Long) {
        cardCall = loyaltyService.createCard(merchantId, customerId)
        cardCall?.enqueue(object : Callback<LoyaltyCard> {
            override fun onResponse(call: Call<LoyaltyCard>, response: Response<LoyaltyCard>) {
                if (isDestroyed || isFinishing) return
                
                showLoading(false)
                
                when {
                    response.isSuccessful -> {
                        Toast.makeText(this@AddCustomerActivity, "Client inscrit avec succès !", Toast.LENGTH_SHORT).show()
                        
                        val intent = Intent(this@AddCustomerActivity, LoyaltyCardActivity::class.java)
                        intent.putExtra("CUSTOMER_ID", customerId)
                        intent.putExtra("MERCHANT_ID", merchantId)
                        startActivity(intent)
                        finish()
                    }
                    else -> {
                        val errorMsg = ErrorHandler.getHttpErrorMessage(response.code())
                        ErrorHandler.logError("AddCustomerActivity", "Card creation failed: ${response.code()}")
                        showError("Client créé mais erreur lors de la création de la carte: $errorMsg")
                    }
                }
            }

            override fun onFailure(call: Call<LoyaltyCard>, t: Throwable) {
                if (isDestroyed || isFinishing) return
                
                showLoading(false)
                val errorMsg = ErrorHandler.getNetworkErrorMessage(t)
                ErrorHandler.logError("AddCustomerActivity", "Network error on card creation", t)
                showError("Client créé mais erreur réseau: $errorMsg")
            }
        })
    }
    
    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        createButton.isEnabled = !show
        phoneInput.isEnabled = !show
        nameInput.isEnabled = !show
        emailInput.isEnabled = !show
        consentCheck.isEnabled = !show
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        customerCall?.cancel()
        cardCall?.cancel()
    }
}
