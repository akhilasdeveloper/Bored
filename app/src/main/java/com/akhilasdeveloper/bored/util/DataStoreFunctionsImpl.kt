package com.akhilasdeveloper.bored.util

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.io.IOException

class DataStoreFunctionsImpl(private val context: Context) : DataStoreFunctions {
    override suspend fun <T> saveValueToPreferencesStore(key: Preferences.Key<T>, value: T) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    override fun <T> getValueAsFlowFromPreferencesStore(key: Preferences.Key<T>): Flow<T?> =
        context.userPreferencesDataStore.data.map { preference ->
            preference[key]
        }

    override suspend fun <T> getValueFromPreferencesStore(key: Preferences.Key<T>): T? =
        context.userPreferencesDataStore.data.first()[key]

}