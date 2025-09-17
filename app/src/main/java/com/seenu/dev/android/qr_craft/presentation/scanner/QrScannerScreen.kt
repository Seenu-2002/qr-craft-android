package com.seenu.dev.android.qr_craft.presentation.scanner

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seenu.dev.android.qr_craft.R
import com.seenu.dev.android.qr_craft.framework.bitmap.BitmapDecoderFactory
import com.seenu.dev.android.qr_craft.framework.camera.QRCodeAnalyzer
import com.seenu.dev.android.qr_craft.framework.camera.QrCameraController
import com.seenu.dev.android.qr_craft.presentation.UiState
import com.seenu.dev.android.qr_craft.presentation.design_system.components.CustomSnackBar
import com.seenu.dev.android.qr_craft.presentation.design_system.components.PermissionDialog
import com.seenu.dev.android.qr_craft.presentation.design_system.components.PermissionTextProvider
import com.seenu.dev.android.qr_craft.presentation.scanner.components.NoQRFoundDialog
import com.seenu.dev.android.qr_craft.presentation.scanner.components.ScannerOverlay
import com.seenu.dev.android.qr_craft.presentation.ui.theme.onOverlay
import com.seenu.dev.android.qr_craft.presentation.ui.theme.onSurfaceDisabled
import com.seenu.dev.android.qr_craft.presentation.ui.theme.success
import com.seenu.dev.android.qr_craft.presentation.ui.theme.surfaceHigher
import com.seenu.dev.android.qr_craft.presentation.util.openAppSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import timber.log.Timber

