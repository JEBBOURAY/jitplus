package com.jitplus.merchant.ui.customer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.jitplus.merchant.R
import com.jitplus.merchant.databinding.ActivityCustomerSearchBinding
import com.jitplus.merchant.ui.loyalty.LoyaltyCardActivity
import com.jitplus.merchant.utils.TokenManager

class CustomerSearchActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityCustomerSearchBinding
    private val viewModel: CustomerSearchViewModel by viewModels()
    
    companion object {
        private const val CAMERA_PERMISSION_REQUEST = 100
    }
    
    private var merchantId: String? = null
    
    private val scanLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            viewModel.searchByQrToken(result.contents)
        } else {
            showError(getString(R.string.scan_cancelled))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeServices()
        setupListeners()
        setupObservers()
    }
    
    private fun initializeServices() {
        val tokenManager = TokenManager(this)
        merchantId = tokenManager.getUsername()

        if (merchantId == null) {
            Toast.makeText(this, getString(R.string.session_expired), Toast.LENGTH_SHORT).show()
            finish()
            return
        }
    }
    
    private fun setupListeners() {
        binding.btnPerformSearch.setOnClickListener { 
            val phone = binding.searchPhone.text.toString().trim()
            viewModel.searchByPhone(phone)
        }
        binding.btnScanQr.setOnClickListener { checkCameraPermissionAndScan() }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.errorMessage.observe(this) { errorMsg ->
            showError(errorMsg)
        }

        viewModel.customer.observe(this) { customer ->
            binding.searchPhone.setText(customer.phoneNumber)
            // Automatically check/create loyalty card when customer is found
            viewModel.checkLoyaltyCard(merchantId!!, customer.id!!, customer.name, customer.phoneNumber)
        }

        viewModel.loyaltyCard.observe(this) { card ->
            navigateToLoyaltyCard(card.customerId, binding.searchPhone.text.toString())
        }

        viewModel.notFound.observe(this) { phone ->
            showNotFoundDialog(phone)
        }
    }
    
    private fun navigateToLoyaltyCard(customerId: Long, phoneNumber: String) {
        val intent = Intent(this, LoyaltyCardActivity::class.java).apply {
            putExtra("CUSTOMER_ID", customerId)
            putExtra("MERCHANT_ID", merchantId)
            putExtra("PHONE_NUMBER", phoneNumber)
        }
        startActivity(intent)
        finish()
    }

    private fun showNotFoundDialog(phone: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.customer_not_found_title))
            .setMessage(getString(R.string.customer_not_found_message))
            .setPositiveButton(getString(R.string.create)) { _, _ ->
                val intent = Intent(this, AddCustomerActivity::class.java)
                intent.putExtra("PHONE_NUMBER", phone)
                startActivity(intent)
                finish()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
    
    private fun checkCameraPermissionAndScan() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
            == PackageManager.PERMISSION_GRANTED) {
            startQRCodeScan()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST
            )
        }
    }
    
    private fun startQRCodeScan() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt(getString(R.string.scan_qr_prompt))
        options.setCameraId(0)
        options.setBeepEnabled(true)
        options.setBarcodeImageEnabled(false)
        options.setOrientationLocked(true)
        scanLauncher.launch(options)
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startQRCodeScan()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.camera_permission_required),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    
    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnPerformSearch.isEnabled = !show
        binding.btnScanQr.isEnabled = !show
        binding.searchPhone.isEnabled = !show
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
