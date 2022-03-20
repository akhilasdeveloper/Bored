package com.akhilasdeveloper.bored.data

import androidx.compose.ui.graphics.Color

data class CardDao(
    val activityName: String?,
    val accessibility: Float?,
    val type: String?,
    val participants: Int?,
    val price: Float?,
    val link: String?,
    val cardColor: CardColor
)