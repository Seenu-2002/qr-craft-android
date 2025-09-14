package com.seenu.dev.android.qr_craft.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.seenu.dev.android.qr_craft.R
import com.seenu.dev.android.qr_craft.presentation.ui.theme.QrCraftTheme
import com.seenu.dev.android.qr_craft.presentation.ui.theme.surfaceHigher

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionDialog(
    textProvider: PermissionTextProvider,
    isPermanentlyDenied: Boolean,
    onOkClicked: () -> Unit,
    onAppCloseClicked: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onAppCloseClicked,
        content = {
            PermissionDialogContent(
                heading = textProvider.getHeadingText(isPermanentlyDenied),
                description = textProvider.getDescriptionText(isPermanentlyDenied),
                onOkClicked = onOkClicked,
                onAppCloseClicked = onAppCloseClicked
            )
        }
    )
}

@Preview
@Composable
private fun PermissionDialogContentPreview() {
    QrCraftTheme {
        PermissionDialogContent(
            modifier = Modifier.width(300.dp),
            heading = "Camera Required",
            description = "This app cannot function without camera permission. To scan QR codes, please grant permission.",
            onOkClicked = {},
            onAppCloseClicked = {}
        )
    }
}

@Composable
private fun PermissionDialogContent(
    modifier: Modifier = Modifier,
    heading: String,
    description: String,
    onOkClicked: () -> Unit,
    onAppCloseClicked: () -> Unit,
) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.small
            )
            .padding(16.dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = heading,
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = description,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.permission_btn_close_app),
                modifier = Modifier
                    .weight(1F)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceHigher,
                        shape = MaterialTheme.shapes.large
                    )
                    .clip(
                        shape = MaterialTheme.shapes.large
                    )
                    .clickable { onAppCloseClicked() }
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.error,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.permission_btn_grant),
                modifier = Modifier
                    .weight(1F)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceHigher,
                        shape = MaterialTheme.shapes.large
                    )
                    .clip(
                        shape = MaterialTheme.shapes.large
                    )
                    .clickable { onOkClicked() }
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

interface PermissionTextProvider {
    @Composable
    fun getHeadingText(isPermanentlyDenied: Boolean): String
    @Composable
    fun getDescriptionText(isPermanentlyDenied: Boolean): String
}