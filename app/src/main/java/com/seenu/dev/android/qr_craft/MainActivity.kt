package com.seenu.dev.android.qr_craft

import android.R.attr.data
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.seenu.dev.android.qr_craft.domain.model.QrData
import com.seenu.dev.android.qr_craft.presentation.common.components.ScreenSlider
import com.seenu.dev.android.qr_craft.presentation.common.components.ScreenSliderItem
import com.seenu.dev.android.qr_craft.presentation.create.ChooseQrTypeScreen
import com.seenu.dev.android.qr_craft.presentation.create.CreateQrScreen
import com.seenu.dev.android.qr_craft.presentation.route.Screen
import com.seenu.dev.android.qr_craft.presentation.scan_details.QrScanDetailsScreen
import com.seenu.dev.android.qr_craft.presentation.scanner.QrScannerScreen
import com.seenu.dev.android.qr_craft.presentation.state.QrType
import com.seenu.dev.android.qr_craft.presentation.ui.theme.QrCraftTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.getViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            QrCraftTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    val currentScreen =
                        navController.currentBackStackEntryAsState().value?.destination?.route

                    val barVisibility = when (currentScreen) {
                        Screen.Scanner.route -> BarVisibility.Hidden
                        else -> BarVisibility.Visible
                    }
                    UpdateSystemBars(barVisibility)

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Scanner.route
                    ) {
                        composable(Screen.Scanner.route) {
                            QrScannerScreen(
                                openQrDetailsScreen = { data ->
                                    val qrDatAsString = Json.encodeToString(data)
                                    navController.navigate(
                                        "${Screen.ScanDetails.route}/$qrDatAsString/false"
                                    )
                                }, onCloseApp = {
                                    navController.popBackStack()
                                })
                        }

                        composable("${Screen.ScanDetails.route}/{data}/{isPreview}") { entry ->
                            val isPreview =
                                entry.arguments?.getString("isPreview")?.toBoolean() ?: false
                            val data = entry.arguments?.getString("data")?.let {
                                Json.decodeFromString<QrData>(it)
                            } ?: throw IllegalStateException("No data found")
                            ScreenContainer(showDarkIcons = false) {
                                QrScanDetailsScreen(
                                    isPreview = isPreview,
                                    qrData = data,
                                    onCopyData = {
                                        copyDataToClipboard(it)
                                    },
                                    onShareData = {
                                        shareData(it)
                                    },
                                    onBackPressed = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }

                        composable(Screen.ChooseQrType.route) {
                            ScreenContainer(showDarkIcons = true) {
                                ChooseQrTypeScreen(openQrCreateScreen = { type ->
                                    navController.navigate("${Screen.CreateQr.route}/${type.name}")
                                })
                            }
                        }

                        composable("${Screen.CreateQr.route}/{type}") { entry ->
                            val type =
                                QrType.valueOf(entry.arguments?.getString("type")!!.uppercase())
                            ScreenContainer(showDarkIcons = true) {
                                CreateQrScreen(type = type, onGenerateQr = { data ->
                                    val qrDataAsString = Json.encodeToString(data)
                                    navController.navigate("${Screen.ScanDetails.route}/${qrDataAsString}/true") {
                                        popUpTo("${Screen.CreateQr.route}/$type") {
                                            inclusive = true
                                        }
                                        launchSingleTop = true
                                    }
                                }, onNavigateBack = {
                                    navController.popBackStack()
                                })
                            }
                        }
                    }

                    val items = listOf(
                        ScreenSliderItem(
                            icon = painterResource(id = R.drawable.ic_clock_refresh),
                            contentDescription = stringResource(R.string.access_scan_history)
                        ),
                        ScreenSliderItem(
                            icon = painterResource(id = R.drawable.ic_scan),
                            contentDescription = stringResource(R.string.access_qr_scanner)
                        ),
                        ScreenSliderItem(
                            icon = painterResource(id = R.drawable.ic_plus_circle),
                            contentDescription = stringResource(R.string.access_qr_create)
                        )
                    )

                    val showScreenSlider = when (currentScreen) {
                        Screen.Scanner.route, Screen.ChooseQrType.route -> true
                        else -> false
                    }
                    var selectedItem by remember { mutableStateOf(items[1]) }
                    AnimatedVisibility(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 52.dp),
                        visible = showScreenSlider,
                        exit = slideOutVertically(
                            targetOffsetY = { fullHeight -> fullHeight },
                            animationSpec = tween(300)
                        ), enter = slideInVertically(
                            initialOffsetY = { fullHeight -> fullHeight },
                            animationSpec = tween(durationMillis = 300)
                        )
                    ) {
                        ScreenSlider(
                            items = items,
                            selectedItem = selectedItem
                        ) { index, item ->
                            if (index > 0) {
                                selectedItem = item
                            }

                            val route = when (index) {
                                1 -> Screen.Scanner.route
                                2 -> Screen.ChooseQrType.route
                                else -> return@ScreenSlider
                            }
                            navController.navigate(route) {
                                popUpTo(0) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    }
                }
            }
        }
    }

    private fun copyDataToClipboard(qrData: QrData) {
        val data = when (qrData) {
            is QrData.Url -> {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    qrData.url.toUri()
                )
                ClipData.newIntent("open_url", intent)
            }

            else -> {
                ClipData.newPlainText(
                    "qr_data", qrData.rawValue
                )
            }
        }
        val clipBoardManager =
            ContextCompat.getSystemService(this, ClipboardManager::class.java)
        clipBoardManager?.setPrimaryClip(data)
    }

    private fun shareData(data: QrData) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, data.rawValue)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(intent, null)
        startActivity(shareIntent)
    }
}

@Composable
fun ScreenContainer(
    showDarkIcons: Boolean = true,
    content: @Composable () -> Unit
) {
    SetStatusBarIconColor(showDarkIcons)
    content()
}

@Composable
fun SetStatusBarIconColor(showDarkIcons: Boolean) {
    val view = LocalView.current
    val activity = LocalActivity.current
    val windowInsetsController = activity?.window?.let {
        WindowCompat.getInsetsController(it, view)
    }

    LaunchedEffect(showDarkIcons) {
        windowInsetsController?.isAppearanceLightStatusBars = showDarkIcons
    }
}

sealed class BarVisibility {
    object Hidden : BarVisibility()
    object Visible : BarVisibility()
}

@Composable
fun UpdateSystemBars(visibility: BarVisibility) {
    val activity = LocalActivity.current
    val insetsController = remember(activity) {
        activity?.window?.let { WindowCompat.getInsetsController(it, it.decorView) }
    }

    LaunchedEffect(visibility) {
        insetsController?.let {
            when (visibility) {
                BarVisibility.Hidden -> {
                    it.hide(WindowInsetsCompat.Type.statusBars())
                    it.hide(WindowInsetsCompat.Type.navigationBars())
                    it.systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }

                BarVisibility.Visible -> {
                    it.show(WindowInsetsCompat.Type.statusBars())
                    it.show(WindowInsetsCompat.Type.navigationBars())
                    it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
                }
            }
        }
    }
}
