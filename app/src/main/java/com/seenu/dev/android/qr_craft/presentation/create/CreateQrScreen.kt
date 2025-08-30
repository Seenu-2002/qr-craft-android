package com.seenu.dev.android.qr_craft.presentation.create

import android.R.attr.type
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.seenu.dev.android.qr_craft.R
import com.seenu.dev.android.qr_craft.domain.model.QrData
import com.seenu.dev.android.qr_craft.presentation.create.components.CreateQrForm
import com.seenu.dev.android.qr_craft.presentation.state.QrType
import com.seenu.dev.android.qr_craft.presentation.state.getStringRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateQrScreen(type: QrType, onGenerateQr: (QrData) -> Unit, onNavigateBack: () -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    val titleRes = type.getStringRes()
                    Text(
                        text = stringResource(titleRes),
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_left),
                            contentDescription = stringResource(R.string.access_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }, containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        CreateQrForm(
            type = type, modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
        ) { data ->
            onGenerateQr(data)
        }
    }
}

