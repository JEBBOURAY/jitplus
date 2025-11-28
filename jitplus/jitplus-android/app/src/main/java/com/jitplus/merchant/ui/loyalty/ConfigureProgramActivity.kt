package com.jitplus.merchant.ui.loyalty

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.jitplus.merchant.R
import com.jitplus.merchant.data.model.LoyaltyProgram
import com.jitplus.merchant.databinding.ActivityConfigureProgramBinding
import com.jitplus.merchant.utils.TokenManager

class ConfigureProgramActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityConfigureProgramBinding
    private val viewModel: LoyaltyViewModel by viewModels()
    private var selectedType = "STAMPS"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigureProgramBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tokenManager = TokenManager(this)
        val username = tokenManager.getUsername()

        if (username == null) {
            Toast.makeText(this, getString(R.string.session_expired), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupObservers()
        setupSelectionUI()

        // Setup Happy Hour switch listener
        binding.switchHappyHour.setOnCheckedChangeListener { _, isChecked ->
            binding.layoutHappyHour.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        binding.btnSaveProgram.setOnClickListener {
            attemptSaveProgram(username)
        }
    }

    private fun setupSelectionUI() {
        binding.cardStamps.setOnClickListener { updateSelection("STAMPS") }
        binding.cardPoints.setOnClickListener { updateSelection("POINTS") }
        binding.cardProgressive.setOnClickListener { updateSelection("PROGRESSIVE") }
        
        // Initial state
        updateSelection("STAMPS")
    }

    private fun updateSelection(type: String) {
        selectedType = type
        
        // Reset all
        resetCard(binding.cardStamps)
        resetCard(binding.cardPoints)
        resetCard(binding.cardProgressive)
        
        // Highlight selected
        when (type) {
            "STAMPS" -> highlightCard(binding.cardStamps)
            "POINTS" -> highlightCard(binding.cardPoints)
            "PROGRESSIVE" -> highlightCard(binding.cardProgressive)
        }
        
        // Visibility
        binding.layoutProgressive.visibility = if (type == "PROGRESSIVE") View.VISIBLE else View.GONE
    }

    private fun resetCard(card: com.google.android.material.card.MaterialCardView) {
        card.strokeWidth = 0
        card.setCardBackgroundColor(androidx.core.content.ContextCompat.getColor(this, R.color.white))
    }

    private fun highlightCard(card: com.google.android.material.card.MaterialCardView) {
        card.strokeWidth = 6 
        card.strokeColor = androidx.core.content.ContextCompat.getColor(this, R.color.primary)
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

        viewModel.programCreated.observe(this) { created ->
            if (created) {
                Toast.makeText(this, getString(R.string.program_saved_success), Toast.LENGTH_SHORT).show()
                // Navigate to Dashboard
                val intent = android.content.Intent(this, com.jitplus.merchant.ui.dashboard.DashboardActivity::class.java)
                intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }
    
    private fun attemptSaveProgram(username: String) {
        val name = binding.progName.text.toString().trim()
        val type = selectedType
        
        val valueStr = binding.progValue.text.toString().trim()
        val thresholdStr = binding.progThreshold.text.toString().trim()
        val reward = binding.progReward.text.toString().trim()

        // Validation Basic
        if (name.isEmpty() || valueStr.isEmpty() || thresholdStr.isEmpty() || reward.isEmpty()) {
            showError(getString(R.string.fill_all_fields))
            return
        }

        val value = valueStr.toIntOrNull()
        val threshold = thresholdStr.toIntOrNull()

        if (value == null || value <= 0) {
            showError("La valeur par visite doit être positive")
            return
        }

        if (threshold == null || threshold <= 0) {
            showError(getString(R.string.positive_threshold_error))
            return
        }

        // Progressive Fields
        var progStep = 5
        var progBonus = 1
        if (type == "PROGRESSIVE") {
            val stepStr = binding.etProgStep.text.toString().trim()
            val bonusStr = binding.etProgBonus.text.toString().trim()
            
            if (stepStr.isNotEmpty()) progStep = stepStr.toIntOrNull() ?: 5
            if (bonusStr.isNotEmpty()) progBonus = bonusStr.toIntOrNull() ?: 1
        }

        // Happy Hour Fields
        val hhEnabled = binding.switchHappyHour.isChecked
        var hhStart: String? = null
        var hhEnd: String? = null
        var hhMult = 1.0

        if (hhEnabled) {
            hhStart = binding.etHhStart.text.toString().trim()
            hhEnd = binding.etHhEnd.text.toString().trim()
            val multStr = binding.etHhMultiplier.text.toString().trim()
            
            if (hhStart.isEmpty() || hhEnd.isEmpty()) {
                showError("Veuillez définir les heures de Happy Hour")
                return
            }
            if (multStr.isNotEmpty()) {
                hhMult = multStr.toDoubleOrNull() ?: 1.0
            }
        }

        val program = LoyaltyProgram(
            merchantId = username,
            name = name,
            type = type,
            pointsPerVisit = value,
            threshold = threshold,
            rewardDescription = reward,
            happyHourEnabled = hhEnabled,
            happyHourStart = hhStart,
            happyHourEnd = hhEnd,
            happyHourMultiplier = hhMult,
            progressiveStep = progStep,
            progressiveBonus = progBonus
        )

        viewModel.createProgram(program)
    }
    
    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnSaveProgram.isEnabled = !show
        binding.progName.isEnabled = !show
        binding.progValue.isEnabled = !show
        binding.progThreshold.isEnabled = !show
        binding.progReward.isEnabled = !show
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
