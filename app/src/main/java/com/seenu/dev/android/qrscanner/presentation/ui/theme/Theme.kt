package com.seenu.dev.android.qrscanner.presentation.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    surface = Surface,
    onSurface = OnSurface,
    error = Error
)

val ColorScheme.surfaceHigher: Color
    get() = SurfaceHigher

val ColorScheme.onSurfaceAlt: Color
    get() = OnSurfaceAlt

val ColorScheme.overlay: Color
    get() = Overlay

val ColorScheme.onOverlay: Color
    get() = OnOverlay

val ColorScheme.link: Color
    get() = Link

val ColorScheme.linkBg: Color
    get() = LinkBg

val ColorScheme.success: Color
    get() = Success

val ColorScheme.text: Color
    get() = Text

val ColorScheme.textBg: Color
    get() = TextBg

val ColorScheme.contact: Color
    get() = Contact

val ColorScheme.contactBg: Color
    get() = ContactBg

val ColorScheme.geo: Color
    get() = Geo

val ColorScheme.geoBg: Color
    get() = GeoBg

val ColorScheme.phone: Color
    get() = Phone

val ColorScheme.phoneBg: Color
    get() = PhoneBg

val ColorScheme.wifi: Color
    get() = Wifi

val ColorScheme.wifiBg: Color
    get() = WifiBg

@Composable
fun QRScannerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}