package com.seenu.dev.android.qr_craft.presentation.util

import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.compositionLocalOf

enum class DeviceConfiguration {

    MOBILE_PORTRAIT,
    MOBILE_LANDSCAPE,
    TABLET_PORTRAIT,
    TABLET_LANDSCAPE,
    DESKTOP;

    companion object {
        fun fromWindowSizeClass(windowSizeClass: WindowSizeClass): DeviceConfiguration {
            val widthSize = windowSizeClass.widthSizeClass
            val heightSize = windowSizeClass.heightSizeClass
            return when (widthSize) {
                WindowWidthSizeClass.Compact -> when (heightSize) {
                    WindowHeightSizeClass.Compact -> MOBILE_PORTRAIT
                    WindowHeightSizeClass.Medium -> MOBILE_PORTRAIT
                    WindowHeightSizeClass.Expanded -> MOBILE_PORTRAIT
                    else -> MOBILE_PORTRAIT
                }

                WindowWidthSizeClass.Medium -> when (heightSize) {
                    WindowHeightSizeClass.Compact -> MOBILE_LANDSCAPE
                    WindowHeightSizeClass.Medium -> TABLET_PORTRAIT
                    WindowHeightSizeClass.Expanded -> TABLET_PORTRAIT
                    else -> TABLET_PORTRAIT
                }

                WindowWidthSizeClass.Expanded -> when (heightSize) {
                    WindowHeightSizeClass.Compact -> MOBILE_LANDSCAPE
                    WindowHeightSizeClass.Medium -> TABLET_LANDSCAPE
                    WindowHeightSizeClass.Expanded -> DESKTOP
                    else -> DESKTOP
                }

                else -> DESKTOP
            }
        }
    }

}

val LocalDeviceConfiguration = compositionLocalOf {
    DeviceConfiguration.MOBILE_PORTRAIT
}