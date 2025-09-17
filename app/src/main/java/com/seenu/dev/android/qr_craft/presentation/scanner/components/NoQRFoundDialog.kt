package com.seenu.dev.android.qr_craft.presentation.scanner.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.seenu.dev.android.qr_craft.R
import com.seenu.dev.android.qr_craft.presentation.ui.theme.QrCraftTheme
import com.seenu.dev.android.qr_craft.presentation.ui.theme.surfaceHigher

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoQRFoundDialog(onDismissRequest: () -> Unit = {}) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        content = {
            NoQRFoundDialogContent()
        }
    )
}

@Preview
@Composable
private fun NoQRFoundDialogContentPreview() {
    QrCraftTheme {
        NoQRFoundDialogContent()
    }
}

@Composable
fun NoQRFoundDialogContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceHigher,
                shape = MaterialTheme.shapes.medium
            )
            .padding(horizontal = 60.dp, 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_alert),
            contentDescription = "No QR found",
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.no_qr_found),
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge
        )
    }
}