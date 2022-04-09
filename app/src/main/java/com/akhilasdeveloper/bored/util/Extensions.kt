package com.akhilasdeveloper.bored.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.akhilasdeveloper.bored.util.Constants.BORED_DATABASE_NAME

internal val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = BORED_DATABASE_NAME
)

fun Float.roundToTwoDecimals() = "%.2f".format(this).toFloat()

