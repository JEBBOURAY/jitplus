package com.jitplus.merchant.ui.login

import android.content.Intent
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
import com.jitplus.merchant.data.model.LoginRequest
import com.jitplus.merchant.ui.home.HomeActivity
import com.jitplus.merchant.utils.ErrorHandler
import com.jitplus.merchant.utils.TokenManager
import com.jitplus.merchant.utils.ValidationUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar
    private var loginCall: Call<String>? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        usernameInput = findViewById(R.id.username)
        passwordInput = findViewById(R.id.password)
        loginButton = findViewById(R.id.login_button)
        progressBar = findViewById(R.id.progress_bar)
        val registerLink = findViewById<android.widget.TextView>(R.id.tv_register_link)

        // Check if already logged in
        val tokenManager = TokenManager(this)
        if (tokenManager.getToken() != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        val authService = ApiClient.getClient(this).create(AuthService::class.java)

        registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        loginButton.setOnClickListener {
            attemptLogin(authService, tokenManager)
        }
    }
    
    private fun attemptLogin(authService: AuthService, tokenManager: TokenManager) {
        val username = usernameInput.text.toString().trim()
        val password = passwordInput.text.toString()

        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs")
            return
        }

        if (!ValidationUtils.isValidEmail(username) && !ValidationUtils.isValidPhoneNumber(username)) {
            showError("Format email ou téléphone invalide")
            return
        }

        val (isPasswordValid, passwordError) = ValidationUtils.isValidPassword(password)
        if (!isPasswordValid) {
            showError(passwordError)
            return
        }

        showLoading(true)
        val request = LoginRequest(username, password)
        
        loginCall = authService.login(request)
        loginCall?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (isDestroyed || isFinishing) return
                
                showLoading(false)
                
                when {
                    response.isSuccessful -> {
                        val token = response.body()
                        if (token != null && token.isNotEmpty()) {
                            tokenManager.saveToken(token)
                            tokenManager.saveUsername(username)
                            Toast.makeText(this@LoginActivity, "Connexion réussie", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                            finish()
                        } else {
                            showError("Token invalide reçu du serveur")
                        }
                    }
                    else -> {
                        val errorMsg = ErrorHandler.getHttpErrorMessage(response.code())
                        ErrorHandler.logError("LoginActivity", "Login failed: ${response.code()} - ${response.message()}")
                        showError(errorMsg)
                    }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                if (isDestroyed || isFinishing) return
                
                showLoading(false)
                val errorMsg = ErrorHandler.getNetworkErrorMessage(t)
                ErrorHandler.logError("LoginActivity", "Network error", t)
                showError(errorMsg)
            }
        })
    }
    
    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        loginButton.isEnabled = !show
        usernameInput.isEnabled = !show
        passwordInput.isEnabled = !show
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        loginCall?.cancel()
    }
}
