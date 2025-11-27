package com.jitplus.merchant.ui.login

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jitplus.merchant.R
import com.jitplus.merchant.api.ApiClient
import com.jitplus.merchant.api.services.AuthService
import com.jitplus.merchant.data.model.RegisterRequest
import com.jitplus.merchant.utils.ErrorHandler
import com.jitplus.merchant.utils.ValidationUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var shopNameInput: EditText
    private lateinit var cityInput: EditText
    private lateinit var addressInput: EditText
    private lateinit var registerButton: Button
    private lateinit var progressBar: ProgressBar
    private var registerCall: Call<String>? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        emailInput = findViewById(R.id.reg_email)
        passwordInput = findViewById(R.id.reg_password)
        shopNameInput = findViewById(R.id.reg_shop_name)
        cityInput = findViewById(R.id.reg_city)
        addressInput = findViewById(R.id.reg_address)
        registerButton = findViewById(R.id.btn_register)
        progressBar = findViewById(R.id.progress_bar)

        val authService = ApiClient.getClient(this).create(AuthService::class.java)

        registerButton.setOnClickListener {
            attemptRegister(authService)
        }
    }
    
    private fun attemptRegister(authService: AuthService) {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()
        val shopName = shopNameInput.text.toString().trim()
        val city = cityInput.text.toString().trim()
        val address = addressInput.text.toString().trim()

        // Validation
        if (email.isEmpty() || password.isEmpty() || shopName.isEmpty()) {
            showError("Veuillez remplir tous les champs obligatoires (email, mot de passe, nom de boutique)")
            return
        }

        if (!ValidationUtils.isValidEmail(email)) {
            showError("Format email invalide")
            return
        }

        val (isPasswordValid, passwordError) = ValidationUtils.isValidPassword(password)
        if (!isPasswordValid) {
            showError(passwordError)
            return
        }

        if (!ValidationUtils.isValidShopName(shopName)) {
            showError("Le nom de la boutique doit contenir au moins 2 caractères")
            return
        }

        showLoading(true)
        val request = RegisterRequest(email, password, shopName, city, address)

        registerCall = authService.register(request)
        registerCall?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (isDestroyed || isFinishing) return
                
                showLoading(false)
                
                when {
                    response.isSuccessful -> {
                        Toast.makeText(this@RegisterActivity, "Inscription réussie ! Vous pouvez vous connecter", Toast.LENGTH_LONG).show()
                        finish()
                    }
                    else -> {
                        val errorMsg = ErrorHandler.getHttpErrorMessage(response.code())
                        ErrorHandler.logError("RegisterActivity", "Registration failed: ${response.code()} - ${response.message()}")
                        showError(errorMsg)
                    }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                if (isDestroyed || isFinishing) return
                
                showLoading(false)
                val errorMsg = ErrorHandler.getNetworkErrorMessage(t)
                ErrorHandler.logError("RegisterActivity", "Network error", t)
                showError(errorMsg)
            }
        })
    }
    
    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        registerButton.isEnabled = !show
        emailInput.isEnabled = !show
        passwordInput.isEnabled = !show
        shopNameInput.isEnabled = !show
        cityInput.isEnabled = !show
        addressInput.isEnabled = !show
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        registerCall?.cancel()
    }
}
