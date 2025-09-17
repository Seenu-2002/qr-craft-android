package com.seenu.dev.android.qr_craft.presentation.misc

import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class QRCodeAnalyzer constructor(
    private val listener: ProcessListener
) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient()
    private var isProcessing = false
    private var isPaused = false

    fun pause() {
        isPaused = true
    }

    fun resume() {
        isPaused = false
    }

    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        if (isProcessing || isPaused) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            isProcessing = true
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        barcode.rawValue?.let { qrContent ->
                            listener.onSuccess(qrContent)
                            return@addOnSuccessListener
                        }
                    }
                    isProcessing = false
                }
                .addOnFailureListener {
                    isProcessing = false
                    listener.onFailure(it)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    fun analyze(image: Bitmap) {
        if (isProcessing || isPaused) {
            return
        }

        isProcessing = true

        scanner.process(image, 0)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isEmpty()) {
                    listener.onFailure(QRCodeNotFoundException())
                }

                for (barcode in barcodes) {
                    barcode.rawValue?.let { qrContent ->
                        listener.onSuccess(qrContent)
                        return@addOnSuccessListener
                    }
                }
                isProcessing = false
            }
            .addOnFailureListener {
                isProcessing = false
                listener.onFailure(it)
            }
    }

    interface ProcessListener {
        fun onSuccess(qrData: String)
        fun onFailure(exception: Exception)
    }

    class QRCodeNotFoundException : Exception("No QR code found")

}
