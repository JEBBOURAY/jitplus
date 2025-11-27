package com.jitplus.merchant.ui.loyalty

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.jitplus.merchant.R
import com.jitplus.merchant.api.ApiClient
import com.jitplus.merchant.api.services.LoyaltyService
import com.jitplus.merchant.data.model.LoyaltyCard
import com.jitplus.merchant.data.model.LoyaltyProgram
import com.jitplus.merchant.data.model.RedemptionRequest
import com.jitplus.merchant.data.model.VisitRequest
import com.jitplus.merchant.data.model.VisitResponse
import com.jitplus.merchant.utils.ErrorHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoyaltyCardActivity : AppCompatActivity() {

    private var customerId: Long = -1
    private var merchantId: String? = null
    private var currentProgram: LoyaltyProgram? = null
    private var currentCard: LoyaltyCard? = null

    private lateinit var tvProgramName: TextView
    private lateinit var tvProgress: TextView
    private lateinit var tvRewardStatus: TextView
    private lateinit var btnAddVisit: Button
    private lateinit var btnRedeem: Button
    private lateinit var progressBar: ProgressBar
    
    private var programCall: Call<LoyaltyProgram>? = null
    private var cardCall: Call<LoyaltyCard>? = null
    private var visitCall: Call<VisitResponse>? = null
    private var redeemCall: Call<LoyaltyCard>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loyalty_card)

        customerId = intent.getLongExtra("CUSTOMER_ID", -1)
        merchantId = intent.getStringExtra("MERCHANT_ID")

        if (customerId == -1L || merchantId == null) {
            Toast.makeText(this, "Erreur: données client manquantes", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        tvProgramName = findViewById(R.id.tv_program_name)
        tvProgress = findViewById(R.id.tv_progress)
        tvRewardStatus = findViewById(R.id.tv_reward_status)
        btnAddVisit = findViewById(R.id.btn_add_visit)
        btnRedeem = findViewById(R.id.btn_redeem_reward)
        progressBar = findViewById(R.id.progress_bar)

        val loyaltyService = ApiClient.getClient(this).create(LoyaltyService::class.java)

        loadData(loyaltyService)

        btnAddVisit.setOnClickListener {
            addVisit(loyaltyService)
        }

        btnRedeem.setOnClickListener {
            redeemReward(loyaltyService)
        }
    }

    private fun loadData(loyaltyService: LoyaltyService) {
        showLoading(true)
        
        // 1. Load Program
        programCall = loyaltyService.getProgram(merchantId!!)
        programCall?.enqueue(object : Callback<LoyaltyProgram> {
            override fun onResponse(call: Call<LoyaltyProgram>, response: Response<LoyaltyProgram>) {
                if (isDestroyed || isFinishing) return
                
                when {
                    response.isSuccessful && response.body() != null -> {
                        currentProgram = response.body()
                        tvProgramName.text = "Programme: ${currentProgram?.name}"
                        updateUI()
                        showLoading(false)
                    }
                    else -> {
                        showLoading(false)
                        val errorMsg = ErrorHandler.getHttpErrorMessage(response.code())
                        ErrorHandler.logError("LoyaltyCardActivity", "Program load failed: ${response.code()}")
                        showError("Erreur chargement programme: $errorMsg")
                    }
                }
            }
            
            override fun onFailure(call: Call<LoyaltyProgram>, t: Throwable) {
                if (isDestroyed || isFinishing) return
                
                showLoading(false)
                val errorMsg = ErrorHandler.getNetworkErrorMessage(t)
                ErrorHandler.logError("LoyaltyCardActivity", "Network error loading program", t)
                showError(errorMsg)
            }
        })

        // 2. Load Card
        cardCall = loyaltyService.getCard(merchantId!!, customerId)
        cardCall?.enqueue(object : Callback<LoyaltyCard> {
            override fun onResponse(call: Call<LoyaltyCard>, response: Response<LoyaltyCard>) {
                if (isDestroyed || isFinishing) return
                
                when {
                    response.isSuccessful && response.body() != null -> {
                        currentCard = response.body()
                        updateUI()
                    }
                    else -> {
                        val errorMsg = ErrorHandler.getHttpErrorMessage(response.code())
                        ErrorHandler.logError("LoyaltyCardActivity", "Card load failed: ${response.code()}")
                        showError("Erreur chargement carte: $errorMsg")
                    }
                }
            }
            
            override fun onFailure(call: Call<LoyaltyCard>, t: Throwable) {
                if (isDestroyed || isFinishing) return
                
                val errorMsg = ErrorHandler.getNetworkErrorMessage(t)
                ErrorHandler.logError("LoyaltyCardActivity", "Network error loading card", t)
                showError(errorMsg)
            }
        })
    }

    private fun updateUI() {
        if (currentProgram != null && currentCard != null) {
            val isStamps = currentProgram!!.type == "STAMPS"
            val current = if (isStamps) currentCard!!.currentStamps else currentCard!!.currentPoints
            val threshold = currentProgram!!.threshold

            tvProgress.text = "$current / $threshold ${if (isStamps) "Tampons" else "Points"}"

            if (current >= threshold) {
                tvRewardStatus.text = "Récompense disponible : ${currentProgram!!.rewardDescription}"
                btnRedeem.visibility = View.VISIBLE
            } else {
                tvRewardStatus.text = ""
                btnRedeem.visibility = View.GONE
            }
        }
    }

    private fun addVisit(loyaltyService: LoyaltyService) {
        showLoading(true)
        
        val request = VisitRequest(merchantId!!, customerId, 1)
        visitCall = loyaltyService.recordVisit(request)
        visitCall?.enqueue(object : Callback<VisitResponse> {
            override fun onResponse(call: Call<VisitResponse>, response: Response<VisitResponse>) {
                if (isDestroyed || isFinishing) return
                
                showLoading(false)
                
                when {
                    response.isSuccessful && response.body() != null -> {
                        val visitResponse = response.body()!!
                        
                        // Update local card state
                        currentCard = currentCard?.copy(
                            currentStamps = visitResponse.currentStamps,
                            currentPoints = visitResponse.currentPoints
                        )
                        updateUI()

                        if (visitResponse.rewardUnlocked) {
                            showRewardDialog(visitResponse.rewardDescription)
                        } else {
                            Toast.makeText(this@LoyaltyCardActivity, "+1 Ajouté avec succès !", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else -> {
                        val errorMsg = ErrorHandler.getHttpErrorMessage(response.code())
                        ErrorHandler.logError("LoyaltyCardActivity", "Visit recording failed: ${response.code()}")
                        showError("Erreur ajout visite: $errorMsg")
                    }
                }
            }

            override fun onFailure(call: Call<VisitResponse>, t: Throwable) {
                if (isDestroyed || isFinishing) return
                
                showLoading(false)
                val errorMsg = ErrorHandler.getNetworkErrorMessage(t)
                ErrorHandler.logError("LoyaltyCardActivity", "Network error recording visit", t)
                showError(errorMsg)
            }
        })
    }

    private fun redeemReward(loyaltyService: LoyaltyService) {
        showLoading(true)
        
        val request = RedemptionRequest(merchantId!!, customerId)
        redeemCall = loyaltyService.redeemReward(request)
        redeemCall?.enqueue(object : Callback<LoyaltyCard> {
            override fun onResponse(call: Call<LoyaltyCard>, response: Response<LoyaltyCard>) {
                if (isDestroyed || isFinishing) return
                
                showLoading(false)
                
                when {
                    response.isSuccessful && response.body() != null -> {
                        currentCard = response.body()
                        updateUI()
                        
                        AlertDialog.Builder(this@LoyaltyCardActivity)
                            .setTitle("✅ Récompense utilisée")
                            .setMessage("La récompense a été validée avec succès. Le solde a été remis à zéro.")
                            .setPositiveButton("OK", null)
                            .show()
                    }
                    else -> {
                        val errorMsg = ErrorHandler.getHttpErrorMessage(response.code())
                        ErrorHandler.logError("LoyaltyCardActivity", "Redemption failed: ${response.code()}")
                        showError("Erreur utilisation récompense: $errorMsg")
                    }
                }
            }

            override fun onFailure(call: Call<LoyaltyCard>, t: Throwable) {
                if (isDestroyed || isFinishing) return
                
                showLoading(false)
                val errorMsg = ErrorHandler.getNetworkErrorMessage(t)
                ErrorHandler.logError("LoyaltyCardActivity", "Network error redeeming reward", t)
                showError(errorMsg)
            }
        })
    }

    private fun showRewardDialog(reward: String) {
        if (isDestroyed || isFinishing) return
        
        AlertDialog.Builder(this)
            .setTitle("🎉 Félicitations !")
            .setMessage("Le client a débloqué sa récompense : $reward")
            .setPositiveButton("OK", null)
            .show()
    }
    
    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnAddVisit.isEnabled = !show
        btnRedeem.isEnabled = !show && (currentCard?.let { card ->
            currentProgram?.let { program ->
                val current = if (program.type == "STAMPS") card.currentStamps else card.currentPoints
                current >= program.threshold
            }
        } ?: false)
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        programCall?.cancel()
        cardCall?.cancel()
        visitCall?.cancel()
        redeemCall?.cancel()
    }
}
