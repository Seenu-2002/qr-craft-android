package com.seenu.dev.android.qr_craft.presentation.create

import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seenu.dev.android.qr_craft.R
import com.seenu.dev.android.qr_craft.presentation.UiState
import com.seenu.dev.android.qr_craft.presentation.common.ProgressDialog
import com.seenu.dev.android.qr_craft.presentation.create.components.CreateQrForm
import com.seenu.dev.android.qr_craft.presentation.state.QrType
import com.seenu.dev.android.qr_craft.presentation.state.getStringRes
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateQrScreen(
    type: QrType,
    onQrGenerated: (id: Long) -> Unit,
    onNavigateBack: () -> Unit
) {

    val viewModel: CreateQrViewModel = koinViewModel()
    val qrDataState by viewModel.qrData.collectAsStateWithLifecycle()

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
            viewModel.insertQrData(data = data.rawValue)
        }

        when (val state = qrDataState) {
            is UiState.Empty -> {
                // No-op
            }

            is UiState.Loading -> {
                ProgressDialog {
                    Text(
                        text = stringResource(R.string.loading),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 8.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            is UiState.Success -> {
                LaunchedEffect(Unit) {
                    // Navigate back with the data
                    onQrGenerated(state.data.id)
                }
            }

            is UiState.Error -> {
                val context = LocalContext.current
                Toast.makeText(context, stringResource(R.string.error_create_qr), Toast.LENGTH_LONG)
                    .show()
                Timber.e("Error creating QR: ${state.message}")
            }
        }
    }
}

