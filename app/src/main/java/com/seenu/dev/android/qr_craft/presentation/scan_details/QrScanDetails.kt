package com.seenu.dev.android.qr_craft.presentation.scan_details

import android.R.attr.data
import android.graphics.Bitmap
import android.widget.Toast
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.seenu.dev.android.qr_craft.R
import com.seenu.dev.android.qr_craft.presentation.common.QrDetailsViewModel
import com.seenu.dev.android.qr_craft.presentation.scan_details.components.QrDetailsContent
import com.seenu.dev.android.qr_craft.presentation.state.QrData
import com.seenu.dev.android.qr_craft.presentation.ui.theme.QrCraftTheme
import com.seenu.dev.android.qr_craft.presentation.ui.theme.onOverlay
import com.seenu.dev.android.qr_craft.presentation.ui.theme.surfaceHigher
import org.koin.compose.viewmodel.koinActivityViewModel
import androidx.core.graphics.createBitmap
import com.seenu.dev.android.qr_craft.presentation.misc.QrGenerator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScanDetailsScreen(qrDetailsViewModel: QrDetailsViewModel, onBackPressed: () -> Unit = {}) {

    val qrData = qrDetailsViewModel.qrData.collectAsState().value

    val context = LocalContext.current
    if (qrData == null) {
        Toast.makeText(context, stringResource(R.string.invalid_qr_message), Toast.LENGTH_SHORT)
            .show()
        return
    }

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
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
                Text(
                    text = stringResource(R.string.scan_result),
                    style = MaterialTheme.typography.titleMedium
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.onSurface,
                titleContentColor = MaterialTheme.colorScheme.onOverlay
            )
        )
    }, containerColor = MaterialTheme.colorScheme.onSurface) { innerPadding ->
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
                    .offset(y = 128.dp),
                contentTopPadding = 82.dp,
                qrData = qrData
            )
        }
    }
}