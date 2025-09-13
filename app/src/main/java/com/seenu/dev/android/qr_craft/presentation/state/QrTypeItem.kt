package com.seenu.dev.android.qr_craft.presentation.state

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.seenu.dev.android.qr_craft.R
import com.seenu.dev.android.qr_craft.presentation.ui.theme.contact
import com.seenu.dev.android.qr_craft.presentation.ui.theme.contactBg
import com.seenu.dev.android.qr_craft.presentation.ui.theme.geo
import com.seenu.dev.android.qr_craft.presentation.ui.theme.geoBg
import com.seenu.dev.android.qr_craft.presentation.ui.theme.link
import com.seenu.dev.android.qr_craft.presentation.ui.theme.linkBg
import com.seenu.dev.android.qr_craft.presentation.ui.theme.phone
import com.seenu.dev.android.qr_craft.presentation.ui.theme.phoneBg
import com.seenu.dev.android.qr_craft.presentation.ui.theme.text
import com.seenu.dev.android.qr_craft.presentation.ui.theme.textBg
import com.seenu.dev.android.qr_craft.presentation.ui.theme.wifi
import com.seenu.dev.android.qr_craft.presentation.ui.theme.wifiBg

data class QrTypeItem constructor(
    val type: QrType,
    val title: String,
    val tint: Color,
    val backgroundColor: Color,
    val iconRes: Int
)

enum class QrType {
    TEXT, LINK, CONTACT, PHONE, GEO_LOCATION, WIFI
}


// TODO: Should not cache this data as it retain during config changes
val items: Map<QrType, QrTypeItem>
    @Composable
    get() = mapOf(
        QrType.TEXT to QrTypeItem(
            type = QrType.TEXT,
            title = stringResource(R.string.title_text),
            tint = MaterialTheme.colorScheme.text,
            backgroundColor = MaterialTheme.colorScheme.textBg,
            iconRes = R.drawable.ic_text
        ),
        QrType.LINK to QrTypeItem(
            type = QrType.LINK,
            title = stringResource(R.string.title_link),
            tint = MaterialTheme.colorScheme.link,
            backgroundColor = MaterialTheme.colorScheme.linkBg,
            iconRes = R.drawable.ic_link
        ),
        QrType.CONTACT to QrTypeItem(
            type = QrType.CONTACT,
            title = stringResource(R.string.title_contact),
            tint = MaterialTheme.colorScheme.contact,
            backgroundColor = MaterialTheme.colorScheme.contactBg,
            iconRes = R.drawable.ic_user
        ),
        QrType.PHONE to QrTypeItem(
            type = QrType.PHONE,
            title = stringResource(R.string.title_phone),
            tint = MaterialTheme.colorScheme.phone,
            backgroundColor = MaterialTheme.colorScheme.phoneBg,
            iconRes = R.drawable.ic_phone
        ),
        QrType.GEO_LOCATION to QrTypeItem(
            type = QrType.GEO_LOCATION,
            title = stringResource(R.string.title_geo),
            tint = MaterialTheme.colorScheme.geo,
            backgroundColor = MaterialTheme.colorScheme.geoBg,
            iconRes = R.drawable.ic_marker_pin
        ),
        QrType.WIFI to QrTypeItem(
            type = QrType.WIFI,
            title = stringResource(R.string.title_wifi),
            tint = MaterialTheme.colorScheme.wifi,
            backgroundColor = MaterialTheme.colorScheme.wifiBg,
            iconRes = R.drawable.ic_wifi
        )
    )

fun QrType.getStringRes(): Int {
    return when (this) {
        QrType.TEXT -> R.string.title_text
        QrType.LINK -> R.string.title_link
        QrType.CONTACT -> R.string.title_contact
        QrType.PHONE -> R.string.title_phone
        QrType.GEO_LOCATION -> R.string.title_geo
        QrType.WIFI -> R.string.title_wifi
    }
}