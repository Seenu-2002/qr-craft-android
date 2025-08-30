package com.seenu.dev.android.qr_craft.presentation.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seenu.dev.android.qr_craft.domain.model.QrData
import com.seenu.dev.android.qr_craft.presentation.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import timber.log.Timber

class QrScannerViewModel : ViewModel(), KoinComponent {

    private val _qrData: MutableSharedFlow<UiState<QrData>> = MutableSharedFlow()
    val qrData: SharedFlow<UiState<QrData>> = _qrData.asSharedFlow()

    // Use Built-in functions to get the data.
    fun parseQrData(rawValue: String) {
        viewModelScope.launch {
            _qrData.emit(UiState.Loading())
            delay(500L) // Intentional delay
            val data = when {
                // http://https://pl-coding.mymemberspot.io
                rawValue.startsWith("http://") -> {
                    QrData.Url(rawValue.drop(7).trim())
                }

                rawValue.startsWith("https://") -> {
                    QrData.Url(rawValue.drop(8).trim())
                }

                rawValue.startsWith("BEGIN:VCARD") -> {
                    parseVCard(rawValue)
                }

                rawValue.startsWith("WIFI:", ignoreCase = true) -> {
                    parseWifi(rawValue)
                }

                // tel:+49 170 1234567
                rawValue.startsWith("TEL", ignoreCase = true) -> {
                    QrData.Phone(rawValue.drop(4).trim())
                }

                rawValue.startsWith("GEO:", ignoreCase = true) -> {
                    parseGeoLocation(rawValue)
                }

                // QR Code detected: Meeting notes:
                // - Review UI components
                // - Finalize QR saving logic
                // - Test gallery import feature
                else -> {
                    QrData.Text(rawValue)
                }
            }
            delay(500L)
            _qrData.emit(UiState.Success(data))
        }
    }

    //   geo:50.4501,30.5234
    private fun parseGeoLocation(rawValue: String): QrData {
        val coordinates = rawValue.removePrefix("GEO:").split(",")
        return if (coordinates.size == 2) {
            val latitude = coordinates[0].toDoubleOrNull()
            val longitude = coordinates[1].toDoubleOrNull()
            if (latitude != null && longitude != null) {
                QrData.GeoLocation(latitude, longitude)
            } else {
                Timber.e("Invalid GEO format: $rawValue")
                QrData.Text(rawValue)
            }
        } else {
            Timber.e("Invalid GEO format: $rawValue")
            QrData.Text(rawValue)
        }
    }


    //BEGIN:VCARD
    //VERSION:3.0
    //N:Olivia Schmidt
    //TEL:+1 (555) 284-7390
    //EMAIL:olivia.schmidt@example.com
    //END:VCARD
    private fun parseVCard(rawValue: String): QrData {
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
            QrData.Contact(
                name = name,
                phone = phone ?: "",
                email = email ?: ""
            )
        } else {
            Timber.e("Invalid vCard format: $rawValue")
            QrData.Text(rawValue)
        }
    }

    // WIFI:S:DevHub_WiFi;T:WPA;P:QrCraft2025;H:false;;
    private fun parseWifi(rawValue: String): QrData.Wifi {
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

        return QrData.Wifi(
            ssid = ssid ?: "Unknown SSID",
            password = password ?: "No password",
            encryptionType = encryptionType ?: "No encryption"
        )
    }

}