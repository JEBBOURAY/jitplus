package com.jitplus.merchant.ui.loyalty

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jitplus.merchant.R
import com.jitplus.merchant.api.ApiClient
import com.jitplus.merchant.api.services.LoyaltyService
import com.jitplus.merchant.data.model.LoyaltyProgram
import com.jitplus.merchant.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConfigureProgramActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configure_program)

        val nameInput = findViewById<EditText>(R.id.prog_name)
        val typeGroup = findViewById<RadioGroup>(R.id.rg_type)
        val valueInput = findViewById<EditText>(R.id.prog_value)
        val thresholdInput = findViewById<EditText>(R.id.prog_threshold)
        val rewardInput = findViewById<EditText>(R.id.prog_reward)
        val saveButton = findViewById<Button>(R.id.btn_save_program)

        val tokenManager = TokenManager(this)
        val username = tokenManager.getUsername()

        if (username == null) {
            Toast.makeText(this, "Erreur utilisateur non connecté", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val loyaltyService = ApiClient.getClient(this).create(LoyaltyService::class.java)

        // Pre-fill if exists (Optional for MVP, but good UX)
        // For now, let's just allow creating/overwriting

        saveButton.setOnClickListener {
            val name = nameInput.text.toString()
            val isStamps = findViewById<RadioButton>(R.id.rb_stamps).isChecked
            val type = if (isStamps) "STAMPS" else "POINTS"
            val valueStr = valueInput.text.toString()
            val thresholdStr = thresholdInput.text.toString()
            val reward = rewardInput.text.toString()

            if (name.isEmpty() || valueStr.isEmpty() || thresholdStr.isEmpty() || reward.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val program = LoyaltyProgram(
                merchantId = username,
                name = name,
                type = type,
                pointsPerVisit = valueStr.toInt(),
                threshold = thresholdStr.toInt(),
                rewardDescription = reward
            )

            loyaltyService.createProgram(program).enqueue(object : Callback<LoyaltyProgram> {
                override fun onResponse(call: Call<LoyaltyProgram>, response: Response<LoyaltyProgram>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ConfigureProgramActivity, "Programme enregistré !", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@ConfigureProgramActivity, "Erreur: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoyaltyProgram>, t: Throwable) {
                    Toast.makeText(this@ConfigureProgramActivity, "Erreur réseau: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
