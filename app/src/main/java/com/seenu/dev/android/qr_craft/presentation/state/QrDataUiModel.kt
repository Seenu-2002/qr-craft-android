package com.seenu.dev.android.qr_craft.presentation.state

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.seenu.dev.android.qr_craft.R
import kotlinx.serialization.Serializable

@Serializable
data class QrDataUiModel constructor(
    val id: Long,
    val customTitle: String?,
    val createdAtLabel: String,
    val lastUpdatedAt: String,
    val isScanned: Boolean,
    val data: Data
) {

    sealed interface Data {
        val rawValue: String

        @Serializable
        data class Text constructor(
            val text: String
        ) : Data {
            override val rawValue: String get() = text
        }

        @Serializable
        data class Url constructor(
            val url: String
        ) : Data {
            override val rawValue: String get() = url
        }

        @Serializable
        data class Contact constructor(
            val name: String,
            val phone: String? = null,
            val email: String? = null
        ) : Data {
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
        ) : Data {
            override val rawValue: String get() = "geo:$latitude,$longitude"
        }

        @Serializable
        data class Wifi constructor(
            val ssid: String,
            val password: String,
            val encryptionType: String? = "WPA"
        ) : Data {
            override val rawValue: String
                get() = "WIFI:S:$ssid;T:${encryptionType ?: ""};P:$password;;"
        }

        @Serializable
        data class Phone constructor(
            val phoneNumber: String
        ) : Data {
            override val rawValue: String get() = "tel:$phoneNumber"
        }
    }
}

@Composable
fun QrDataUiModel.Data.formattedContent(): String {
    return buildString {
        when (this@formattedContent) {
            is QrDataUiModel.Data.Text -> {
                append(text)
            }

            is QrDataUiModel.Data.Url -> {
                append(url)
            }

            is QrDataUiModel.Data.Contact -> {
                append("$name\n")
                phone?.let {
                    append("$it\n")
                }
                email?.let {
                    append(it)
                }
            }

            is QrDataUiModel.Data.GeoLocation -> {
                append("$latitude, $longitude")
            }

            is QrDataUiModel.Data.Wifi -> {
                append("${stringResource(R.string.title_ssid)}: $ssid\n")
                append("${stringResource(R.string.title_password)}: $password\n")
                append("${stringResource(R.string.title_encryption_type)}: ${encryptionType ?: ""}")
            }

            is QrDataUiModel.Data.Phone -> {
                append(phoneNumber)
            }
        }

    }
}

fun QrDataUiModel.getTitleRes(): Int {
    return when (this.data) {
        is QrDataUiModel.Data.Text -> R.string.title_text
        is QrDataUiModel.Data.Url -> R.string.title_link
        is QrDataUiModel.Data.Contact -> R.string.title_contact
        is QrDataUiModel.Data.GeoLocation -> R.string.title_geo
        is QrDataUiModel.Data.Phone -> R.string.title_phone
        is QrDataUiModel.Data.Wifi -> R.string.title_wifi_network
    }
}

@Composable
fun QrDataUiModel.getTypeItem(): QrTypeItem {
    return when (this.data) {
        is QrDataUiModel.Data.Text -> items[QrType.TEXT]!!
        is QrDataUiModel.Data.Url -> items[QrType.LINK]!!
        is QrDataUiModel.Data.Contact -> items[QrType.CONTACT]!!
        is QrDataUiModel.Data.GeoLocation -> items[QrType.GEO_LOCATION]!!
        is QrDataUiModel.Data.Phone -> items[QrType.PHONE]!!
        is QrDataUiModel.Data.Wifi -> items[QrType.WIFI]!!
    }
}