@OptIn(ExperimentalGetImage::class)
@Composable
fun QrScannerScreen(
    openQrDetailsScreen: (id: Long) -> Unit,
    onCloseApp: () -> Unit
) {

    var isCameraPermissionGranted by remember {
        mutableStateOf(false)
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val viewModel = koinInject<QrScannerViewModel>()
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val qrDataState by viewModel.qrData.collectAsStateWithLifecycle(initialValue = UiState.Empty())

    var showNoQrFoundDialog by remember {
        mutableStateOf(false)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.resetState()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.qrData.collectLatest {
            when (it) {
                is UiState.Success -> {
                    Timber.d("QR Data received: ${it.data}")
                    openQrDetailsScreen(it.data.id)
                }

                is UiState.Error -> {
                    if (it.exp is QRCodeAnalyzer.QRCodeNotFoundException) {
                        showNoQrFoundDialog = true
                        scope.launch {
                            delay(2000)
                            showNoQrFoundDialog = false
                        }
                    } else {
                        Timber.e(it.exp, "QR code processing failed")
                        Toast.makeText(
                            context,
                            R.string.error_qr_processing,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    viewModel.resetState()
                }

                else -> {
                    // No-op
                }
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize(), snackbarHost = {
        SnackbarHost(hostState = snackBarHostState) { snackBarData ->
            CustomSnackBar(
                message = snackBarData.visuals.message,
                icon = Icons.Rounded.Check,
                containerColor = MaterialTheme.colorScheme.success
            )
        }
    }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            val snackBarMessage = stringResource(id = R.string.permission_camera_granted)
            AskCameraPermission(onGranted = { byUserAction ->
                if (byUserAction) {
                    Timber.d("Camera permission granted without user action")
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            message = snackBarMessage,
                        )
                    }
                }
                isCameraPermissionGranted = true
            }, onCloseApp = onCloseApp)

            if (!isCameraPermissionGranted) {
                return@Box
            }
            val isScannerEnabled = qrDataState is UiState.Empty

            val cameraController = remember {
                QrCameraController(
                    context = context,
                    lifecycleOwner = lifecycleOwner,
                    scope = scope,
                    onQrScanned = { data ->
                        viewModel.parseQrData(data)
                    },
                    onQrError = { exp ->
                        viewModel.throwQrAnalyzeError(exp)
                    }
                )
            }

            DisposableEffect(Unit) {
                onDispose {
                    cameraController.release()
                }
            }

            LaunchedEffect(isScannerEnabled, showNoQrFoundDialog) {
                if (showNoQrFoundDialog) {
                    cameraController.pauseAnalyzer()
                } else {
                    if (isScannerEnabled) {
                        cameraController.resumeAnalyzer()
                    } else {
                        cameraController.pauseAnalyzer()
                    }
                }
            }

            val hasFlash: Boolean = cameraController.hasFlash()
            val isFlashOn: Boolean by cameraController.torchState.collectAsStateWithLifecycle()

            AndroidView(
                modifier = Modifier
                    .fillMaxSize(),
                factory = { context ->
                    PreviewView(context)
                },
                update = { previewView ->
                    try {
                        cameraController.bindCamera(previewView)
                    } catch (exc: Exception) {
                        // Handle camera binding errors
                        Timber.e(exc, "Use case binding failed")
                        Toast.makeText(context, R.string.error_camera, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            )
            ScannerOverlay(
                modifier = Modifier.fillMaxSize(),
                showCameraBounds = isScannerEnabled
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopStart),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val flashIcon: Int
                val flashBgColor: Color
                if (isFlashOn) {
                    flashIcon = R.drawable.ic_zap_off
                    flashBgColor = MaterialTheme.colorScheme.primary
                } else {
                    flashIcon = R.drawable.ic_zap
                    flashBgColor = MaterialTheme.colorScheme.surfaceHigher
                }
                IconButton(
                    enabled = hasFlash,
                    onClick = {
                        if (hasFlash) {
                            cameraController.toggleFlash(!isFlashOn)
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = flashBgColor,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceDisabled
                    ),
                ) {
                    Icon(
                        painter = painterResource(flashIcon),
                        contentDescription = "Flash",
                        modifier = Modifier.size(16.dp)
                    )
                }

                var imageUri: Uri? by remember {
                    mutableStateOf(null)
                }
                val mediaPicker = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.PickVisualMedia(),
                    onResult = { uri ->
                        imageUri = uri
                    }
                )

                LaunchedEffect(imageUri) {
                    imageUri?.let { uri ->
                        val bitmap = BitmapDecoderFactory.getDecoder().decode(context, uri)
                        cameraController.analyzeBitmap(bitmap)
                    }
                    imageUri = null
                }

                IconButton(
                    onClick = {
                        mediaPicker.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }, colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceHigher,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_gallary),
                        contentDescription = "Pick image to scan from gallery",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            val isProcessingQr = qrDataState is UiState.Loading
            if (isProcessingQr) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = CenterHorizontally,
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onOverlay,
                    )
                    Text(
                        text = stringResource(R.string.loading),
                        color = MaterialTheme.colorScheme.onOverlay,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            if (showNoQrFoundDialog) {
                NoQRFoundDialog() {
                    showNoQrFoundDialog = false
                }
            }
        }
    }
}

@Composable
private fun AskCameraPermission(
    onGranted: (byUserAction: Boolean) -> Unit,
    onCloseApp: () -> Unit
) {

    val activity = LocalActivity.current ?: return
    val lifecycleOwner = LocalLifecycleOwner.current
    var isUserActionPerformed by remember {
        mutableStateOf(false)
    }
    var isCameraPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                Timber.d("Lifecycle ON_RESUME called")
                isCameraPermissionGranted = ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(isCameraPermissionGranted, isUserActionPerformed) {
        Timber.d("Is camera permission granted: $isCameraPermissionGranted by user action: $isUserActionPerformed")
        if (isCameraPermissionGranted) {
            onGranted(isUserActionPerformed)
        }
    }

    if (isCameraPermissionGranted) {
        return
    }

    var isCameraPermissionPermanentlyDenied by remember {
        mutableStateOf(false)
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                Timber.d("Camera permission granted by user action")
                isUserActionPerformed = true
                isCameraPermissionGranted = true
            } else {
                // Check if the permission is permanently denied
                isCameraPermissionPermanentlyDenied =
                    !shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)
            }
        }
    )

    PermissionDialog(
        textProvider = CameraPermissionTextProvider,
        isPermanentlyDenied = isCameraPermissionPermanentlyDenied,
        onOkClicked = {
            if (isCameraPermissionPermanentlyDenied) {
                activity.openAppSettings()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        },
        onAppCloseClicked = onCloseApp
    )
}

object CameraPermissionTextProvider : PermissionTextProvider {
    @Composable
    override fun getHeadingText(isPermanentlyDenied: Boolean): String {
        val context = LocalContext.current
        return context.getString(R.string.permission_camera_heading)
    }

    @Composable
    override fun getDescriptionText(isPermanentlyDenied: Boolean): String {
        val context = LocalContext.current
        return context.getString(R.string.permission_camera_message)
    }

}
