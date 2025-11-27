package com.jitplus.merchant.ui.login

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jitplus.merchant.R
import com.jitplus.merchant.api.ApiClient
import com.jitplus.merchant.api.services.AuthService
import com.jitplus.merchant.data.model.RegisterRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val emailInput = findViewById<EditText>(R.id.reg_email)
        val passwordInput = findViewById<EditText>(R.id.reg_password)
        val shopNameInput = findViewById<EditText>(R.id.reg_shop_name)
        val cityInput = findViewById<EditText>(R.id.reg_city)
        val addressInput = findViewById<EditText>(R.id.reg_address)
        val registerButton = findViewById<Button>(R.id.btn_register)

        val authService = ApiClient.getClient(this).create(AuthService::class.java)

        registerButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            val shopName = shopNameInput.text.toString()
            val city = cityInput.text.toString()
            val address = addressInput.text.toString()

            if (email.isEmpty() || password.isEmpty() || shopName.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir les champs obligatoires", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = RegisterRequest(email, password, shopName, city, address)

            authService.register(request).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@RegisterActivity, "Inscription réussie !", Toast.LENGTH_SHORT).show()
                        finish() // Go back to Login
                    } else {
                        Toast.makeText(this@RegisterActivity, "Erreur lors de l'inscription", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, "Erreur réseau: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
