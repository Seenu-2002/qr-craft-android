package com.seenu.dev.android.qr_craft.presentation.mapper

import com.seenu.dev.android.qr_craft.domain.model.QrData
import com.seenu.dev.android.qr_craft.presentation.state.QrDataUiModel
import timber.log.Timber

object QrUiModelDataParser {

    fun parse(qrData: QrData): QrDataUiModel.Data {
        val rawValue = qrData.data.trim()
        return when {
            // http://https://pl-coding.mymemberspot.io
            rawValue.startsWith("http://") -> {


                QrDataUiModel.Data.Url(
                    url = rawValue.drop(7).trim()
                )
            }

            rawValue.startsWith("https://") -> {
                QrDataUiModel.Data.Url(rawValue)
            }

            rawValue.startsWith("BEGIN:VCARD") -> {
                parseVCard(rawValue)
            }

            rawValue.startsWith("WIFI:", ignoreCase = true) -> {
                parseWifi(rawValue)
            }

            // tel:+49 170 1234567
            rawValue.startsWith("TEL", ignoreCase = true) -> {
                QrDataUiModel.Data.Phone(rawValue.drop(4).trim())
            }

            rawValue.startsWith("GEO:", ignoreCase = true) -> {
                parseGeoLocation(rawValue)
            }

            // QR Code detected: Meeting notes:
            // - Review UI components
            // - Finalize QR saving logic
            // - Test gallery import feature
            else -> {
                QrDataUiModel.Data.Text(rawValue)
            }
        }
    }

    //   geo:50.4501,30.5234
    private fun parseGeoLocation(rawValue: String): QrDataUiModel.Data {
        val coordinates = rawValue.removePrefix("GEO:").split(",")
        return if (coordinates.size == 2) {
            val latitude = coordinates[0].toDoubleOrNull()
            val longitude = coordinates[1].toDoubleOrNull()
            if (latitude != null && longitude != null) {
                QrDataUiModel.Data.GeoLocation(latitude, longitude)
            } else {
                Timber.e("Invalid GEO format: $rawValue")
                QrDataUiModel.Data.Text(rawValue)
            }
        } else {
            Timber.e("Invalid GEO format: $rawValue")
            QrDataUiModel.Data.Text(rawValue)
        }
    }


    //BEGIN:VCARD
    //VERSION:3.0
    //N:Olivia Schmidt
    //TEL:+1 (555) 284-7390
    //EMAIL:olivia.schmidt@example.com
    //END:VCARD
    private fun parseVCard(rawValue: String): QrDataUiModel.Data {
        val data = rawValue.split("\n")

        var name: String? = null
        var phone: String? = null
        var email: String? = null
        for (line in data) {
            when {
                line.startsWith("N:") -> {
                    name = line.removePrefix("N:").trim()
                }

                line.startsWith("TEL:") -> {
                    phone = line.removePrefix("TEL:").trim()
                }

                line.startsWith("EMAIL:") -> {
                    email = line.removePrefix("EMAIL:").trim()
                }
            }
        }

        return if (name != null && (email != null || phone != null)) {
            QrDataUiModel.Data.Contact(
                name = name,
                phone = phone ?: "",
                email = email ?: ""
            )
        } else {
            Timber.e("Invalid vCard format: $rawValue")
            QrDataUiModel.Data.Text(rawValue)
        }
    }

    // WIFI:S:DevHub_WiFi;T:WPA;P:QrCraft2025;H:false;;
    private fun parseWifi(rawValue: String): QrDataUiModel.Data.Wifi {
        val data = rawValue.removePrefix("WIFI:").split(";")
        var ssid: String? = null
        var password: String? = null
        var encryptionType: String? = null

        for (line in data) {
            when {
                line.startsWith("S:") -> {
                    ssid = line.removePrefix("S:").trim()
                }

                line.startsWith("P:") -> {
                    password = line.removePrefix("P:").trim()
                }

                line.startsWith("T:") -> {
                    encryptionType = line.removePrefix("T:").trim()
                }
            }
        }

        return QrDataUiModel.Data.Wifi(
            ssid = ssid ?: "Unknown SSID",
            password = password ?: "No password",
            encryptionType = encryptionType ?: "No encryption"
        )
    }
}