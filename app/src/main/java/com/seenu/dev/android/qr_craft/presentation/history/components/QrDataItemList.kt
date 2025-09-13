package com.seenu.dev.android.qr_craft.presentation.history.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradient
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.seenu.dev.android.qr_craft.R
import com.seenu.dev.android.qr_craft.presentation.scan_details.components.QrUiModelProvider
import com.seenu.dev.android.qr_craft.presentation.state.QrDataUiModel
import com.seenu.dev.android.qr_craft.presentation.ui.theme.QrCraftTheme

@Preview
@Composable
private fun QrDataItemListPreview() {
    val list = remember {
        QrUiModelProvider().values.toList()
    }
    val data = (1..100).map {
        list[it % list.size]
    }

    QrCraftTheme {
        Surface {
            QrDataItemList(
                modifier = Modifier,
                qrItems = data,
                onItemLongPress = {},
                onItemClick = {}
            )
        }
    }
}

@Composable
fun QrDataItemList(
    modifier: Modifier = Modifier,
    qrItems: List<QrDataUiModel>,
    onItemLongPress: (QrDataUiModel) -> Unit,
    onItemClick: (QrDataUiModel) -> Unit
) {

    Box(modifier = modifier, contentAlignment = Alignment.Center) {

        if (qrItems.isEmpty()) {
            Text(
                text = stringResource(id = R.string.no_history),
                modifier = Modifier,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
            return
        }

        val lazyListState = rememberLazyListState()

        val size = qrItems.size
        val lastVisibleItemIndex =
            lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 4.dp)
        ) {
            items(qrItems) { item ->
                QrDataItem(
                    modifier = Modifier
                        .animateItem(
                            fadeOutSpec = spring(
                                stiffness = Spring.StiffnessLow,
                                dampingRatio = Spring.DampingRatioLowBouncy,
                            ),
                            placementSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow,
                                visibilityThreshold = IntOffset.VisibilityThreshold,
                            )
                        )
                        .padding(vertical = 4.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                onItemClick(item)
                            }, onLongPress = {
                                onItemLongPress(item)
                            })
                        },
                    qrData = item,
                )
            }
        }
        AnimatedVisibility(
            visible = lastVisibleItemIndex != size - 1,
            modifier = Modifier
                .align(Alignment.BottomEnd),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(.3F)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
            )
        }
    }
}