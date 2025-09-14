package com.seenu.dev.android.qr_craft.presentation.scan_details.components

import android.R.attr.text
import android.graphics.Bitmap
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.colorspace.ColorSpaces
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
import com.seenu.dev.android.qr_craft.presentation.state.QrData
import com.seenu.dev.android.qr_craft.presentation.ui.theme.QrCraftTheme
import com.seenu.dev.android.qr_craft.presentation.ui.theme.surfaceHigher

fun mockImageBitmap(width: Int = 100, height: Int = 100): Bitmap {
    // Creates a solid-colored placeholder bitmap
    val bitmap = ImageBitmap(width, height, ImageBitmapConfig.Argb8888, true)
    val canvas = androidx.compose.ui.graphics.Canvas(bitmap)
    val paint = Paint().apply { color = Color.Red }
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    return bitmap.asAndroidBitmap()
}

object QrDataProvider : PreviewParameterProvider<QrData> {
    override val values: Sequence<QrData>
        get() = sequenceOf(
            QrData.Text(
                "QR Code detected: Meeting notes:\n" +
                        "- Review UI components\n" +
                        "- Finalize QR saving logic\n" +
                        "- Test gallery import feature",
                ""
            ),
            QrData.Url(
                "https://www.example.com",
                ""
            ),
            QrData.Contact(
                "John Doe", "+1234567890", "johndoe@yahoo.com",
                ""
            ),
            QrData.GeoLocation(
                37.7749, -122.4194,
                ""
            ),
            QrData.PhoneNumber(
                "+1234567890",
                ""
            ),
            QrData.Wifi(
                ssid = "MyWifiNetwork",
                password = "securepassword",
                encryptionType = "WPA",
                ""
            )
        )
}

@Preview
@Composable
private fun QrDetailsContentPreview(
    @PreviewParameter(QrDataProvider::class)
    data: QrData
) {
    QrCraftTheme {
        QrDetailsContent(qrData = data)
    }
}

// TODO: Expandable text feature
@Composable
fun QrDetailsContent(qrData: QrData, contentTopPadding: Dp = 16.dp, modifier: Modifier = Modifier) {
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
        val contentTextAlign =
            if (qrData is QrData.Text) {
                TextAlign.Start
            } else {
                TextAlign.Center
            }

        if (qrData is QrData.Url) {
            val content = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(color = MaterialTheme.colorScheme.primary),

                    ) {
                    append(qrData.url)
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
                text = qrData.toContentText(),
                textAlign = contentTextAlign,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {

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


private fun QrData.toContentText(): String {
    return when (this) {
        is QrData.Text -> this.text
        is QrData.Url -> this.url

        is QrData.Contact -> {
            """
                ${this.name}
                ${this.email}
                ${this.phone}
            """.trimIndent()
        }

        is QrData.GeoLocation -> {
            """
                ${this.latitude},${this.longitude}
            """.trimIndent()
        }

        is QrData.PhoneNumber -> this.phoneNumber
        is QrData.Wifi -> {
            """
                SSID: ${this.ssid}
                Password: ${this.password}
                Encryption type: ${this.encryptionType}
            """.trimIndent()
        }
    }
}

private fun QrData.getTitleRes(): Int {
    return when (this) {
        is QrData.Text -> R.string.title_text
        is QrData.Url -> R.string.title_link
        is QrData.Contact -> R.string.title_contact
        is QrData.GeoLocation -> R.string.title_geo
        is QrData.PhoneNumber -> R.string.title_phone
        is QrData.Wifi -> R.string.title_wifi
    }
}