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
import com.jitplus.merchant.utils.ValidationUtils
import kotlinx.coroutines.launch

class CustomerSearchViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MerchantRepository(application)

    private val _customer = MutableLiveData<Customer>()
    val customer: LiveData<Customer> = _customer

    private val _loyaltyCard = MutableLiveData<LoyaltyCard>()
    val loyaltyCard: LiveData<LoyaltyCard> = _loyaltyCard

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _notFound = MutableLiveData<String>() // Phone number that was not found
    val notFound: LiveData<String> = _notFound

    fun searchByPhone(phone: String) {
        if (phone.isEmpty()) {
            _errorMessage.value = "Veuillez entrer un numéro de téléphone"
            return
        }
        if (!ValidationUtils.isValidPhoneNumber(phone)) {
            _errorMessage.value = "Format de téléphone invalide. Utilisez le format français (ex: 0612345678)"
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getCustomerByPhone(phone)
                _isLoading.value = false
                
                when {
                    response.isSuccessful && response.body() != null -> {
                        _customer.value = response.body()
                    }
                    response.code() == 404 -> {
                        _notFound.value = phone
                    }
                    else -> {
                        ErrorHandler.logError("CustomerSearchViewModel", "Search failed: ${response.code()}")
                        _errorMessage.value = ErrorHandler.getHttpErrorMessage(getApplication(), response.code())
                    }
                }
            } catch (e: Exception) {
                _isLoading.value = false
                ErrorHandler.logError("CustomerSearchViewModel", "Network error", e)
                _errorMessage.value = ErrorHandler.getNetworkErrorMessage(getApplication(), e)
            }
        }
    }

    fun searchByQrToken(token: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getCustomerByQrToken(token)
                _isLoading.value = false
                
                when {
                    response.isSuccessful && response.body() != null -> {
                        _customer.value = response.body()
                    }
                    response.code() == 404 -> {
                        _errorMessage.value = "QR Code invalide ou client introuvable"
                    }
                    else -> {
                        ErrorHandler.logError("CustomerSearchViewModel", "QR search failed: ${response.code()}")
                        _errorMessage.value = ErrorHandler.getHttpErrorMessage(getApplication(), response.code())
                    }
                }
            } catch (e: Exception) {
                _isLoading.value = false
                ErrorHandler.logError("CustomerSearchViewModel", "Network error", e)
                _errorMessage.value = ErrorHandler.getNetworkErrorMessage(getApplication(), e)
            }
        }
    }

    fun checkLoyaltyCard(merchantId: String, customerId: Long, name: String?, phone: String?) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.createCard(merchantId, customerId, name, phone)
                _isLoading.value = false
                
                if (response.isSuccessful && response.body() != null) {
                    _loyaltyCard.value = response.body()
                } else {
                    ErrorHandler.logError("CustomerSearchViewModel", "Card retrieval failed: ${response.code()}")
                    _errorMessage.value = ErrorHandler.getHttpErrorMessage(getApplication(), response.code())
                }
            } catch (e: Exception) {
                _isLoading.value = false
                ErrorHandler.logError("CustomerSearchViewModel", "Network error", e)
                _errorMessage.value = ErrorHandler.getNetworkErrorMessage(getApplication(), e)
            }
        }
    }
}
