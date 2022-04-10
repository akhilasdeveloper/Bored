package com.akhilasdeveloper.bored.util

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.akhilasdeveloper.bored.data.CategoryValueData
import com.akhilasdeveloper.bored.data.mapper.ThemeValueMapper
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DemoFunctions
@Inject constructor(private val dataStore: DataStoreFunctionsImpl) {


    private val IS_CARD_SWIPE_TRIED = booleanPreferencesKey("IS_CARD_SWIPE_TRIED")
    private val IS_CARD_TAP_TRIED = booleanPreferencesKey("IS_CARD_TAP_TRIED")
    private val IS_ACTIVITY_CARD_SWIPE_TRIED = booleanPreferencesKey("IS_ACTIVITY_CARD_SWIPE_TRIED")
    private val IS_TERMS_DEMO_SHOWN = booleanPreferencesKey("IS_TERMS_DEMO_SHOWN")

    suspend fun setCardSwipeTriedValue(value: Boolean) {
        dataStore.saveValueToPreferencesStore(IS_CARD_SWIPE_TRIED, value)
    }
    suspend fun getCardSwipeTriedValue() =
        dataStore.getValueFromPreferencesStore(IS_CARD_SWIPE_TRIED)?: false


    suspend fun setCardTapTriedValue(value: Boolean) {
        dataStore.saveValueToPreferencesStore(IS_CARD_TAP_TRIED, value)
    }
    suspend fun getCardTapTriedValue() =
        dataStore.getValueFromPreferencesStore(IS_CARD_TAP_TRIED)?: false


    suspend fun setActivityCardSwipeTriedValue(value: Boolean) {
        dataStore.saveValueToPreferencesStore(IS_ACTIVITY_CARD_SWIPE_TRIED, value)
    }
    suspend fun getActivityCardSwipeTriedValue() =
        dataStore.getValueFromPreferencesStore(IS_ACTIVITY_CARD_SWIPE_TRIED)?: false


    suspend fun setTermsDemoShownValue(value: Boolean) {
        dataStore.saveValueToPreferencesStore(IS_TERMS_DEMO_SHOWN, value)
    }
    suspend fun getTermsDemoShownValue() =
        dataStore.getValueFromPreferencesStore(IS_TERMS_DEMO_SHOWN)?: false
}


