package com.jitplus.merchant.ui.loyalty

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jitplus.merchant.R
import com.jitplus.merchant.api.ApiClient
import com.jitplus.merchant.api.services.LoyaltyService
import com.jitplus.merchant.data.model.LoyaltyProgram
import com.jitplus.merchant.utils.ErrorHandler
import com.jitplus.merchant.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConfigureProgramActivity : AppCompatActivity() {
    
    private lateinit var nameInput: EditText
    private lateinit var valueInput: EditText
    private lateinit var thresholdInput: EditText
    private lateinit var rewardInput: EditText
    private lateinit var saveButton: Button
    private lateinit var progressBar: ProgressBar
    private var programCall: Call<LoyaltyProgram>? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configure_program)

        nameInput = findViewById(R.id.prog_name)
        val typeGroup = findViewById<RadioGroup>(R.id.rg_type)
        valueInput = findViewById(R.id.prog_value)
        thresholdInput = findViewById(R.id.prog_threshold)
        rewardInput = findViewById(R.id.prog_reward)
        saveButton = findViewById(R.id.btn_save_program)
        progressBar = findViewById(R.id.progress_bar)

        val tokenManager = TokenManager(this)
        val username = tokenManager.getUsername()

        if (username == null) {
            Toast.makeText(this, "Session expirée. Reconnectez-vous", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val loyaltyService = ApiClient.getClient(this).create(LoyaltyService::class.java)

        saveButton.setOnClickListener {
            attemptSaveProgram(loyaltyService, username, typeGroup)
        }
    }
    
    private fun attemptSaveProgram(loyaltyService: LoyaltyService, username: String, typeGroup: RadioGroup) {
        val name = nameInput.text.toString().trim()
        val isStamps = findViewById<RadioButton>(R.id.rb_stamps).isChecked
        val type = if (isStamps) "STAMPS" else "POINTS"
        val valueStr = valueInput.text.toString().trim()
        val thresholdStr = thresholdInput.text.toString().trim()
        val reward = rewardInput.text.toString().trim()

        // Validation
        if (name.isEmpty() || valueStr.isEmpty() || thresholdStr.isEmpty() || reward.isEmpty()) {
            showError("Veuillez remplir tous les champs")
            return
        }

        val value = valueStr.toIntOrNull()
        val threshold = thresholdStr.toIntOrNull()

        if (value == null || value <= 0) {
            showError("Les ${if (isStamps) "tampons" else "points"} par passage doivent être un nombre positif")
            return
        }

        if (threshold == null || threshold <= 0) {
            showError("L'objectif doit être un nombre positif")
            return
        }

        if (threshold <= value) {
            showError("L'objectif doit être supérieur aux ${if (isStamps) "tampons" else "points"} par passage")
            return
        }

        showLoading(true)

        val program = LoyaltyProgram(
            merchantId = username,
            name = name,
            type = type,
            pointsPerVisit = value,
            threshold = threshold,
            rewardDescription = reward
        )

        programCall = loyaltyService.createProgram(program)
        programCall?.enqueue(object : Callback<LoyaltyProgram> {
            override fun onResponse(call: Call<LoyaltyProgram>, response: Response<LoyaltyProgram>) {
                if (isDestroyed || isFinishing) return
                
                showLoading(false)
                
                when {
                    response.isSuccessful -> {
                        Toast.makeText(this@ConfigureProgramActivity, "Programme enregistré avec succès !", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    else -> {
                        val errorMsg = ErrorHandler.getHttpErrorMessage(response.code())
                        ErrorHandler.logError("ConfigureProgramActivity", "Program save failed: ${response.code()}")
                        showError(errorMsg)
                    }
                }
            }

            override fun onFailure(call: Call<LoyaltyProgram>, t: Throwable) {
                if (isDestroyed || isFinishing) return
                
                showLoading(false)
                val errorMsg = ErrorHandler.getNetworkErrorMessage(t)
                ErrorHandler.logError("ConfigureProgramActivity", "Network error", t)
                showError(errorMsg)
            }
        })
    }
    
    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        saveButton.isEnabled = !show
        nameInput.isEnabled = !show
        valueInput.isEnabled = !show
        thresholdInput.isEnabled = !show
        rewardInput.isEnabled = !show
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        programCall?.cancel()
    }
}
