package com.seenu.dev.android.qr_craft.presentation.scan_details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seenu.dev.android.qr_craft.R
import com.seenu.dev.android.qr_craft.presentation.state.QrDataUiModel
import com.seenu.dev.android.qr_craft.presentation.state.formattedContent
import com.seenu.dev.android.qr_craft.presentation.state.getTitleRes
import com.seenu.dev.android.qr_craft.presentation.ui.theme.QrCraftTheme
import com.seenu.dev.android.qr_craft.presentation.ui.theme.surfaceHigher

class QrUiModelProvider : PreviewParameterProvider<QrDataUiModel> {
    override val values: Sequence<QrDataUiModel>
        get() = sequenceOf(
            QrDataUiModel(
                id = 12L,
                customTitle = "Sample title",
                createdAtLabel = "2024-10-01 10:00",
                lastUpdatedAt = "2024-10-01 10:00",
                isScanned = true,
                data = QrDataUiModel.Data.Text(
                    text = "QR Code detected: Meeting notes:\n" +
                            "- Review UI components\n" +
                            "- Finalize QR saving logic\n" +
                            "- Test gallery import feature",
                )
            ),
            QrDataUiModel(
                id = 12L,
                customTitle = "Sample title",
                createdAtLabel = "2024-10-01 10:00",
                lastUpdatedAt = "2024-10-01 10:00",
                isScanned = true,
                data = QrDataUiModel.Data.Url("https://www.example.com"),
            ),
            QrDataUiModel(
                id = 12L,
                customTitle = "Sample title",
                createdAtLabel = "2024-10-01 10:00",
                lastUpdatedAt = "2024-10-01 10:00",
                isScanned = true,
                data = QrDataUiModel.Data.Contact("John Doe", "+1234567890", "johndoe@yahoo.com"),
            ),
            QrDataUiModel(
                id = 12L,
                customTitle = "Sample title",
                createdAtLabel = "2024-10-01 10:00",
                lastUpdatedAt = "2024-10-01 10:00",
                isScanned = true,
                QrDataUiModel.Data.GeoLocation(37.7749, -122.4194),
            ),
            QrDataUiModel(
                id = 12L,
                customTitle = "Sample title",
                createdAtLabel = "2024-10-01 10:00",
                lastUpdatedAt = "2024-10-01 10:00",
                isScanned = true,
                QrDataUiModel.Data.Phone("+1234567890"),
            ),
            QrDataUiModel(
                id = 12L,
                customTitle = "Sample title",
                createdAtLabel = "2024-10-01 10:00",
                lastUpdatedAt = "2024-10-01 10:00",
                isScanned = true,
                QrDataUiModel.Data.Wifi(
                    ssid = "MyWifiNetwork",
                    password = "securepassword",
                    encryptionType = "WPA",
                )
            )
        )
}

@Preview
@Composable
private fun QrDetailsContentPreview(
    @PreviewParameter(QrUiModelProvider::class)
    data: QrDataUiModel
) {
    QrCraftTheme {
        QrDetailsContent(qrData = data)
    }
}

// TODO: Expandable text feature
@Composable
fun QrDetailsContent(
    qrData: QrDataUiModel,
    modifier: Modifier = Modifier,
    contentTopPadding: Dp = 16.dp,
    onCopy: (data: QrDataUiModel) -> Unit = {},
    onShare: (data: QrDataUiModel) -> Unit = {},
) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium
            )
            .padding(top = contentTopPadding, start = 12.dp, bottom = 12.dp, end = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val title = stringResource(qrData.getTitleRes())
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))
        val data = qrData.data
        val contentTextAlign =
            if (data is QrDataUiModel.Data.Text) {
                TextAlign.Start
            } else {
                TextAlign.Center
            }

        if (data is QrDataUiModel.Data.Url) {
            val content = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(background = MaterialTheme.colorScheme.primary),
                ) {
                    append(data.url)
                }
            }
            Text(
                text = content,
                textAlign = contentTextAlign,
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = data.formattedContent(),
                textAlign = contentTextAlign,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    onShare(qrData)
                },
                modifier = Modifier
                    .weight(1F)
                    .padding(horizontal = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceHigher,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_share),
                        contentDescription = "Share",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.action_share),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
            Button(
                onClick = {
                    onCopy(qrData)
                }, modifier = Modifier
                    .weight(1F)
                    .padding(horizontal = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceHigher,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_copy),
                        contentDescription = "Copy",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.action_copy),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}