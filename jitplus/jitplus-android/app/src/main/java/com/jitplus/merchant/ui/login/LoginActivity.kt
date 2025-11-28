package com.jitplus.merchant.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.jitplus.merchant.R
import com.jitplus.merchant.data.model.LoginRequest
import com.jitplus.merchant.databinding.ActivityLoginBinding
import com.jitplus.merchant.ui.home.HomeActivity
import com.jitplus.merchant.utils.TokenManager
import com.jitplus.merchant.utils.ValidationUtils

class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if already logged in
        val tokenManager = TokenManager(this)
        if (tokenManager.getToken() != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        setupObservers()

        binding.tvRegisterLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.loginButton.setOnClickListener {
            attemptLogin()
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

        viewModel.loginSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }
    }
    
    private fun attemptLogin() {
        val username = binding.username.text.toString().trim()
        val password = binding.password.text.toString()

        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            showError(getString(R.string.fill_all_fields))
            return
        }

        if (!ValidationUtils.isValidEmail(username) && !ValidationUtils.isValidPhoneNumber(username)) {
            showError(getString(R.string.invalid_email_phone))
            return
        }

        val (isPasswordValid, passwordError) = ValidationUtils.isValidPassword(password)
        if (!isPasswordValid) {
            showError(passwordError)
            return
        }

        val request = LoginRequest(username, password)
        viewModel.login(request)
    }
    
    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.loginButton.isEnabled = !show
        binding.username.isEnabled = !show
        binding.password.isEnabled = !show
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
