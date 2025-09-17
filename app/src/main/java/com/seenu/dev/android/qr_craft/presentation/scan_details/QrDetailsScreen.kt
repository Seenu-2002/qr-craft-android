package com.seenu.dev.android.qr_craft.presentation.scan_details

import android.graphics.Bitmap
import android.os.Build
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seenu.dev.android.qr_craft.R
import com.seenu.dev.android.qr_craft.presentation.UiState
import com.seenu.dev.android.qr_craft.presentation.design_system.components.CustomSnackBar
import com.seenu.dev.android.qr_craft.presentation.design_system.LocalDimen
import com.seenu.dev.android.qr_craft.presentation.mapper.toUiModel
import com.seenu.dev.android.qr_craft.presentation.state.QrDataUiModel
import com.seenu.dev.android.qr_craft.presentation.misc.QrGenerator
import com.seenu.dev.android.qr_craft.presentation.scan_details.components.QrDetailsContent
import com.seenu.dev.android.qr_craft.presentation.state.getTitleRes
import com.seenu.dev.android.qr_craft.presentation.ui.theme.onOverlay
import com.seenu.dev.android.qr_craft.presentation.ui.theme.onSurfaceDisabled
import com.seenu.dev.android.qr_craft.presentation.ui.theme.success
import com.seenu.dev.android.qr_craft.presentation.ui.theme.surfaceHigher
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrDetailsScreen(
    isPreview: Boolean,
    id: Long,
    onCopyData: (data: QrDataUiModel) -> Unit = {},
    onShareData: (data: QrDataUiModel) -> Unit = {},
    onSave: (title: String, bitmap: Bitmap) -> Unit = { _, _ -> },
    onBackPressed: () -> Unit = {}
) {

    val viewModel: QrDetailViewModel = koinViewModel()
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val qrDataState by viewModel.qrData.collectAsStateWithLifecycle()

    var title: String? by remember {
        mutableStateOf(null)
    }

    LaunchedEffect(Unit) {
        if (qrDataState is UiState.Empty) {
            viewModel.getQrData(id)
        }
    }

    fun saveTitle() {
        if (qrDataState is UiState.Success) {
            val data = (qrDataState as UiState.Success).data
            if (data.customTitle != title && !title.isNullOrBlank()) {
                viewModel.updateTitle(data.id, title!!.trim())
            }
        }
    }

    BackHandler {
        saveTitle()
        onBackPressed()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState) { snackBarData ->
                CustomSnackBar(
                    message = snackBarData.visuals.message,
                    icon = Icons.Rounded.Check,
                    containerColor = MaterialTheme.colorScheme.success
                )
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            saveTitle()
                            onBackPressed()
                        }
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
                actions = {
                    val qrData = (qrDataState as? UiState.Success)?.data?.toUiModel()
                        ?: return@CenterAlignedTopAppBar
                    val isFavourite = qrData.isFavourite
                    IconButton(onClick = {
                        viewModel.updateFavorite(qrData.id, !isFavourite)
                    }) {
                        val (iconRes, tint) = if (isFavourite) {
                            R.drawable.ic_fav_filled to MaterialTheme.colorScheme.onOverlay
                        } else {
                            R.drawable.ic_fav_outline to MaterialTheme.colorScheme.onSurfaceDisabled
                        }

                        Icon(
                            painter = painterResource(iconRes),
                            contentDescription = "Mark as favourite",
                            tint = tint
                        )
                    }
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
                .fillMaxWidth()
                .wrapContentSize()
                .padding(innerPadding)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            val dimen = LocalDimen.current
            when (val state = qrDataState) {
                is UiState.Empty, is UiState.Loading -> {
                    CircularProgressIndicator()
                }

                is UiState.Success -> {
                    val uiModel = state.data.toUiModel()
                    LaunchedEffect(uiModel.customTitle) {
                        title = uiModel.customTitle
                    }
                    QrDetailSuccessContent(
                        modifier = Modifier.let {
                            if (dimen.scanDetailPage.cardWidth == null) {
                                it.fillMaxWidth()
                            } else {
                                it.width(dimen.scanDetailPage.cardWidth)
                            }
                        },
                        qrData = uiModel,
                        title = title,
                        onTitleChange = { newTitle ->
                            val oldTitle = title
                            if (newTitle.length < (oldTitle?.length ?: 0)) {
                                title = newTitle
                            } else if ((oldTitle?.length ?: 0) <= 32) {
                                title = newTitle
                            }
                        },
                        onCopyData = onCopyData,
                        onShareData = onShareData,
                        onSave = { title, bitmap ->
                            val snackBarMessage =
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    try {
                                        onSave(title, bitmap)
                                        context.getString(R.string.qr_save_success)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        context.getString(R.string.qr_save_error)
                                    }
                                } else {
                                    context.getString(R.string.qr_save_not_supported)
                                }
                            scope.launch {
                                snackBarHostState.showSnackbar(
                                    message = snackBarMessage,
                                )
                            }
                        }
                    )
                }

                is UiState.Error -> {
                    Text(
                        text = state.exp?.message ?: "Unknown Error",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun QrDetailSuccessContent(
    modifier: Modifier = Modifier,
    qrData: QrDataUiModel,
    title: String?,
    onTitleChange: (String) -> Unit,
    onCopyData: (data: QrDataUiModel) -> Unit,
    onShareData: (data: QrDataUiModel) -> Unit,
    onSave: (title: String, bitmap: Bitmap) -> Unit = { _, _ -> }
) {
    val size = with(LocalDensity.current) {
        144.dp.roundToPx()
    }
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
        Image(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            bitmap = QrGenerator.generate(
                qrData.data.rawValue,
                size
            )?.asImageBitmap()!!,
            contentDescription = "Qr Image"
        )
    }

    val defaultTitle = stringResource(qrData.getTitleRes())
    QrDetailsContent(
        modifier = modifier
            .offset(y = 140.dp),
        contentTopPadding = 82.dp,
        qrData = qrData,
        title = title,
        onTitleChange = onTitleChange,
        onCopy = onCopyData,
        onShare = onShareData,
        onSave = { data ->
            val bitmap = QrGenerator.generate(
                data.data.rawValue,
                size = size
            )!!
            onSave(data.customTitle ?: defaultTitle, bitmap)
        }
    )
}