package com.jitplus.merchant.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.jitplus.merchant.R
import com.jitplus.merchant.ui.loyalty.ConfigureProgramActivity

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        findViewById<Button>(R.id.btn_config_program).setOnClickListener {
            startActivity(Intent(this, ConfigureProgramActivity::class.java))
        }

        findViewById<Button>(R.id.btn_add_customer).setOnClickListener {
            startActivity(Intent(this, com.jitplus.merchant.ui.customer.AddCustomerActivity::class.java))
        }

        findViewById<Button>(R.id.btn_search_customer).setOnClickListener {
            startActivity(Intent(this, com.jitplus.merchant.ui.customer.CustomerSearchActivity::class.java))
        }
    }
}
