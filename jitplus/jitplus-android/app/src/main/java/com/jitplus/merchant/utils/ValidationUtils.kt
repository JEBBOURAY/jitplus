package com.jitplus.merchant.utils

import android.util.Patterns

object ValidationUtils {
    
    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    fun isValidPassword(password: String): Pair<Boolean, String> {
        return when {
            password.length < 6 -> false to "Mot de passe trop court (minimum 6 caractères)"
            !password.any { it.isDigit() } -> false to "Doit contenir au moins un chiffre"
            !password.any { it.isLetter() } -> false to "Doit contenir au moins une lettre"
            else -> true to "OK"
        }
    }
    
    fun isValidPhoneNumber(phone: String): Boolean {
        // Format international ou local français
        val phonePattern = "^(\\+33|0)[1-9]\\d{8}$"
        return phone.isNotEmpty() && phone.matches(phonePattern.toRegex())
    }
    
    fun isValidShopName(name: String): Boolean {
        return name.isNotEmpty() && name.length >= 2
    }
}
