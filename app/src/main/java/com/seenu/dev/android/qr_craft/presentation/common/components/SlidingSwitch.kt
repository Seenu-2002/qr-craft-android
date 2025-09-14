package com.seenu.dev.android.qr_craft.presentation.common.components

import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.seenu.dev.android.qr_craft.presentation.ui.theme.QrCraftTheme

@Preview
@Composable
private fun SlidingSwitchPreview() {
    QrCraftTheme {
        var selected by remember { mutableStateOf("Scanned") }
        SlidingSwitch(
            modifier = Modifier.fillMaxWidth(),
            selected = selected,
            options = listOf("Scanned", "Generated")
        ) { index, it ->
            selected = it
        }
    }
}

@Composable
fun SlidingSwitch(
    modifier: Modifier = Modifier,
    selected: String,
    options: List<String>,
    animDuration: Int = 250,
    onOptionSelected: (Int, String) -> Unit = { _, _ -> }
) {
    if (selected !in options) {
        throw IllegalArgumentException("Selected option must be one of the options")
    }

    var selectedOptionOffset by remember {
        mutableStateOf(Offset.Zero)
    }
    val animatedOffset by animateOffsetAsState(
        targetValue = selectedOptionOffset,
        label = "SlidingSwitch Offset Animation",
        animationSpec = tween(durationMillis = animDuration)
    )

    var selectedOptionSize = remember {
        IntSize.Zero
    }

    val selectorColor = MaterialTheme.colorScheme.onSurface
    val lineColor = MaterialTheme.colorScheme.outline
    Row(modifier = modifier.drawWithContent {
        drawContent()

        val gap = 4.dp.toPx()
        val selectorHeight = 2.dp.toPx()
        val lineHeight = 1.dp.toPx()
        val roundedRect = RoundRect(
            rect = Rect(
                offset = Offset(animatedOffset.x + gap, size.height - selectorHeight),
                size = Size(selectedOptionSize.width.toFloat() - (2 * gap), selectorHeight)
            ),
            topLeft = CornerRadius(2.dp.toPx()),
            topRight = CornerRadius(2.dp.toPx()),
        )
        val path = Path().apply {
            addRoundRect(roundedRect)
        }

        drawLine(
            color = lineColor,
            start = Offset(0F, size.height - (lineHeight / 2F)),
            end = Offset(size.width, size.height - (lineHeight / 2F)),
            strokeWidth = lineHeight
        )

        drawPath(
            path = path, color = selectorColor, style = Fill
        )
    }) {
        for ((index, option) in options.withIndex()) {
            val isSelected = option == selected
            Box(
                modifier = Modifier
                    .weight(1F)
                    .clickable {
                        onOptionSelected(index, option)
                    }
                    .onGloballyPositioned {
                        if (isSelected) {
                            val position = it.positionInParent()
                            selectedOptionSize = it.size
                            selectedOptionOffset = position
                        }
                    }
            ) {
                Text(
                    text = option,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}