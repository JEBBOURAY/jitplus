package com.jitplus.merchant.ui.customer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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
import com.jitplus.merchant.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomerSearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_search)

        val phoneInput = findViewById<EditText>(R.id.search_phone)
        val searchButton = findViewById<Button>(R.id.btn_perform_search)

        val tokenManager = TokenManager(this)
        val merchantId = tokenManager.getUsername()

        if (merchantId == null) {
            finish()
            return
        }

        val customerService = ApiClient.getClient(this).create(CustomerService::class.java)
        val loyaltyService = ApiClient.getClient(this).create(LoyaltyService::class.java)

        searchButton.setOnClickListener {
            val phone = phoneInput.text.toString()
            if (phone.isEmpty()) {
                Toast.makeText(this, "Entrez un numéro", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            customerService.getCustomerByPhone(phone).enqueue(object : Callback<Customer> {
                override fun onResponse(call: Call<Customer>, response: Response<Customer>) {
                    if (response.isSuccessful && response.body() != null) {
                        val customer = response.body()!!
                        // Customer found, check for loyalty card
                        checkLoyaltyCard(loyaltyService, merchantId, customer.id!!)
                    } else {
                        // Customer not found
                        showNotFoundDialog(phone)
                    }
                }

                override fun onFailure(call: Call<Customer>, t: Throwable) {
                    Toast.makeText(this@CustomerSearchActivity, "Erreur réseau: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun checkLoyaltyCard(loyaltyService: LoyaltyService, merchantId: String, customerId: Long) {
        // We use createCard here because it acts as "get or create" in our backend logic
        // But to be semantically correct with the prompt "retrieve card", we could use getCard.
        // However, if the customer exists but never visited THIS shop, we want to create the card.
        // So createCard (which is createCardIfNotExists in backend) is the perfect call.
        loyaltyService.createCard(merchantId, customerId).enqueue(object : Callback<LoyaltyCard> {
            override fun onResponse(call: Call<LoyaltyCard>, response: Response<LoyaltyCard>) {
                if (response.isSuccessful) {
                    val intent = Intent(this@CustomerSearchActivity, LoyaltyCardActivity::class.java)
                    intent.putExtra("CUSTOMER_ID", customerId)
                    intent.putExtra("MERCHANT_ID", merchantId)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@CustomerSearchActivity, "Erreur récupération carte", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoyaltyCard>, t: Throwable) {
                Toast.makeText(this@CustomerSearchActivity, "Erreur réseau: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showNotFoundDialog(phone: String) {
        AlertDialog.Builder(this)
            .setTitle("Client introuvable")
            .setMessage("Ce numéro n'est pas enregistré. Voulez-vous créer un nouveau client ?")
            .setPositiveButton("Créer") { _, _ ->
                val intent = Intent(this, AddCustomerActivity::class.java)
                // Optional: Pass the phone number to pre-fill
                // intent.putExtra("PHONE", phone) 
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Annuler", null)
            .show()
    }
}
