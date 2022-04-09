package com.akhilasdeveloper.bored.util

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.akhilasdeveloper.bored.data.CategoryValueData
import com.akhilasdeveloper.bored.data.mapper.ThemeValueMapper
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ThemeFunctions
@Inject constructor(private val dataStore: DataStoreFunctionsImpl) {


    private val CURRENT_THEME_VALUE = intPreferencesKey("CURRENT_THEME_VALUE")

    suspend fun setCurrentThemeValue(value: Int) {
        dataStore.saveValueToPreferencesStore(CURRENT_THEME_VALUE, value)
    }

    fun getCurrentThemeValue() = dataStore.getValueAsFlowFromPreferencesStore(CURRENT_THEME_VALUE).map {
            it ?: ThemeValueMapper.SYSTEM_THEME
        }


}


