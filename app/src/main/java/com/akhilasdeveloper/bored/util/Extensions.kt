package com.akhilasdeveloper.bored.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.akhilasdeveloper.bored.util.Constants.BORED_DATABASE_NAME
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.roundToInt

internal val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = BORED_DATABASE_NAME
)

fun Float.roundToTwoDecimals() = "%.2f".format(this).toFloat()
fun Float.filterMultiplesOf(value:Float) = if((this * 100).roundToInt() % (value * 100).roundToInt() == 0) value else null
