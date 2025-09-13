package com.seenu.dev.android.qr_craft.presentation.create

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.seenu.dev.android.qr_craft.R
import com.seenu.dev.android.qr_craft.presentation.state.QrType
import com.seenu.dev.android.qr_craft.presentation.state.QrTypeItem
import com.seenu.dev.android.qr_craft.presentation.state.items
import com.seenu.dev.android.qr_craft.presentation.ui.theme.QrCraftTheme
import com.seenu.dev.android.qr_craft.presentation.ui.theme.link
import com.seenu.dev.android.qr_craft.presentation.ui.theme.linkBg
import com.seenu.dev.android.qr_craft.presentation.ui.theme.surfaceHigher

@Preview
@Composable
private fun CreateQrListScreenPreview() {
    QrCraftTheme {
        ChooseQrTypeScreen()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseQrTypeScreen(openQrCreateScreen: (QrType) -> Unit = {}) {
    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.create_qr),
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }, containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        val qrTypeItems = items
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(all = 8.dp),
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(qrTypeItems.values.toList()) { item ->
                QrTypeItem(modifier = Modifier.clickable {
                    openQrCreateScreen(item.type)
                }, data = item)
            }
        }
    }
}

@Preview
@Composable
private fun QrTypeItemPreview() {
    QrCraftTheme {
        val item = QrTypeItem(
            title = "Link",
            type = QrType.LINK,
            tint = MaterialTheme.colorScheme.link,
            backgroundColor = MaterialTheme.colorScheme.linkBg,
            iconRes = R.drawable.ic_link
        )
        QrTypeItem(modifier = Modifier.width(300.dp), data = item)
    }
}

@Composable
fun QrTypeItem(modifier: Modifier = Modifier, data: QrTypeItem) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceHigher,
                shape = MaterialTheme.shapes.medium
            )
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier
                .background(
                    color = data.backgroundColor,
                    shape = CircleShape
                )
                .padding(12.dp),
            painter = painterResource(id = data.iconRes),
            contentDescription = data.title,
            tint = data.tint
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = data.title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}