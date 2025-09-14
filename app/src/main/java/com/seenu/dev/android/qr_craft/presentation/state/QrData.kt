package com.seenu.dev.android.qr_craft.presentation.state

import androidx.compose.runtime.Stable

sealed class QrData constructor(open val rawValue: String) {

    @Stable
    data class Text constructor(
        val text: String,
        override val rawValue: String
    ) : QrData(rawValue)

    @Stable
    data class Url constructor(
        val url: String,
        override val rawValue: String
    ) : QrData(rawValue)

    @Stable
    data class Contact constructor(
        val name: String,
        val phone: String? = null,
        val email: String? = null,
        override val rawValue: String
    ) : QrData(rawValue)

    @Stable
    data class GeoLocation constructor(
        val latitude: Double,
        val longitude: Double,
        override val rawValue: String
    ) : QrData(rawValue)

    @Stable
    data class Wifi constructor(
        val ssid: String,
        val password: String,
        val encryptionType: String? = null,
        override val rawValue: String
    ) : QrData(rawValue)

    @Stable
    data class PhoneNumber constructor(
        val phoneNumber: String,
        override val rawValue: String
    ) : QrData(rawValue)

}