package com.seenu.dev.android.qr_craft.presentation.common.components

import android.R.attr.animationDuration
import android.R.attr.contentDescription
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.seenu.dev.android.qr_craft.R
import com.seenu.dev.android.qr_craft.presentation.ui.theme.QrCraftTheme
import com.seenu.dev.android.qr_craft.presentation.ui.theme.surfaceHigher

@Preview
@Composable
private fun ScreenSliderPreview() {
    val items = listOf(
        ScreenSliderItem(
            icon = androidx.compose.ui.res.painterResource(id = R.drawable.ic_clock_refresh),
            contentDescription = "Camera"
        ),
        ScreenSliderItem(
            icon = androidx.compose.ui.res.painterResource(id = R.drawable.ic_scan),
            contentDescription = "Compass"
        ),
        ScreenSliderItem(
            icon = androidx.compose.ui.res.painterResource(id = R.drawable.ic_plus_circle),
            contentDescription = "Directions"
        )
    )
    val selectedItem = items[2]

    QrCraftTheme {
        var selectedItem by remember { mutableStateOf(selectedItem) }
        ScreenSlider(items = items, selectedItem = selectedItem) { _, it -> selectedItem = it }
    }
}

@Composable
fun ScreenSlider(
    modifier: Modifier = Modifier,
    items: List<ScreenSliderItem>,
    selectedItem: ScreenSliderItem,
    onItemSelected: (Int, ScreenSliderItem) -> Unit = { _, _ -> }
) {
    val backgroundColor = MaterialTheme.colorScheme.surfaceHigher
    val selectedItemBackgroundColor = MaterialTheme.colorScheme.primary
    val selectedItemIndex = items.indexOf(selectedItem)
    if (selectedItemIndex == -1) {
        throw IllegalArgumentException("Selected item must be one of the items in the list")
    }

    var selectedItemOffset by remember { mutableStateOf(Offset.Zero) }
    val animatedSelectedItemOffset by animateOffsetAsState(
        targetValue = selectedItemOffset,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
    )

    val itemSize = 44.dp
    val horizontalPadding = 4.dp

    Row(
        modifier = modifier
            .padding(vertical = 4.dp)
            .drawWithContent(
                onDraw = {
                    val backgroundHeight = 52.dp.toPx()
                    val radius = backgroundHeight / 2F
                    val top = (size.height - backgroundHeight) / 2F
                    val itemSizeInPx = itemSize.toPx()
                    drawRoundRect(
                        color = backgroundColor,
                        cornerRadius = CornerRadius(x = radius),
                        topLeft = Offset(0F, top),
                        size = Size(size.width, backgroundHeight),
                    )

                    val selectedItemSize = 64.dp.toPx()
                    val horizontalPaddingInPx = horizontalPadding.toPx()

                    val itemCenter =
                        animatedSelectedItemOffset + Offset(itemSizeInPx / 2f, itemSizeInPx / 2f)
                    val backgroundOffset =
                        itemCenter - Offset(selectedItemSize / 2f, selectedItemSize / 2f) + Offset(
                            horizontalPaddingInPx,
                            0F
                        )

                    drawRoundRect(
                        color = selectedItemBackgroundColor,
                        topLeft = backgroundOffset,
                        cornerRadius = CornerRadius(selectedItemSize / 2f),
                        size = Size(selectedItemSize, selectedItemSize)
                    )

                    drawContent()

                }
            )
            .padding(horizontal = horizontalPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {

        for ((index, item) in items.withIndex()) {
            val isSelectedItem = item == selectedItem
            val (painter, contentDescription) = item

            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(itemSize)
                    .onGloballyPositioned {
                        if (isSelectedItem) {
                            selectedItemOffset = it.positionInParent()
                        }
                    }
                    .clickable(interactionSource = null, indication = null) {
                        onItemSelected(index, item)
                    }, contentAlignment = Alignment.Center
            ) {
                val scale by animateFloatAsState(
                    targetValue = if (isSelectedItem) 1.75F else 1F, // scale up when selected
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )

                Icon(
                    modifier = Modifier
                        .size(16.dp)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        },
                    painter = painter,
                    contentDescription = contentDescription
                )
            }
        }
    }
}

data class ScreenSliderItem constructor(
    val icon: Painter,
    val contentDescription: String
) {

    override fun equals(other: Any?): Boolean {
        return other is ScreenSliderItem && other.contentDescription == contentDescription
    }

    override fun hashCode(): Int {
        var result = icon.hashCode()
        result = 31 * result + contentDescription.hashCode()
        return result
    }
}