package com.jitplus.merchant.ui.customer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

class CustomerSearchActivity : AppCompatActivity() {
    
    private lateinit var phoneInput: EditText
    private lateinit var searchButton: Button
    private lateinit var progressBar: ProgressBar
    private var searchCall: Call<Customer>? = null
    private var cardCall: Call<LoyaltyCard>? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_search)

        phoneInput = findViewById(R.id.search_phone)
        searchButton = findViewById(R.id.btn_perform_search)
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

        searchButton.setOnClickListener {
            performSearch(customerService, loyaltyService, merchantId)
        }
    }
    
    private fun performSearch(customerService: CustomerService, loyaltyService: LoyaltyService, merchantId: String) {
        val phone = phoneInput.text.toString().trim()
        
        if (phone.isEmpty()) {
            showError("Veuillez entrer un numéro de téléphone")
            return
        }

        if (!ValidationUtils.isValidPhoneNumber(phone)) {
            showError("Format de téléphone invalide. Utilisez le format français (ex: 0612345678)")
            return
        }

        showLoading(true)

        searchCall = customerService.getCustomerByPhone(phone)
        searchCall?.enqueue(object : Callback<Customer> {
            override fun onResponse(call: Call<Customer>, response: Response<Customer>) {
                if (isDestroyed || isFinishing) return
                
                when {
                    response.isSuccessful && response.body() != null -> {
                        val customer = response.body()!!
                        checkLoyaltyCard(loyaltyService, merchantId, customer.id!!)
                    }
                    response.code() == 404 -> {
                        showLoading(false)
                        showNotFoundDialog(phone)
                    }
                    else -> {
                        showLoading(false)
                        val errorMsg = ErrorHandler.getHttpErrorMessage(response.code())
                        ErrorHandler.logError("CustomerSearchActivity", "Search failed: ${response.code()}")
                        showError(errorMsg)
                    }
                }
            }

            override fun onFailure(call: Call<Customer>, t: Throwable) {
                if (isDestroyed || isFinishing) return
                
                showLoading(false)
                val errorMsg = ErrorHandler.getNetworkErrorMessage(t)
                ErrorHandler.logError("CustomerSearchActivity", "Network error", t)
                showError(errorMsg)
            }
        })
    }

    private fun checkLoyaltyCard(loyaltyService: LoyaltyService, merchantId: String, customerId: Long) {
        cardCall = loyaltyService.createCard(merchantId, customerId)
        cardCall?.enqueue(object : Callback<LoyaltyCard> {
            override fun onResponse(call: Call<LoyaltyCard>, response: Response<LoyaltyCard>) {
                if (isDestroyed || isFinishing) return
                
                showLoading(false)
                
                when {
                    response.isSuccessful -> {
                        val intent = Intent(this@CustomerSearchActivity, LoyaltyCardActivity::class.java)
                        intent.putExtra("CUSTOMER_ID", customerId)
                        intent.putExtra("MERCHANT_ID", merchantId)
                        startActivity(intent)
                        finish()
                    }
                    else -> {
                        val errorMsg = ErrorHandler.getHttpErrorMessage(response.code())
                        ErrorHandler.logError("CustomerSearchActivity", "Card retrieval failed: ${response.code()}")
                        showError("Erreur récupération carte: $errorMsg")
                    }
                }
            }

            override fun onFailure(call: Call<LoyaltyCard>, t: Throwable) {
                if (isDestroyed || isFinishing) return
                
                showLoading(false)
                val errorMsg = ErrorHandler.getNetworkErrorMessage(t)
                ErrorHandler.logError("CustomerSearchActivity", "Network error on card", t)
                showError(errorMsg)
            }
        })
    }

    private fun showNotFoundDialog(phone: String) {
        if (isDestroyed || isFinishing) return
        
        AlertDialog.Builder(this)
            .setTitle("Client introuvable")
            .setMessage("Ce numéro n'est pas encore enregistré dans le système. Souhaitez-vous créer un nouveau client ?")
            .setPositiveButton("Créer") { _, _ ->
                val intent = Intent(this, AddCustomerActivity::class.java)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Annuler", null)
            .show()
    }
    
    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        searchButton.isEnabled = !show
        phoneInput.isEnabled = !show
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        searchCall?.cancel()
        cardCall?.cancel()
    }
}
