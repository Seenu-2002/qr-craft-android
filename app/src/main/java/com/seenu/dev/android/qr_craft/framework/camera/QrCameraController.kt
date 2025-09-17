package com.seenu.dev.android.qr_craft.framework.camera

import android.content.Context
import android.widget.Toast
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asFlow
import com.seenu.dev.android.qr_craft.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QrCameraController(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val scope: CoroutineScope,
    private val onQrScanned: (String) -> Unit,
    private val onQrError: (Exception) -> Unit
) {

    private var camera: Camera? = null
    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    private val _torchState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val torchState: StateFlow<Boolean> = _torchState.asStateFlow()

    private val qrAnalyzer = QRCodeAnalyzer(object : QRCodeAnalyzer.ProcessListener {
        override fun onSuccess(qrData: String) = onQrScanned(qrData)
        override fun onFailure(exception: Exception) = onQrError(exception)
    })

    fun bindCamera(previewView: PreviewView) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().apply {
            surfaceProvider = previewView.surfaceProvider
        }
        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .apply { setAnalyzer(cameraExecutor, qrAnalyzer) }

        try {
            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageAnalysis
            )
            scope.launch {
                camera?.cameraInfo?.torchState?.asFlow()?.collect { state ->
                    _torchState.value = state == TorchState.ON
                }
            }
        } catch (exc: Exception) {
            Timber.e(exc, "Camera binding failed")
            Toast.makeText(context, R.string.error_camera, Toast.LENGTH_SHORT).show()
        }
    }

    fun pauseAnalyzer() = qrAnalyzer.pause()
    fun resumeAnalyzer() = qrAnalyzer.resume()
    fun analyzeBitmap(bitmap: android.graphics.Bitmap) = qrAnalyzer.analyze(bitmap)
    fun hasFlash(): Boolean = camera?.cameraInfo?.hasFlashUnit() ?: false
    fun isFlashOn(): Boolean = camera?.cameraInfo?.torchState?.value == TorchState.ON
    fun toggleFlash(isOn: Boolean) {
        camera?.cameraControl?.enableTorch(isOn)
    }

    fun release() {
        cameraExecutor.shutdown()
    }
}
