package com.akhilasdeveloper.bored.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val defaultTypography = Typography()

val Typography = Typography(
    body1 = TextStyle(
        fontFamily = BalooBhaijaan2FontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    h6 = defaultTypography.h6.copy(fontFamily = BalooBhaijaan2FontFamily)
)