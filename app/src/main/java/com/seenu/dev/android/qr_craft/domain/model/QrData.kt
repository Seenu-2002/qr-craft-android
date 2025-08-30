package com.seenu.dev.android.qr_craft.domain.model

import kotlinx.serialization.Serializable

@Serializable
sealed class QrData {

    abstract val rawValue: String

    @Serializable
    data class Text constructor(
        val text: String
    ) : QrData() {
        override val rawValue: String get() = text
    }

    @Serializable
    data class Url constructor(
        val url: String
    ) : QrData() {
        override val rawValue: String get() = url
    }

    @Serializable
    data class Contact constructor(
        val name: String,
        val phone: String? = null,
        val email: String? = null
    ) : QrData() {
        override val rawValue: String
            get() {
                val parts = buildList {
                    add("MECARD:N:$name")
                    phone?.let { add("TEL:$it") }
                    email?.let { add("EMAIL:$it") }
                }
                return parts.joinToString(";") + ";"
            }
    }

    @Serializable
    data class GeoLocation constructor(
        val latitude: Double,
        val longitude: Double
    ) : QrData() {
        override val rawValue: String get() = "geo:$latitude,$longitude"
    }

    @Serializable
    data class Wifi constructor(
        val ssid: String,
        val password: String,
        val encryptionType: String? = "WPA"
    ) : QrData() {
        override val rawValue: String
            get() = "WIFI:S:$ssid;T:${encryptionType ?: ""};P:$password;;"
    }

    @Serializable
    data class Phone constructor(
        val phoneNumber: String
    ) : QrData() {
        override val rawValue: String get() = "tel:$phoneNumber"
    }
}
