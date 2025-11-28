package com.jitplus.merchant.ui.dashboard

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.jitplus.merchant.data.model.DashboardStats
import com.jitplus.merchant.databinding.ActivityDashboardBinding
import com.jitplus.merchant.utils.TokenManager

class DashboardActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var topCustomerAdapter: TopCustomerAdapter
    
    private val viewModel: DashboardViewModel by viewModels()
    private var merchantId: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        initializeViews()
        setupObservers()
        
        val tokenManager = TokenManager(this)
        merchantId = tokenManager.getUsername()
        
        if (merchantId == null) {
            Toast.makeText(this, getString(com.jitplus.merchant.R.string.session_expired), Toast.LENGTH_LONG).show()
            finish()
            return
        }
        
        binding.btnRefresh.setOnClickListener { viewModel.loadStats(merchantId!!) }
        
        // Load stats on start
        viewModel.loadStats(merchantId!!)
    }
    
    private fun initializeViews() {
        binding.rvTopCustomers.layoutManager = LinearLayoutManager(this)
        topCustomerAdapter = TopCustomerAdapter()
        binding.rvTopCustomers.adapter = topCustomerAdapter
    }
    
    private fun setupObservers() {
        viewModel.stats.observe(this) { stats ->
            displayStats(stats)
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }
        
        viewModel.errorMessage.observe(this) { errorMsg ->
            showError(errorMsg)
        }
    }
    
    private fun displayStats(stats: DashboardStats) {
        binding.tvTotalCustomers.text = stats.totalCustomers.toString()
        binding.tvVisitsWeek.text = stats.visitsThisWeek.toString()
        binding.tvRewardsDistributed.text = stats.totalRewardsDistributed.toString()
        
        topCustomerAdapter.submitList(stats.topCustomers)
    }
    
    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnRefresh.isEnabled = !show
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
