package com.seenu.dev.android.qr_craft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.seenu.dev.android.qr_craft.presentation.common.QrDetailsViewModel
import com.seenu.dev.android.qr_craft.presentation.route.Screen
import com.seenu.dev.android.qr_craft.presentation.scan_details.QrScanDetailsScreen
import com.seenu.dev.android.qr_craft.presentation.scanner.QrScannerScreen
import com.seenu.dev.android.qr_craft.presentation.ui.theme.QrCraftTheme
import org.koin.androidx.viewmodel.ext.android.getViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            QrCraftTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Screen.Scanner
                ) {
                    val qrDetailsViewModel = getViewModel<QrDetailsViewModel>()
                    composable<Screen.Scanner> {
                        QrScannerScreen(
                            qrDetailsViewModel = qrDetailsViewModel,
                            openQrDetailsScreen = {
                                navController.navigate(
                                    Screen.ScanDetails
                                )
                            }, onCloseApp = {
                                navController.popBackStack()
                            })
                    }

                    composable<Screen.ScanDetails> {
                        ScreenContainer(showDarkIcons = false) {
                            QrScanDetailsScreen(
                                qrDetailsViewModel = qrDetailsViewModel,
                                onBackPressed = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }

                }
            }
        }
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