package com.seenu.dev.android.qr_craft.presentation.history.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.seenu.dev.android.qr_craft.R
import com.seenu.dev.android.qr_craft.presentation.ui.theme.QrCraftTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrHistoryItemActionBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = {}) {
        QrHistoryItemActionContent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            onShare = onShare,
            onDelete = onDelete
        )
    }
}

@Preview
@Composable
private fun QrHistoryItemActionContentPreview() {
    QrCraftTheme { QrHistoryItemActionContent(onShare = {}, onDelete = {}) }
}

@Composable
fun QrHistoryItemActionContent(
    modifier: Modifier = Modifier,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onShare()
                },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(16.dp),
                painter = painterResource(R.drawable.ic_share),
                contentDescription = "Share",
                tint = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.action_share),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .padding(vertical = 16.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onDelete()
                },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(16.dp),
                painter = painterResource(R.drawable.ic_delete),
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = stringResource(R.string.action_delete),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .padding(vertical = 16.dp)
            )
        }
    }
}

