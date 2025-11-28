package com.jitplus.merchant.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jitplus.merchant.data.model.LoginRequest
import com.jitplus.merchant.data.model.RegisterRequest
import com.jitplus.merchant.data.repository.MerchantRepository
import com.jitplus.merchant.utils.ErrorHandler
import com.jitplus.merchant.utils.TokenManager
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MerchantRepository(application)
    private val tokenManager = TokenManager(application)

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    private val _registerSuccess = MutableLiveData<Boolean>()
    val registerSuccess: LiveData<Boolean> = _registerSuccess

    private val _updateStoreSuccess = MutableLiveData<Boolean>()
    val updateStoreSuccess: LiveData<Boolean> = _updateStoreSuccess

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun login(request: LoginRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.login(request)
                _isLoading.value = false
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    val token = response.body()!!
                    tokenManager.saveToken(token)
                    tokenManager.saveUsername(request.username)
                    _loginSuccess.value = true
                } else {
                    _errorMessage.value = ErrorHandler.getHttpErrorMessage(getApplication(), response.code())
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = ErrorHandler.getNetworkErrorMessage(getApplication(), e)
            }
        }
    }

    fun register(request: RegisterRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.register(request)
                _isLoading.value = false
                if (response.isSuccessful) {
                    _registerSuccess.value = true
                } else {
                    _errorMessage.value = ErrorHandler.getHttpErrorMessage(getApplication(), response.code())
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = ErrorHandler.getNetworkErrorMessage(getApplication(), e)
            }
        }
    }

    fun updateStoreInfo(request: com.jitplus.merchant.data.model.StoreInfoRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.updateStoreInfo(request)
                _isLoading.value = false
                if (response.isSuccessful) {
                    _updateStoreSuccess.value = true
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
