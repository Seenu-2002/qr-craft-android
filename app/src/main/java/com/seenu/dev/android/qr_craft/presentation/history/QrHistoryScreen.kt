package com.seenu.dev.android.qr_craft.presentation.history

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seenu.dev.android.qr_craft.R
import com.seenu.dev.android.qr_craft.presentation.UiState
import com.seenu.dev.android.qr_craft.presentation.common.components.SlidingSwitch
import com.seenu.dev.android.qr_craft.presentation.design_system.LocalDimen
import com.seenu.dev.android.qr_craft.presentation.history.components.QrDataItemList
import com.seenu.dev.android.qr_craft.presentation.history.components.QrHistoryItemActionBottomSheet
import com.seenu.dev.android.qr_craft.presentation.mapper.toUiModel
import com.seenu.dev.android.qr_craft.presentation.state.QrDataUiModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrHistoryScreen(
    openQrDetail: (id: Long) -> Unit,
    onShareQr: (QrDataUiModel) -> Unit
) {

    val viewModel: QrHistoryViewModel = koinViewModel()
    val context = LocalContext.current
    val selectedOption by viewModel.selectedOption.collectAsStateWithLifecycle()
    val qrHistoryItems by viewModel.qrHistoryData.collectAsStateWithLifecycle()
    val itemLongPressed by viewModel.itemLongPressed.collectAsStateWithLifecycle()
    val updateStatus by viewModel.deleteStatus.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (qrHistoryItems is UiState.Empty) {
            viewModel.selectOption(HistoryType.SCANNED)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.scan_history),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        val dimen = LocalDimen.current
        Box(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val selected = remember(selectedOption) {
                    selectedOption.toOptionString(context)
                }
                val options = remember {
                    HistoryType.entries.map { it.toOptionString(context) }
                }

                SlidingSwitch(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    selected = selected,
                    options = options
                ) { index, _ ->
                    val option = HistoryType.entries[index]
                    viewModel.selectOption(option)
                }

                when (val state = qrHistoryItems) {
                    is UiState.Empty, is UiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1F),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is UiState.Success -> {
                        val hapticFeedback = LocalHapticFeedback.current
                        QrDataItemList(
                            modifier = Modifier
                                .let {
                                    if (dimen.qrHistoryPage.contentWidth == null) {
                                        it.fillMaxSize()
                                    } else {
                                        it.width(dimen.qrHistoryPage.contentWidth)
                                    }
                                }
                                .weight(1F)
                                .padding(horizontal = 12.dp),
                            qrItems = state.data.map { it.toUiModel() },
                            onItemClick = { item ->
                                openQrDetail(item.id)
                            },
                            onItemLongPress = { item ->
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.onItemLongPressed(item)
                            },
                            onFavIconClicked = { item, isFav ->
                                viewModel.updateFavorite(item.id, isFav)
                            }
                        )
                    }

                    is UiState.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1F),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.error_fetching_history),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }


        if (itemLongPressed != null) {
            val sheetState = rememberModalBottomSheetState()
            QrHistoryItemActionBottomSheet(
                modifier = Modifier.let {
                    if (dimen.qrHistoryPage.bottomSheetWidth == null) {
                        it
                    } else {
                        it.width(dimen.qrHistoryPage.bottomSheetWidth)
                    }
                },
                sheetState = sheetState,
                onDismissRequest = {
                    viewModel.clearItemLongPressed()
                },
                onShare = {
                    itemLongPressed?.let {
                        onShareQr(it)
                    }
                    viewModel.clearItemLongPressed()
                },
                onDelete = {
                    itemLongPressed?.let {
                        viewModel.delete(it)
                    }
                }
            )
        }

        val context = LocalContext.current
        when (updateStatus) {
            is UiState.Success -> {
                val message = context.getString(R.string.item_deleted_success)
                LaunchedEffect(Unit) {
                    Toast
                        .makeText(context, message, Toast.LENGTH_SHORT)
                        .show()
                    viewModel.clearDeleteStatus()
                    viewModel.clearItemLongPressed()
                }
            }

            is UiState.Error -> {
                val message = context.getString(R.string.item_deleted_error)
                LaunchedEffect(Unit) {
                    Toast
                        .makeText(context, message, Toast.LENGTH_SHORT)
                        .show()
                    viewModel.clearDeleteStatus()
                    viewModel.clearItemLongPressed()
                }
            }

            else -> {
                // Do nothing
            }
        }
    }
}

enum class HistoryType {
    SCANNED,
    GENERATED
}

fun HistoryType.toOptionString(context: Context): String {
    return when (this) {
        HistoryType.SCANNED -> context.getString(R.string.scanned)
        HistoryType.GENERATED -> context.getString(R.string.generated)
    }
}
