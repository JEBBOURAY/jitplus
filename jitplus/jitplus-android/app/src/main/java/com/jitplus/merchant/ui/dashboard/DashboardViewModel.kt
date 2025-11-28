package com.jitplus.merchant.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jitplus.merchant.data.model.DashboardStats
import com.jitplus.merchant.data.repository.MerchantRepository
import com.jitplus.merchant.utils.ErrorHandler
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MerchantRepository(application)

    private val _stats = MutableLiveData<DashboardStats>()
    val stats: LiveData<DashboardStats> = _stats

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun loadStats(merchantId: String) {
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                val response = repository.getDashboardStats(merchantId)
                _isLoading.value = false
                
                if (response.isSuccessful && response.body() != null) {
                    _stats.value = response.body()
                } else {
                    ErrorHandler.logError("DashboardViewModel", "Stats load failed: ${response.code()}")
                    _errorMessage.value = ErrorHandler.getHttpErrorMessage(getApplication(), response.code())
                }
            } catch (e: Exception) {
                _isLoading.value = false
                ErrorHandler.logError("DashboardViewModel", "Network error", e)
                _errorMessage.value = ErrorHandler.getNetworkErrorMessage(getApplication(), e)
            }
        }
    }
}
