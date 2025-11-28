package com.jitplus.merchant.ui.loyalty

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.jitplus.merchant.R
import com.jitplus.merchant.data.model.LoyaltyCard
import com.jitplus.merchant.data.model.LoyaltyProgram
import com.jitplus.merchant.data.model.RedemptionRequest
import com.jitplus.merchant.data.model.VisitRequest
import com.jitplus.merchant.databinding.ActivityLoyaltyCardBinding

class LoyaltyCardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoyaltyCardBinding
    private val viewModel: LoyaltyViewModel by viewModels()
    
    private var customerId: Long = -1
    private var merchantId: String? = null
    private var currentProgram: LoyaltyProgram? = null
    private var currentCard: LoyaltyCard? = null
    private var customerPhoneNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoyaltyCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customerId = intent.getLongExtra("CUSTOMER_ID", -1)
        merchantId = intent.getStringExtra("MERCHANT_ID")
        customerPhoneNumber = intent.getStringExtra("PHONE_NUMBER")

        if (customerId == -1L || merchantId == null) {
            Toast.makeText(this, getString(R.string.error_missing_customer_data), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupObservers()
        loadData()

        binding.btnAddVisit.setOnClickListener {
            addVisit()
        }
        
        binding.btnShowQr.setOnClickListener {
            showQRCode()
        }

        binding.btnRedeemReward.setOnClickListener {
            redeemReward()
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

        viewModel.program.observe(this) { program ->
            if (program != null) {
                currentProgram = program
                binding.tvProgramName.text = getString(R.string.program_label, program.name)
                updateUI()
            }
        }

        viewModel.card.observe(this) { card ->
            if (card != null) {
                currentCard = card
                updateUI()
            }
        }

        viewModel.visitResponse.observe(this) { visitResponse ->
            if (visitResponse != null) {
                // Update local card state
                currentCard = currentCard?.copy(
                    currentStamps = visitResponse.currentStamps,
                    currentPoints = visitResponse.currentPoints
                )
                updateUI()

                if (visitResponse.rewardUnlocked) {
                    showRewardDialog(visitResponse.rewardDescription)
                } else {
                    Toast.makeText(this, getString(R.string.visit_added_success), Toast.LENGTH_SHORT).show()
                }
                viewModel.resetVisitResponse()
            }
        }

        viewModel.redemptionSuccess.observe(this) { success ->
            if (success) {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.reward_redeemed_title))
                    .setMessage(getString(R.string.reward_redeemed_message))
                    .setPositiveButton(getString(R.string.ok), null)
                    .show()
                viewModel.resetRedemptionSuccess()
            }
        }
    }

    private fun loadData() {
        viewModel.getProgram(merchantId!!)
        viewModel.getCard(merchantId!!, customerId)
    }

    private fun updateUI() {
        if (currentProgram != null && currentCard != null) {
            val isStamps = currentProgram!!.type == "STAMPS"
            val current = if (isStamps) currentCard!!.currentStamps else currentCard!!.currentPoints
            val threshold = currentProgram!!.threshold

            if (isStamps) {
                // Show Stamps Grid
                binding.gridStamps.visibility = View.VISIBLE
                binding.layoutPoints.visibility = View.GONE
                
                binding.gridStamps.removeAllViews()
                binding.gridStamps.columnCount = 5
                
                val sizePx = (48 * resources.displayMetrics.density).toInt()
                val marginPx = (4 * resources.displayMetrics.density).toInt()

                for (i in 1..threshold) {
                    val stampView = ImageView(this)
                    val params = LinearLayout.LayoutParams(sizePx, sizePx)
                    params.setMargins(marginPx, marginPx, marginPx, marginPx)
                    stampView.layoutParams = params
                    
                    if (i <= current) {
                        stampView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_stamp_filled))
                    } else {
                        stampView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_stamp_empty))
                    }
                    
                    binding.gridStamps.addView(stampView)
                }
                
            } else {
                // Show Points Progress
                binding.gridStamps.visibility = View.GONE
                binding.layoutPoints.visibility = View.VISIBLE
                
                binding.progressPoints.max = threshold
                binding.progressPoints.progress = current
                binding.tvPointsValue.text = current.toString()
                binding.tvPointsLabel.text = "/ $threshold Points"
            }

            if (current >= threshold) {
                binding.tvRewardStatus.text = getString(R.string.reward_available, currentProgram!!.rewardDescription)
                binding.btnRedeemReward.visibility = View.VISIBLE
            } else {
                binding.tvRewardStatus.text = ""
                binding.btnRedeemReward.visibility = View.GONE
            }
        }
    }

    private fun addVisit() {
        val request = VisitRequest(merchantId!!, customerId, 1)
        viewModel.recordVisit(request)
    }

    private fun redeemReward() {
        val request = RedemptionRequest(merchantId!!, customerId)
        viewModel.redeemReward(request)
    }

    private fun showQRCode() {
        val phoneToShow = customerPhoneNumber
        
        if (phoneToShow != null) {
            val intent = android.content.Intent(this, com.jitplus.merchant.ui.customer.QRCodeActivity::class.java)
            intent.putExtra("CUSTOMER_ID", customerId)
            intent.putExtra("MERCHANT_ID", merchantId)
            intent.putExtra("PHONE_NUMBER", phoneToShow)
            startActivity(intent)
        } else {
            Toast.makeText(this, getString(R.string.phone_unavailable), Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun showRewardDialog(reward: String) {
        if (isDestroyed || isFinishing) return
        
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.congrats_title))
            .setMessage(getString(R.string.reward_unlocked_message, reward))
            .setPositiveButton(getString(R.string.ok), null)
            .show()
    }
    
    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnAddVisit.isEnabled = !show
        binding.btnRedeemReward.isEnabled = !show && (currentCard?.let { card ->
            currentProgram?.let { program ->
                val current = if (program.type == "STAMPS") card.currentStamps else card.currentPoints
                current >= program.threshold
            }
        } ?: false)
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
