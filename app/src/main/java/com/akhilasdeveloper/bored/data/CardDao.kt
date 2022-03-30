package com.akhilasdeveloper.bored.data

data class CardDao(
    val key: String,
    val activityName: String?,
    val accessibility: Float?,
    val type: String,
    val participants: Int?,
    val price: Float?,
    val link: String?
)