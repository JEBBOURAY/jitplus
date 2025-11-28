package com.jitplus.merchant.ui.loyalty

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jitplus.merchant.data.model.LoyaltyCard
import com.jitplus.merchant.data.model.LoyaltyProgram
import com.jitplus.merchant.data.model.RedemptionRequest
import com.jitplus.merchant.data.model.VisitRequest
import com.jitplus.merchant.data.model.VisitResponse
import com.jitplus.merchant.data.repository.MerchantRepository
import com.jitplus.merchant.utils.ErrorHandler
import kotlinx.coroutines.launch

class LoyaltyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MerchantRepository(application)

    private val _program = MutableLiveData<LoyaltyProgram?>()
    val program: LiveData<LoyaltyProgram?> = _program

    private val _card = MutableLiveData<LoyaltyCard?>()
    val card: LiveData<LoyaltyCard?> = _card

    private val _visitResponse = MutableLiveData<VisitResponse?>()
    val visitResponse: LiveData<VisitResponse?> = _visitResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    
    private val _programCreated = MutableLiveData<Boolean>()
    val programCreated: LiveData<Boolean> = _programCreated

    private val _redemptionSuccess = MutableLiveData<Boolean>()
    val redemptionSuccess: LiveData<Boolean> = _redemptionSuccess

    fun getProgram(merchantId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getProgram(merchantId)
                _isLoading.value = false
                if (response.isSuccessful) {
                    _program.value = response.body()
                } else {
                    _errorMessage.value = ErrorHandler.getHttpErrorMessage(getApplication(), response.code())
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = ErrorHandler.getNetworkErrorMessage(getApplication(), e)
            }
        }
    }

    fun createProgram(program: LoyaltyProgram) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.createProgram(program)
                _isLoading.value = false
                if (response.isSuccessful) {
                    _programCreated.value = true
                } else {
                    _errorMessage.value = ErrorHandler.getHttpErrorMessage(getApplication(), response.code())
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = ErrorHandler.getNetworkErrorMessage(getApplication(), e)
            }
        }
    }

    fun getCard(merchantId: String, customerId: Long) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getCard(merchantId, customerId)
                _isLoading.value = false
                if (response.isSuccessful) {
                    _card.value = response.body()
                } else {
                    _errorMessage.value = ErrorHandler.getHttpErrorMessage(getApplication(), response.code())
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = ErrorHandler.getNetworkErrorMessage(getApplication(), e)
            }
        }
    }

    fun recordVisit(request: VisitRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.recordVisit(request)
                _isLoading.value = false
                if (response.isSuccessful) {
                    _visitResponse.value = response.body()
                } else {
                    _errorMessage.value = ErrorHandler.getHttpErrorMessage(getApplication(), response.code())
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = ErrorHandler.getNetworkErrorMessage(getApplication(), e)
            }
        }
    }

    fun redeemReward(request: RedemptionRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.redeemReward(request)
                _isLoading.value = false
                if (response.isSuccessful) {
                    _card.value = response.body() // Update card with new balance
                    _redemptionSuccess.value = true
                } else {
                    _errorMessage.value = ErrorHandler.getHttpErrorMessage(getApplication(), response.code())
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = ErrorHandler.getNetworkErrorMessage(getApplication(), e)
            }
        }
    }
    
    fun resetRedemptionSuccess() {
        _redemptionSuccess.value = false
    }
    
    fun resetVisitResponse() {
        _visitResponse.value = null
    }
}
