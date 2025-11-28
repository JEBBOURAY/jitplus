package com.jitplus.merchant.utils

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("jitplus_settings", Context.MODE_PRIVATE)

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("notifications_enabled", enabled).apply()
    }

    fun areNotificationsEnabled(): Boolean {
        return prefs.getBoolean("notifications_enabled", true)
    }

    fun setDarkModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("dark_mode_enabled", enabled).apply()
    }

    fun isDarkModeEnabled(): Boolean {
        return prefs.getBoolean("dark_mode_enabled", false)
    }

    fun setStoreInfo(name: String, address: String, phone: String) {
        val editor = prefs.edit()
        editor.putString("store_name", name)
        editor.putString("store_address", address)
        editor.putString("store_phone", phone)
        editor.apply()
    }

    fun getStoreName(): String? = prefs.getString("store_name", null)
    fun getStoreAddress(): String? = prefs.getString("store_address", null)
    fun getStorePhone(): String? = prefs.getString("store_phone", null)
}
