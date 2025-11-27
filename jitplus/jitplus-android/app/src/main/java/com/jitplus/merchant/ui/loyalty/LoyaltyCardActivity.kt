package com.jitplus.merchant.ui.loyalty

import android.os.Bundle
import android.view.View
import android.widget.Button
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loyalty_card)

        customerId = intent.getLongExtra("CUSTOMER_ID", -1)
        merchantId = intent.getStringExtra("MERCHANT_ID")

        if (customerId == -1L || merchantId == null) {
            finish()
            return
        }

        tvProgramName = findViewById(R.id.tv_program_name)
        tvProgress = findViewById(R.id.tv_progress)
        tvRewardStatus = findViewById(R.id.tv_reward_status)
        btnAddVisit = findViewById(R.id.btn_add_visit)
        btnRedeem = findViewById(R.id.btn_redeem_reward)

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
        // 1. Load Program
        loyaltyService.getProgram(merchantId!!).enqueue(object : Callback<LoyaltyProgram> {
            override fun onResponse(call: Call<LoyaltyProgram>, response: Response<LoyaltyProgram>) {
                if (response.isSuccessful) {
                    currentProgram = response.body()
                    tvProgramName.text = "Programme: ${currentProgram?.name}"
                    updateUI()
                }
            }
            override fun onFailure(call: Call<LoyaltyProgram>, t: Throwable) {}
        })

        // 2. Load Card
        loyaltyService.getCard(merchantId!!, customerId).enqueue(object : Callback<LoyaltyCard> {
            override fun onResponse(call: Call<LoyaltyCard>, response: Response<LoyaltyCard>) {
                if (response.isSuccessful) {
                    currentCard = response.body()
                    updateUI()
                }
            }
            override fun onFailure(call: Call<LoyaltyCard>, t: Throwable) {}
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
        val request = VisitRequest(merchantId!!, customerId, 1)
        loyaltyService.recordVisit(request).enqueue(object : Callback<VisitResponse> {
            override fun onResponse(call: Call<VisitResponse>, response: Response<VisitResponse>) {
                if (response.isSuccessful && response.body() != null) {
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
                        Toast.makeText(this@LoyaltyCardActivity, "+1 Ajouté !", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<VisitResponse>, t: Throwable) {
                Toast.makeText(this@LoyaltyCardActivity, "Erreur: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun redeemReward(loyaltyService: LoyaltyService) {
        val request = RedemptionRequest(merchantId!!, customerId)
        loyaltyService.redeemReward(request).enqueue(object : Callback<LoyaltyCard> {
            override fun onResponse(call: Call<LoyaltyCard>, response: Response<LoyaltyCard>) {
                if (response.isSuccessful && response.body() != null) {
                    currentCard = response.body()
                    updateUI()
                    
                    AlertDialog.Builder(this@LoyaltyCardActivity)
                        .setTitle("Récompense utilisée")
                        .setMessage("La récompense a été validée. Le solde a été mis à jour.")
                        .setPositiveButton("OK", null)
                        .show()
                } else {
                    Toast.makeText(this@LoyaltyCardActivity, "Erreur lors de l'utilisation", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoyaltyCard>, t: Throwable) {
                Toast.makeText(this@LoyaltyCardActivity, "Erreur réseau: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showRewardDialog(reward: String) {
        AlertDialog.Builder(this)
            .setTitle("Félicitations !")
            .setMessage("Le client a gagné : $reward")
            .setPositiveButton("OK", null)
            .show()
    }
}
