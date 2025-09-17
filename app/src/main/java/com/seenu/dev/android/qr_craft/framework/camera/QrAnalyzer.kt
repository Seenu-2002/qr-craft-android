package com.seenu.dev.android.qr_craft.framework.camera

import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class QRCodeAnalyzer constructor(
    private val listener: ProcessListener,
    private val scope: CoroutineScope
) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient()
    private var isPaused = false

    private val mutex = Mutex()

    fun pause() {
        isPaused = true
    }

    fun resume() {
        isPaused = false
    }

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        scope.launch {
            if (isPaused) {
                imageProxy.close()
                return@launch
            }

            mutex.withLock {
                try {
                    val result = processImageProxy(imageProxy)
                    if (result != null) {
                        listener.onSuccess(result)
                    }
                } catch (e: Exception) {
                    listener.onFailure(e)
                }
            }
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private suspend fun processImageProxy(imageProxy: ImageProxy): String? {
        return suspendCancellableCoroutine { continuation ->
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image =
                    InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

                scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        if (barcodes.isEmpty()) {
                            continuation.resumeWith(Result.success(null))
                            return@addOnSuccessListener
                        }

                        barcodes.firstOrNull()?.rawValue?.let { qrContent ->
                            continuation.resumeWith(Result.success(qrContent))
                            return@addOnSuccessListener
                        }
                    }
                    .addOnFailureListener { it ->
                        continuation.resumeWith(Result.failure(it))
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            } else {
                imageProxy.close()
                continuation.resumeWith(Result.failure(Exception("ImageProxy has no image")))
            }
        }
    }

    fun analyze(image: Bitmap) {
        scope.launch {
            if (isPaused) {
                return@launch
            }

            mutex.withLock {
                try {
                    processBitmap(image)
                        .let { qrContent ->
                            listener.onSuccess(qrContent)
                        }
                } catch (e: Exception) {
                    listener.onFailure(e)
                }
            }
        }
    }

    private suspend fun processBitmap(bitmap: Bitmap): String {
        return suspendCancellableCoroutine { continuation ->
            scanner.process(bitmap, 0)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isEmpty()) {
                        continuation.resumeWith(Result.failure(QRCodeNotFoundException()))
                        return@addOnSuccessListener
                    }

                    barcodes.firstOrNull()?.rawValue?.let { qrContent ->
                        continuation.resumeWith(Result.success(qrContent))
                        return@addOnSuccessListener
                    }
                }
                .addOnFailureListener { it ->
                    continuation.resumeWith(Result.failure(it))
                }
        }
    }


    interface ProcessListener {
        fun onSuccess(qrData: String)
        fun onFailure(exception: Exception)
    }

    class QRCodeNotFoundException : Exception("No QR code found")

}
