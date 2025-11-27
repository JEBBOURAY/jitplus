package com.jitplus.merchant.utils

import android.util.Log

object ErrorHandler {
    
    fun getHttpErrorMessage(code: Int): String {
        return when (code) {
            400 -> "Requête invalide. Vérifiez les données saisies"
            401 -> "Email ou mot de passe incorrect"
            403 -> "Accès interdit"
            404 -> "Service non disponible"
            408 -> "Délai d'attente dépassé"
            409 -> "Conflit - Ces données existent déjà"
            422 -> "Données invalides"
            429 -> "Trop de tentatives. Réessayez plus tard"
            in 500..599 -> "Erreur serveur. Réessayez plus tard"
            else -> "Erreur de connexion (code: $code)"
        }
    }
    
    fun getNetworkErrorMessage(throwable: Throwable): String {
        return when (throwable) {
            is java.net.ConnectException -> "Impossible de se connecter au serveur. Vérifiez que le backend est démarré"
            is java.net.SocketTimeoutException -> "Délai d'attente dépassé. Vérifiez votre connexion"
            is java.net.UnknownHostException -> "Serveur introuvable. Vérifiez votre connexion Internet"
            is javax.net.ssl.SSLException -> "Erreur de sécurité SSL"
            else -> "Erreur réseau: ${throwable.localizedMessage ?: "Inconnue"}"
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
