package com.seenu.dev.android.qr_craft.presentation.scan_details

import android.content.ClipData
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.seenu.dev.android.qr_craft.R
import com.seenu.dev.android.qr_craft.domain.model.QrData
import com.seenu.dev.android.qr_craft.presentation.misc.QrGenerator
import com.seenu.dev.android.qr_craft.presentation.scan_details.components.QrDetailsContent
import com.seenu.dev.android.qr_craft.presentation.ui.theme.onOverlay
import com.seenu.dev.android.qr_craft.presentation.ui.theme.surfaceHigher
import androidx.core.net.toUri
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScanDetailsScreen(
    isPreview: Boolean,
    qrData: QrData,
    onCopyData: (data: QrData) -> Unit = {},
    onShareData: (data: QrData) -> Unit = {},
    onBackPressed: () -> Unit = {}
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onBackPressed
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_left),
                            contentDescription = "Back to scanner",
                            tint = MaterialTheme.colorScheme.onOverlay
                        )
                    }
                },
                title = {
                    val textRes = if (isPreview) {
                        R.string.preview
                    } else {
                        R.string.scan_result
                    }
                    Text(
                        text = stringResource(textRes),
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.onSurface,
                    titleContentColor = MaterialTheme.colorScheme.onOverlay
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.onSurface
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .wrapContentSize()
                .padding(innerPadding)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceHigher,
                        shape = MaterialTheme.shapes.small
                    )
                    .zIndex(1F),
                contentAlignment = Alignment.Center
            ) {
                val size = with(LocalDensity.current) {
                    144.dp.roundToPx()
                }
                Image(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    bitmap = QrGenerator.generate(
                        qrData.rawValue,
                        size
                    )?.asImageBitmap()!!,
                    contentDescription = "Qr Image"
                )
            }

            QrDetailsContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = 140.dp),
                contentTopPadding = 82.dp,
                qrData = qrData,
                onCopy = onCopyData,
                onShare = onShareData
            )
        }
    }
}