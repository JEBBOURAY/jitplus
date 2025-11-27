package com.jitplus.merchant.ui.customer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jitplus.merchant.R
import com.jitplus.merchant.api.ApiClient
import com.jitplus.merchant.api.services.CustomerService
import com.jitplus.merchant.api.services.LoyaltyService
import com.jitplus.merchant.data.model.Customer
import com.jitplus.merchant.data.model.LoyaltyCard
import com.jitplus.merchant.ui.loyalty.LoyaltyCardActivity
import com.jitplus.merchant.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddCustomerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_customer)

        val phoneInput = findViewById<EditText>(R.id.cust_phone)
        val nameInput = findViewById<EditText>(R.id.cust_name)
        val emailInput = findViewById<EditText>(R.id.cust_email)
        val consentCheck = findViewById<CheckBox>(R.id.cb_consent)
        val createButton = findViewById<Button>(R.id.btn_create_customer)

        val tokenManager = TokenManager(this)
        val merchantId = tokenManager.getUsername()

        if (merchantId == null) {
            finish()
            return
        }

        val customerService = ApiClient.getClient(this).create(CustomerService::class.java)
        val loyaltyService = ApiClient.getClient(this).create(LoyaltyService::class.java)

        createButton.setOnClickListener {
            val phone = phoneInput.text.toString()
            val name = nameInput.text.toString()
            val email = emailInput.text.toString()
            val consent = consentCheck.isChecked

            if (phone.isEmpty()) {
                Toast.makeText(this, "Numéro de téléphone obligatoire", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!consent) {
                Toast.makeText(this, "Le consentement est obligatoire", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val customer = Customer(phoneNumber = phone, name = name, email = email, consent = consent)

            // 1. Create Customer
            customerService.registerCustomer(customer).enqueue(object : Callback<Customer> {
                override fun onResponse(call: Call<Customer>, response: Response<Customer>) {
                    if (response.isSuccessful && response.body() != null) {
                        val createdCustomer = response.body()!!
                        
                        // 2. Create Loyalty Card
                        createLoyaltyCard(loyaltyService, merchantId, createdCustomer.id!!)
                    } else {
                        Toast.makeText(this@AddCustomerActivity, "Erreur création client", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Customer>, t: Throwable) {
                    Toast.makeText(this@AddCustomerActivity, "Erreur réseau: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun createLoyaltyCard(loyaltyService: LoyaltyService, merchantId: String, customerId: Long) {
        loyaltyService.createCard(merchantId, customerId).enqueue(object : Callback<LoyaltyCard> {
            override fun onResponse(call: Call<LoyaltyCard>, response: Response<LoyaltyCard>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AddCustomerActivity, "Client inscrit au programme !", Toast.LENGTH_SHORT).show()
                    
                    // Navigate to Loyalty Card View
                    val intent = Intent(this@AddCustomerActivity, LoyaltyCardActivity::class.java)
                    intent.putExtra("CUSTOMER_ID", customerId)
                    intent.putExtra("MERCHANT_ID", merchantId)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@AddCustomerActivity, "Erreur création carte", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoyaltyCard>, t: Throwable) {
                Toast.makeText(this@AddCustomerActivity, "Erreur réseau carte: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
