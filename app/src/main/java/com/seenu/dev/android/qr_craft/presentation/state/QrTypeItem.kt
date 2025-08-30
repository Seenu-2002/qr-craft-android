package com.seenu.dev.android.qr_craft.presentation.state

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.seenu.dev.android.qr_craft.R
import com.seenu.dev.android.qr_craft.presentation.ui.theme.contact
import com.seenu.dev.android.qr_craft.presentation.ui.theme.contactBg
import com.seenu.dev.android.qr_craft.presentation.ui.theme.link
import com.seenu.dev.android.qr_craft.presentation.ui.theme.linkBg
import com.seenu.dev.android.qr_craft.presentation.ui.theme.text
import com.seenu.dev.android.qr_craft.presentation.ui.theme.textBg

data class QrTypeItem(
    val type: QrType,
    val title: String,
    val tint: Color,
    val backgroundColor: Color,
    val iconRes: Int
)

enum class QrType {
    TEXT, LINK, CONTACT, PHONE, GEO_LOCATION, WIFI
}

val items: List<QrTypeItem>
    @Composable
    get() = listOf(
        QrTypeItem(
            type = QrType.TEXT,
            title = stringResource(R.string.title_text),
            tint = MaterialTheme.colorScheme.text,
            backgroundColor = MaterialTheme.colorScheme.textBg,
            iconRes = R.drawable.ic_text
        ),
        QrTypeItem(
            type = QrType.LINK,
            title = stringResource(R.string.title_link),
            tint = MaterialTheme.colorScheme.link,
            backgroundColor = MaterialTheme.colorScheme.linkBg,
            iconRes = R.drawable.ic_link
        ),
        QrTypeItem(
            type = QrType.CONTACT,
            title = stringResource(R.string.title_contact),
            tint = MaterialTheme.colorScheme.contact,
            backgroundColor = MaterialTheme.colorScheme.contactBg,
            iconRes = R.drawable.ic_user
        ),
        QrTypeItem(
            type = QrType.PHONE,
            title = stringResource(R.string.title_phone),
            tint = MaterialTheme.colorScheme.link,
            backgroundColor = MaterialTheme.colorScheme.linkBg,
            iconRes = R.drawable.ic_phone
        ),
        QrTypeItem(
            type = QrType.GEO_LOCATION,
            title = stringResource(R.string.title_geo),
            tint = MaterialTheme.colorScheme.link,
            backgroundColor = MaterialTheme.colorScheme.linkBg,
            iconRes = R.drawable.ic_marker_pin
        ),
        QrTypeItem(
            type = QrType.WIFI,
            title = stringResource(R.string.title_wifi),
            tint = MaterialTheme.colorScheme.link,
            backgroundColor = MaterialTheme.colorScheme.linkBg,
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