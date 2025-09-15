package com.seenu.dev.android.qr_craft.presentation.history.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.seenu.dev.android.qr_craft.R
import com.seenu.dev.android.qr_craft.presentation.scan_details.components.QrUiModelProvider
import com.seenu.dev.android.qr_craft.presentation.state.QrDataUiModel
import com.seenu.dev.android.qr_craft.presentation.state.formattedContent
import com.seenu.dev.android.qr_craft.presentation.state.getTypeItem
import com.seenu.dev.android.qr_craft.presentation.ui.theme.QrCraftTheme
import com.seenu.dev.android.qr_craft.presentation.ui.theme.onOverlay
import com.seenu.dev.android.qr_craft.presentation.ui.theme.onSurfaceAlt
import com.seenu.dev.android.qr_craft.presentation.ui.theme.onSurfaceDisabled
import com.seenu.dev.android.qr_craft.presentation.ui.theme.surfaceHigher

@Preview
@Composable
private fun QrDataItemPreview(
    @PreviewParameter(QrUiModelProvider::class) data: QrDataUiModel
) {
    QrCraftTheme {
        QrDataItem(qrData = data) {}
    }
}

@Composable
fun QrDataItem(
    modifier: Modifier = Modifier,
    qrData: QrDataUiModel,
    onFavIconClicked: (Boolean) -> Unit
) {
    val item = qrData.getTypeItem()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceHigher
            )
            .padding(12.dp)
    ) {
        Icon(
            painter = painterResource(item.iconRes),
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = item.backgroundColor,
                    shape = CircleShape
                )
                .padding(8.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
                .padding(start = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = qrData.customTitle.let {
                        if (it.isNullOrBlank()) {
                            item.title
                        } else {
                            it
                        }
                    },
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1F)
                )
                IconButton(onClick = {
                    onFavIconClicked(!qrData.isFavourite)
                }, modifier = Modifier.size(32.dp)) {
                    val (iconRes, tint) = if (qrData.isFavourite) {
                        R.drawable.ic_fav_filled to MaterialTheme.colorScheme.onSurface
                    } else {
                        R.drawable.ic_fav_outline to MaterialTheme.colorScheme.onSurfaceDisabled
                    }
                    Icon(
                        painter = painterResource(iconRes),
                        contentDescription = "Mark as favourite",
                        tint = tint
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = qrData.data.formattedContent(),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceAlt,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = qrData.createdAtLabel,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceDisabled,
                modifier = Modifier
            )
        }
    }
}