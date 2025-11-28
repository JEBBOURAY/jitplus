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

        binding.btnSaveProgram.setOnClickListener {
            attemptSaveProgram(username)
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

        viewModel.programCreated.observe(this) { created ->
            if (created) {
                Toast.makeText(this, getString(R.string.program_saved_success), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    
    private fun attemptSaveProgram(username: String) {
        val name = binding.progName.text.toString().trim()
        val isStamps = binding.rbStamps.isChecked
        val type = if (isStamps) "STAMPS" else "POINTS"
        val valueStr = binding.progValue.text.toString().trim()
        val thresholdStr = binding.progThreshold.text.toString().trim()
        val reward = binding.progReward.text.toString().trim()

        // Validation
        if (name.isEmpty() || valueStr.isEmpty() || thresholdStr.isEmpty() || reward.isEmpty()) {
            showError(getString(R.string.fill_all_fields))
            return
        }

        val value = valueStr.toIntOrNull()
        val threshold = thresholdStr.toIntOrNull()

        if (value == null || value <= 0) {
            val unit = if (isStamps) getString(R.string.stamps) else getString(R.string.points)
            showError(getString(R.string.positive_value_error, unit))
            return
        }

        if (threshold == null || threshold <= 0) {
            showError(getString(R.string.positive_threshold_error))
            return
        }

        if (threshold <= value) {
            val unit = if (isStamps) getString(R.string.stamps) else getString(R.string.points)
            showError(getString(R.string.threshold_error, unit))
            return
        }

        val program = LoyaltyProgram(
            merchantId = username,
            name = name,
            type = type,
            pointsPerVisit = value,
            threshold = threshold,
            rewardDescription = reward
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
