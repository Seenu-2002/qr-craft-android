package com.seenu.dev.android.qr_craft.presentation.scanner.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.seenu.dev.android.qr_craft.R
import com.seenu.dev.android.qr_craft.presentation.ui.theme.onOverlay
import com.seenu.dev.android.qr_craft.presentation.ui.theme.overlay

@Composable
fun ScannerOverlay(modifier: Modifier = Modifier, showCameraBounds: Boolean = true) {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    var squareOffset by remember {
        mutableStateOf(Offset.Zero)
    }

    val overlayColor = MaterialTheme.colorScheme.overlay
    val borderColor = MaterialTheme.colorScheme.primary
    val textMeasurer = rememberTextMeasurer()
    val helperMessage = stringResource(R.string.point_your_camera_at_qr)
    val helperMessageStyle = MaterialTheme.typography.titleSmall.copy(
        color = MaterialTheme.colorScheme.onOverlay
    )
    Box(
        modifier = modifier
            .onGloballyPositioned {
                size = it.size
            }
            .drawWithCache {

                if (!showCameraBounds) {
                    return@drawWithCache onDrawBehind {
                        this.drawRect(
                            color = overlayColor,
                            topLeft = Offset.Zero,
                            size = this.size
                        )
                    }
                }

                val squareSize = minOf(size.width, size.height) - 48.dp.toPx()
                squareOffset = Offset(
                    (size.width - squareSize) / 2F,
                    (size.height - squareSize) / 2F
                )

                val borderLengthOnSide = 48.dp.toPx()
                val borderWidth = 4.dp.toPx()
                val borderWidthHalf = borderWidth / 2F
                val startX = squareOffset.x - borderWidthHalf
                val startY = squareOffset.y - borderWidthHalf
                val endX = squareOffset.x + squareSize + borderWidthHalf
                val endY = squareOffset.y + squareSize + borderWidthHalf
                val cornerRadius = 16.dp.toPx()
                val borderRadius = 20.dp.toPx()
                val borderPath = Path().apply {

                    // Top Left
                    moveTo(startX, startY + borderLengthOnSide)
                    lineTo(startX, startY + borderRadius)
                    quadraticTo(
                        startX, startY,
                        startX + borderRadius, startY
                    )
                    lineTo(startX + borderLengthOnSide, startY)

                    // Top Right
                    moveTo(endX - borderLengthOnSide, startY)
                    lineTo(endX - borderRadius, startY)
                    quadraticTo(
                        endX, startY,
                        endX, startY + borderRadius
                    )
                    lineTo(endX, startY + borderLengthOnSide)

                    // Bottom Right
                    moveTo(endX, endY - borderLengthOnSide)
                    lineTo(endX, endY - borderRadius)
                    quadraticTo(
                        endX, endY,
                        endX - borderRadius, endY
                    )
                    lineTo(endX - borderLengthOnSide, endY)

                    // Bottom Left
                    moveTo(startX + borderLengthOnSide, endY)
                    lineTo(startX + borderRadius, endY)
                    quadraticTo(
                        startX, endY,
                        startX, endY - borderRadius
                    )
                    lineTo(startX, endY - borderLengthOnSide)
                }

                onDrawBehind {
                    this.drawRect(
                        color = overlayColor,
                        topLeft = Offset.Zero,
                        size = this.size
                    )

                    val textLayoutResult = textMeasurer.measure(
                        text = helperMessage,
                        style = helperMessageStyle
                    )
                    val textSize = textLayoutResult.size
                    this.drawText(
                        textLayoutResult = textLayoutResult,
                        topLeft = Offset(
                            x = (size.width - textSize.width) / 2F,
                            y = squareOffset.y - 48.dp.toPx()
                        )
                    )

                    this.drawRoundRect(
                        color = Color.Transparent,
                        cornerRadius = CornerRadius(cornerRadius),
                        topLeft = squareOffset,
                        size = Size(squareSize, squareSize),
                        blendMode = BlendMode.Clear
                    )

                    this.drawPath(
                        path = borderPath,
                        color = borderColor,
                        style = Stroke(width = borderWidth, cap = StrokeCap.Round)
                    )
                }
            }
    )
}