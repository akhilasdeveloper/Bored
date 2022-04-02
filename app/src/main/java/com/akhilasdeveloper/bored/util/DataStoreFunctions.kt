package com.akhilasdeveloper.bored.util

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface DataStoreFunctions  {
    suspend fun <T> saveValueToPreferencesStore(key: Preferences.Key<T>, value: T)
    fun <T> getValueFromPreferencesStore(key: Preferences.Key<T>) : Flow<T?>
}