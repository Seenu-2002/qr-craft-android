package com.seenu.dev.android.qr_craft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.seenu.dev.android.qr_craft.presentation.route.Screen
import com.seenu.dev.android.qr_craft.presentation.scanner.ScannerScreen
import com.seenu.dev.android.qr_craft.presentation.ui.theme.QrCraftTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            QrCraftTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Scanner,
                        Modifier.padding(innerPadding)
                    ) {

                        composable<Screen.Scanner> {
                            ScannerScreen(onScanResult = {
                                navController.navigate(Screen.ScanDetails)
                            }, onCloseApp = {
                                navController.popBackStack()
                            })
                        }

                    }
                }
            }
        }
    }
}