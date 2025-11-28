package com.jitplus.merchant.ui.customer

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.jitplus.merchant.R
import com.jitplus.merchant.databinding.ActivityQrCodeBinding
import com.jitplus.merchant.ui.loyalty.LoyaltyCardActivity
import com.jitplus.merchant.utils.ErrorHandler
import com.jitplus.merchant.utils.QRCodeUtils
import java.io.File
import java.io.FileOutputStream

class QRCodeActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityQrCodeBinding
    private val viewModel: CustomerViewModel by viewModels()
    
    private var phoneNumber: String? = null
    private var customerId: Long? = null
    private var merchantId: String? = null
    private var qrBitmap: Bitmap? = null
    private var qrToken: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Get data from intent
        phoneNumber = intent.getStringExtra("PHONE_NUMBER")
        customerId = intent.getLongExtra("CUSTOMER_ID", -1)
        merchantId = intent.getStringExtra("MERCHANT_ID")
        
        if (!validateIntentData()) {
            finish()
            return
        }
        
        binding.tvCustomerPhone.text = getString(R.string.phone_label, phoneNumber)
        
        setupObservers()
        
        // Fetch QR token from backend, then generate QR code
        viewModel.getQrToken(customerId!!)
        
        binding.btnShareQr.setOnClickListener {
            shareQRCode()
        }
        
        binding.btnViewCard.setOnClickListener {
            navigateToLoyaltyCard()
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

        viewModel.qrToken.observe(this) { token ->
            if (token != null) {
                qrToken = token
                generateQRCode()
            }
        }
    }
    
    private fun validateIntentData(): Boolean {
        return when {
            customerId == -1L || merchantId == null -> {
                showError(getString(R.string.error_missing_customer_data))
                false
            }
            phoneNumber == null -> {
                showError(getString(R.string.error_missing_phone))
                false
            }
            else -> true
        }
    }
    
    private fun generateQRCode() {
        try {
            if (qrToken.isNullOrEmpty()) {
                showError(getString(R.string.invalid_qr_token))
                return
            }
            
            qrBitmap = QRCodeUtils.generateQRCode(qrToken!!, 512)
            binding.ivQrCode.setImageBitmap(qrBitmap)
        } catch (e: Exception) {
            ErrorHandler.logError("QRCodeActivity", "QR generation failed", e)
            showError(getString(R.string.qr_generation_error))
        }
    }
    
    private fun shareQRCode() {
        if (qrBitmap == null) {
            showError(getString(R.string.qr_unavailable))
            return
        }
        
        try {
            val file = saveQrCodeToCache()
            val contentUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
            val shareIntent = createShareIntent(contentUri)
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_qr_title)))
        } catch (e: Exception) {
            ErrorHandler.logError("QRCodeActivity", "Share failed", e)
            showError(getString(R.string.share_error))
        }
    }
    
    private fun saveQrCodeToCache(): File {
        val cachePath = File(cacheDir, "qr_codes").apply { mkdirs() }
        val file = File(cachePath, "qr_code_${customerId}.png")
        FileOutputStream(file).use { output ->
            qrBitmap!!.compress(Bitmap.CompressFormat.PNG, 100, output)
        }
        return file
    }
    
    private fun createShareIntent(contentUri: Uri): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, contentUri)
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_qr_subject))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_qr_text))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
    
    private fun navigateToLoyaltyCard() {
        val intent = Intent(this, LoyaltyCardActivity::class.java).apply {
            putExtra("CUSTOMER_ID", customerId)
            putExtra("MERCHANT_ID", merchantId)
            putExtra("PHONE_NUMBER", phoneNumber)
        }
        startActivity(intent)
        finish()
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    
    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnShareQr.isEnabled = !show
        binding.btnViewCard.isEnabled = !show
    }
}
