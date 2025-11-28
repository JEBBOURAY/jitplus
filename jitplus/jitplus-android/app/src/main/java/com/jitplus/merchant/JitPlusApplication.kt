package com.jitplus.merchant

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.jitplus.merchant.utils.SettingsManager

class JitPlusApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        val settingsManager = SettingsManager(this)
        val isDarkMode = settingsManager.isDarkModeEnabled()
        val mode = if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}
