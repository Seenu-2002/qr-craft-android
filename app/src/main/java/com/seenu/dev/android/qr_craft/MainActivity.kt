package com.seenu.dev.android.qr_craft

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Bitmap
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
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.seenu.dev.android.qr_craft.framework.image.ImageSaverFactory
import com.seenu.dev.android.qr_craft.presentation.state.QrDataUiModel
import com.seenu.dev.android.qr_craft.presentation.common.components.ScreenSlider
import com.seenu.dev.android.qr_craft.presentation.common.components.ScreenSliderItem
import com.seenu.dev.android.qr_craft.presentation.create.ChooseQrTypeScreen
import com.seenu.dev.android.qr_craft.presentation.create.CreateQrScreen
import com.seenu.dev.android.qr_craft.presentation.design_system.LocalDimen
import com.seenu.dev.android.qr_craft.presentation.design_system.dimen600dp
import com.seenu.dev.android.qr_craft.presentation.design_system.dimenMobilePortrait
import com.seenu.dev.android.qr_craft.presentation.history.QrHistoryScreen
import com.seenu.dev.android.qr_craft.presentation.route.Screen
import com.seenu.dev.android.qr_craft.presentation.scan_details.QrDetailsScreen
import com.seenu.dev.android.qr_craft.presentation.scanner.QrScannerScreen
import com.seenu.dev.android.qr_craft.presentation.state.QrType
import com.seenu.dev.android.qr_craft.presentation.ui.theme.QrCraftTheme
import com.seenu.dev.android.qr_craft.presentation.util.DeviceConfiguration
import com.seenu.dev.android.qr_craft.presentation.util.LocalDeviceConfiguration

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            QrCraftTheme {
                val activity = LocalActivity.current
                val deviceConfiguration = if (activity != null) {
                    val windowSize = calculateWindowSizeClass(activity)
                    DeviceConfiguration.fromWindowSizeClass(windowSize)
                } else {
                    DeviceConfiguration.MOBILE_PORTRAIT
                }

                CompositionLocalProvider(LocalDeviceConfiguration provides deviceConfiguration) {

                    val dimen =
                        if (LocalDeviceConfiguration.current == DeviceConfiguration.MOBILE_PORTRAIT) {
                            dimenMobilePortrait
                        } else {
                            dimen600dp
                        }
                    CompositionLocalProvider(LocalDimen provides dimen) {

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
                                        openQrDetailsScreen = { id ->
                                            navController.navigate("${Screen.ScanDetails.route}/$id/false")
                                        }, onCloseApp = {
                                            navController.popBackStack()
                                        })
                                }

                                composable(
                                    "${Screen.ScanDetails.route}/{id}/{isPreview}",
                                    arguments = listOf(
                                        navArgument("id") {
                                            type = NavType.LongType
                                        },
                                        navArgument("isPreview") {
                                            type = NavType.BoolType
                                        }
                                    )) { entry ->
                                    val isPreview =
                                        entry.arguments?.getBoolean("isPreview") ?: false
                                    val id = entry.arguments?.getLong("id")!!
                                    ScreenContainer(showDarkIcons = false) {
                                        QrDetailsScreen(
                                            isPreview = isPreview,
                                            id = id,
                                            onCopyData = {
                                                copyDataToClipboard(it)
                                            },
                                            onShareData = {
                                                shareData(it)
                                            },
                                            onBackPressed = {
                                                navController.popBackStack()
                                            },
                                            onSave = { title, bitmap ->
                                                saveImage(title, bitmap)
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
                                        QrType.valueOf(
                                            entry.arguments?.getString("type")!!.uppercase()
                                        )
                                    ScreenContainer(showDarkIcons = true) {
                                        CreateQrScreen(type = type, onQrGenerated = { id ->
                                            navController.navigate("${Screen.ScanDetails.route}/${id}/true") {
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

                                composable(Screen.QrHistory.route) {
                                    ScreenContainer(showDarkIcons = true) {
                                        QrHistoryScreen(
                                            openQrDetail = { id ->
                                                navController.navigate("${Screen.ScanDetails.route}/$id/true")
                                            },
                                            onShareQr = { data ->
                                                shareData(data)
                                            }
                                        )
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
                                Screen.Scanner.route, Screen.ChooseQrType.route, Screen.QrHistory.route -> true
                                else -> false
                            }
                            var selectedItemIndex by rememberSaveable { mutableIntStateOf(1) }
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
                                val selectedItem = items[selectedItemIndex]
                                ScreenSlider(
                                    items = items,
                                    selectedItem = selectedItem
                                ) { index, item ->
                                    selectedItemIndex = index

                                    val route = when (index) {
                                        0 -> Screen.QrHistory.route
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
        }
    }

    private fun copyDataToClipboard(qrData: QrDataUiModel) {
        val data = when (qrData.data) {
            is QrDataUiModel.Data.Url -> {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    qrData.data.url.toUri()
                )
                ClipData.newIntent("open_url", intent)
            }

            else -> {
                ClipData.newPlainText(
                    "qr_data", qrData.data.rawValue
                )
            }
        }
        val clipBoardManager =
            ContextCompat.getSystemService(this, ClipboardManager::class.java)
        clipBoardManager?.setPrimaryClip(data)
    }

    private fun shareData(data: QrDataUiModel) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, data.data.rawValue)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(intent, null)
        startActivity(shareIntent)
    }

    private fun saveImage(title: String, bitmap: Bitmap) {
        val fileName = "$title-${System.currentTimeMillis()}"
        ImageSaverFactory.getSaver().save(this.applicationContext, bitmap, fileName)
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
