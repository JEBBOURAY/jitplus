package com.jitplus.merchant.ui.customer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jitplus.merchant.data.model.Customer
import com.jitplus.merchant.data.model.LoyaltyCard
import com.jitplus.merchant.data.repository.MerchantRepository
import com.jitplus.merchant.utils.ErrorHandler
import kotlinx.coroutines.launch

class CustomerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MerchantRepository(application)

    private val _customerCreated = MutableLiveData<Customer?>()
    val customerCreated: LiveData<Customer?> = _customerCreated

    private val _cardCreated = MutableLiveData<LoyaltyCard?>()
    val cardCreated: LiveData<LoyaltyCard?> = _cardCreated

    private val _qrToken = MutableLiveData<String?>()
    val qrToken: LiveData<String?> = _qrToken

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getQrToken(customerId: Long) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getQrToken(customerId)
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _qrToken.value = response.body()!!.qrToken
                } else {
                    _errorMessage.value = ErrorHandler.getHttpErrorMessage(getApplication(), response.code())
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = ErrorHandler.getNetworkErrorMessage(getApplication(), e)
            }
        }
    }

    fun registerCustomer(customer: Customer) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.registerCustomer(customer)
                _isLoading.value = false
                if (response.isSuccessful) {
                    _customerCreated.value = response.body()
                } else {
                    _errorMessage.value = ErrorHandler.getHttpErrorMessage(getApplication(), response.code())
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = ErrorHandler.getNetworkErrorMessage(getApplication(), e)
            }
        }
    }

    fun createCard(merchantId: String, customerId: Long, name: String?, phone: String?) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.createCard(merchantId, customerId, name, phone)
                _isLoading.value = false
                if (response.isSuccessful) {
                    _cardCreated.value = response.body()
                } else {
                    _errorMessage.value = ErrorHandler.getHttpErrorMessage(getApplication(), response.code())
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = ErrorHandler.getNetworkErrorMessage(getApplication(), e)
            }
        }
    }
}
