package com.jitplus.merchant.utils

import android.content.Context
import android.util.Log
import com.jitplus.merchant.R

object ErrorHandler {
    
    fun getHttpErrorMessage(context: Context, code: Int): String {
        return when (code) {
            400 -> context.getString(R.string.error_bad_request)
            401 -> context.getString(R.string.error_unauthorized)
            403 -> context.getString(R.string.error_forbidden)
            404 -> context.getString(R.string.error_not_found)
            408 -> context.getString(R.string.error_timeout)
            409 -> context.getString(R.string.error_conflict)
            422 -> context.getString(R.string.error_unprocessable)
            429 -> context.getString(R.string.error_too_many_requests)
            in 500..599 -> context.getString(R.string.error_server)
            else -> context.getString(R.string.error_connection_code, code)
        }
    }
    
    fun getNetworkErrorMessage(context: Context, throwable: Throwable): String {
        return when (throwable) {
            is java.net.ConnectException -> context.getString(R.string.net_error_connect)
            is java.net.SocketTimeoutException -> context.getString(R.string.net_error_timeout)
            is java.net.UnknownHostException -> context.getString(R.string.net_error_unknown_host)
            is javax.net.ssl.SSLException -> context.getString(R.string.net_error_ssl)
            else -> context.getString(R.string.net_error_generic, throwable.localizedMessage ?: "Inconnue")
        }
    }
    
    fun logError(tag: String, message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e(tag, message, throwable)
        } else {
            Log.e(tag, message)
        }
    }
}
