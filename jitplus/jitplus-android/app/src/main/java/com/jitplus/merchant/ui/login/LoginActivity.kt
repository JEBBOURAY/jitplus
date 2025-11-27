package com.jitplus.merchant.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jitplus.merchant.R
import com.jitplus.merchant.api.ApiClient
import com.jitplus.merchant.api.services.AuthService
import com.jitplus.merchant.data.model.LoginRequest
import com.jitplus.merchant.ui.home.HomeActivity
import com.jitplus.merchant.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameInput = findViewById<EditText>(R.id.username)
        val passwordInput = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.login_button)
        val registerLink = findViewById<android.widget.TextView>(R.id.tv_register_link)

        // Check if already logged in
        val tokenManager = TokenManager(this)
        if (tokenManager.getToken() != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        val authService = ApiClient.getClient(this).create(AuthService::class.java)

        registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        loginButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = LoginRequest(username, password)
            
            authService.login(request).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        val token = response.body()
                        if (token != null) {
                            tokenManager.saveToken(token)
                            tokenManager.saveUsername(username)
                            Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                            finish()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
