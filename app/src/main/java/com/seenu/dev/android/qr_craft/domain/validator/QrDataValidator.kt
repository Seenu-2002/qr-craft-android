package com.seenu.dev.android.qr_craft.domain.validator

import android.util.Patterns

object QrDataValidator {

    fun validateEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validatePhoneNumber(phone: String): Boolean {
        return Patterns.PHONE.matcher(phone).matches()
    }

    fun validateUrl(url: String): Boolean {
        return Patterns.WEB_URL.matcher(url).matches()
    }

    fun validateLatitude(value: String): Boolean {
        val lat = value.toDoubleOrNull() ?: return false
        return lat in -90.0..90.0
    }

    fun validateLongitude(value: String): Boolean {
        val lon = value.toDoubleOrNull() ?: return false
        return lon in -180.0..180.0
    }

    fun validateEncryptionType(type: String): Boolean {
        val validTypes = setOf("WEP", "WPA", "WPA2-EAP", "NOPASS")
        return type.uppercase() in validTypes
    }

}