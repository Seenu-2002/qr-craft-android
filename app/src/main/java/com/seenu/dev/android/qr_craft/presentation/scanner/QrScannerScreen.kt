package com.seenu.dev.android.qr_craft.presentation.scanner

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seenu.dev.android.qr_craft.R
import com.seenu.dev.android.qr_craft.domain.model.QrData
import com.seenu.dev.android.qr_craft.presentation.UiState
import com.seenu.dev.android.qr_craft.presentation.common.components.CustomSnackBar
import com.seenu.dev.android.qr_craft.presentation.common.components.PermissionDialog
import com.seenu.dev.android.qr_craft.presentation.common.components.PermissionTextProvider
import com.seenu.dev.android.qr_craft.presentation.common.openAppSettings
import com.seenu.dev.android.qr_craft.presentation.misc.QRCodeAnalyzer
import com.seenu.dev.android.qr_craft.presentation.scanner.components.ScannerOverlay
import com.seenu.dev.android.qr_craft.presentation.ui.theme.onOverlay
import com.seenu.dev.android.qr_craft.presentation.ui.theme.success
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import timber.log.Timber

@OptIn(ExperimentalGetImage::class)
@Composable
fun QrScannerScreen(
    openQrDetailsScreen: (data: QrData) -> Unit,
    onCloseApp: () -> Unit
) {

    var isCameraPermissionGranted by remember {
        mutableStateOf(false)
    }

    val viewModel = koinInject<QrScannerViewModel>()
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val qrDataState by viewModel.qrData.collectAsStateWithLifecycle(initialValue = UiState.Empty())

    LaunchedEffect(Unit) {
        viewModel.qrData.collectLatest {
            if (it is UiState.Success) {
                Timber.d("QR Data received: ${it.data}")
                openQrDetailsScreen(it.data)
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

            val lifecycleOwner = LocalLifecycleOwner.current
            val context = LocalContext.current
            val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
            val isProcessingQr = qrDataState is UiState.Loading

            val qrAnalyzer = remember {
                val listener = object : QRCodeAnalyzer.ProcessListener {

                    override fun onSuccess(qrData: String) {
                        viewModel.parseQrData(qrData)
                    }

                    override fun onFailure(exception: Exception) {
                        Timber.e(exception, "QR code processing failed")
                        Toast.makeText(
                            context,
                            R.string.error_qr_processing,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                }
                QRCodeAnalyzer(listener = listener)
            }
            LaunchedEffect(isProcessingQr) {
                if (isProcessingQr) {
                    qrAnalyzer.pause()
                } else {
                    qrAnalyzer.resume()
                }
            }

            AndroidView(
                modifier = Modifier
                    .fillMaxSize(),
                factory = { context ->
                    PreviewView(context)
                },
                update = { previewView ->
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build()
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    preview.surfaceProvider = previewView.surfaceProvider


                    val imageAnalyzer = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    imageAnalyzer.setAnalyzer(
                        ContextCompat.getMainExecutor(context),
                        qrAnalyzer
                    )

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalyzer
                        )
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
                showCameraBounds = !isProcessingQr
            )

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
