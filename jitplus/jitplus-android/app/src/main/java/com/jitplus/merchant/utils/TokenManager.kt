package com.jitplus.merchant.utils

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences("jitplus_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        val editor = prefs.edit()
        editor.putString("jwt_token", token)
        editor.apply()
    }

    fun saveUsername(username: String) {
        val editor = prefs.edit()
        editor.putString("username", username)
        editor.apply()
    }

    fun getToken(): String? {
        return prefs.getString("jwt_token", null)
    }

    fun getUsername(): String? {
        return prefs.getString("username", null)
    }

    fun clearToken() {
        val editor = prefs.edit()
        editor.remove("jwt_token")
        editor.remove("username")
        editor.apply()
    }
}
