package com.seenu.dev.android.qr_craft.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.seenu.dev.android.qr_craft.R

val Suse = FontFamily(
    Font(R.font.suse_regular, FontWeight.Normal),
    Font(R.font.suse_medium, FontWeight.Medium),
    Font(R.font.suse_semibold, FontWeight.SemiBold),
    Font(R.font.suse_bold, FontWeight.Bold),
)

val Typography = Typography(
    titleMedium = TextStyle(
        fontFamily = Suse,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 32.sp
    ),
    titleSmall = TextStyle(
        fontFamily = Suse,
        fontWeight = FontWeight.SemiBold,
        fontSize = 19.sp,
        lineHeight = 24.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Suse,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontFamily = Suse,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 16.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Suse,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 20.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Suse,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 16.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Suse,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 14.sp
    )
)