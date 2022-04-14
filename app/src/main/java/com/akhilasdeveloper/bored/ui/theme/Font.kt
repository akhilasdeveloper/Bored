package com.akhilasdeveloper.bored.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.akhilasdeveloper.bored.R

val NotoSansRegularFontFamily = FontFamily(

    fonts = listOf(
        Font(
            resId = R.font.noto_sans_regular,
            style = FontStyle.Normal
        ),
        Font(
            resId = R.font.noto_sans_bold,
            style = FontStyle.Normal,
            weight = FontWeight.W700
        )
    )
)

val NunitoFontFamily = FontFamily(

    fonts = listOf(
        Font(
            resId = R.font.nunito_bold,
            weight = FontWeight.W100,
            style = FontStyle.Normal
        )/*,
        Font(
            resId = R.font.baloo_bhaijaan2_bold,
            weight = FontWeight.W700,
            style = FontStyle.Normal
        ),
        Font(
            resId = R.font.baloo_bhaijaan2_medium,
            weight = FontWeight.W300,
            style = FontStyle.Normal
        ),
        Font(
            resId = R.font.baloo_bhaijaan2_semi_bold,
            weight = FontWeight.W500,
            style = FontStyle.Normal
        ),
        Font(
            resId = R.font.baloo_bhaijaan2_x_bold,
            weight = FontWeight.W900,
            style = FontStyle.Normal
        )*/
    )
)