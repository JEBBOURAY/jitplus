package com.jitplus.merchant.ui.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.jitplus.merchant.R
import com.jitplus.merchant.data.model.RegisterRequest
import com.jitplus.merchant.databinding.ActivityRegisterBinding
import com.jitplus.merchant.utils.ValidationUtils

class RegisterActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()

        binding.btnRegister.setOnClickListener {
            attemptRegister()
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

        viewModel.registerSuccess.observe(this) { success ->
            if (success) {
                // Auto login after register
                val email = binding.regEmail.text.toString().trim()
                val password = binding.regPassword.text.toString()
                viewModel.login(com.jitplus.merchant.data.model.LoginRequest(email, password))
            }
        }

        viewModel.loginSuccess.observe(this) { success ->
            if (success) {
                // Navigate to Store Info
                val intent = android.content.Intent(this, com.jitplus.merchant.ui.settings.StoreInfoActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
    
    private fun attemptRegister() {
        val email = binding.regEmail.text.toString().trim()
        val password = binding.regPassword.text.toString()
        val confirmPassword = binding.regConfirmPassword.text.toString()
        val phone = binding.regPhone.text.toString().trim()

        // Validation
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError(getString(R.string.fill_required_fields))
            return
        }

        if (!ValidationUtils.isValidEmail(email)) {
            showError(getString(R.string.invalid_email))
            return
        }

        val (isPasswordValid, passwordError) = ValidationUtils.isValidPassword(password)
        if (!isPasswordValid) {
            showError(passwordError)
            return
        }

        if (password != confirmPassword) {
            showError("Les mots de passe ne correspondent pas")
            return
        }

        val request = RegisterRequest(email, password, phone)
        viewModel.register(request)
    }
    
    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !show
        binding.regEmail.isEnabled = !show
        binding.regPassword.isEnabled = !show
        binding.regConfirmPassword.isEnabled = !show
        binding.regPhone.isEnabled = !show
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
