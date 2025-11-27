package com.jitplus.merchant

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jitplus.merchant.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Redirect to Login for MVP
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
