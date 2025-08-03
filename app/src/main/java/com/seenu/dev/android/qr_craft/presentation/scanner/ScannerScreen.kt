package com.seenu.dev.android.qr_craft.presentation.scanner

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.RectF
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.core.graphics.toRectF
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.seenu.dev.android.qr_craft.R
import com.seenu.dev.android.qr_craft.presentation.common.components.CustomSnackBar
import com.seenu.dev.android.qr_craft.presentation.common.components.PermissionDialog
import com.seenu.dev.android.qr_craft.presentation.common.components.PermissionTextProvider
import com.seenu.dev.android.qr_craft.presentation.common.components.openAppSettings
import com.seenu.dev.android.qr_craft.presentation.scanner.components.ScannerOverlay
import com.seenu.dev.android.qr_craft.presentation.ui.theme.success
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun ScannerScreen(onScanResult: (String) -> Unit, onCloseApp: () -> Unit) {

    var isCameraPermissionGranted by remember {
        mutableStateOf(false)
    }

    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
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

            HideNavAndStatusBar()

            val lifecycleOwner = LocalLifecycleOwner.current
            val context = LocalContext.current
            val cameraController = remember {
                LifecycleCameraController(context)
            }

            var barcodeBounds: RectF? by remember {
                mutableStateOf(null)
            }

            AndroidView(
                modifier = Modifier
                    .fillMaxSize(),
                factory = { context ->
                    PreviewView(context).apply {
                        cameraController.bindToLifecycle(lifecycleOwner)
                        this.controller = cameraController

                        val scannerOptions = BarcodeScannerOptions.Builder()
                            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                            .build()
                        val barcodeScanner = BarcodeScanning.getClient(scannerOptions)
                        cameraController.setImageAnalysisAnalyzer(
                            ContextCompat.getMainExecutor(context),
                            MlKitAnalyzer(
                                listOf(barcodeScanner),
                                ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED,
                                ContextCompat.getMainExecutor(context)
                            ) { result: MlKitAnalyzer.Result ->
                                val barcodeResults = result.getValue(barcodeScanner)

                                if (!barcodeResults.isNullOrEmpty()) {
                                    val barcode = barcodeResults.first()
                                    barcodeBounds = barcode.boundingBox?.toRectF()
                                } else {
                                    barcodeBounds = null
                                }
                            }
                        )
                    }
                }
            )

            ScannerOverlay(modifier = Modifier.fillMaxSize())

            if (barcodeBounds != null) {
                // Convert the Android Rect to a Compose Rect
                val composeRect = barcodeBounds?.toComposeRect()

                // Draw the rectangle on a Canvas if the rect is not null
                composeRect?.let {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRect(
                            color = Color.Red,
                            topLeft = Offset(it.left, it.top), // Set the top-left position
                            size = Size(it.width, it.height), // Set the size of the rectangle
                            style = Stroke(width = 5F) // Use a stroke style with a width of 5f
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HideNavAndStatusBar() {
    val activity = LocalActivity.current

    DisposableEffect(Unit) {
        val window = activity?.window
            ?: return@DisposableEffect onDispose {}
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)

        with(insetsController) {
            hide(WindowInsetsCompat.Type.statusBars())
            hide(WindowInsetsCompat.Type.navigationBars())
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        onDispose {
            with(insetsController) {
                show(WindowInsetsCompat.Type.statusBars())
                show(WindowInsetsCompat.Type.navigationBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
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
