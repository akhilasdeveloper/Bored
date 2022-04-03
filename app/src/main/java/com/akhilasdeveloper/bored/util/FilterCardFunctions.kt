package com.akhilasdeveloper.bored.util

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.akhilasdeveloper.bored.data.CategoryData
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FilterCardFunctions
@Inject constructor(private val dataStore: DataStoreFunctionsImpl) {

    private val FILTER_RANDOM_IS_CHECKED = booleanPreferencesKey("FILTER_RANDOM_IS_CHECKED")
    private val FILTER_TYPE_IS_CHECKED = booleanPreferencesKey("FILTER_TYPE_IS_CHECKED")
    private val FILTER_TYPE_VALUE = stringPreferencesKey("FILTER_TYPE_VALUE")
    private val FILTER_PARTICIPANTS_IS_CHECKED = booleanPreferencesKey("FILTER_PARTICIPANTS_IS_CHECKED")
    private val FILTER_PARTICIPANTS_VALUE = intPreferencesKey("FILTER_PARTICIPANTS_VALUE")
    private val FILTER_PRICE_RANGE_IS_CHECKED = booleanPreferencesKey("FILTER_PRICE_RANGE_IS_CHECKED")
    private val FILTER_PRICE_RANGE_START_VALUE = floatPreferencesKey("FILTER_PRICE_RANGE_START_VALUE")
    private val FILTER_PRICE_RANGE_END_VALUE = floatPreferencesKey("FILTER_PRICE_RANGE_END_VALUE")
    private val FILTER_ACCESSIBILITY_RANGE_IS_CHECKED = booleanPreferencesKey("FILTER_ACCESSIBILITY_RANGE_IS_CHECKED")
    private val FILTER_ACCESSIBILITY_RANGE_START_VALUE = floatPreferencesKey("FILTER_ACCESSIBILITY_RANGE_START_VALUE")
    private val FILTER_ACCESSIBILITY_RANGE_END_VALUE = floatPreferencesKey("FILTER_ACCESSIBILITY_RANGE_END_VALUE")

    //Filter Field Random

    suspend fun setRandomIsChecked(value: Boolean) {
        dataStore.saveValueToPreferencesStore(FILTER_RANDOM_IS_CHECKED, value)
    }

    fun getRandomIsChecked() = dataStore.getValueAsFlowFromPreferencesStore(FILTER_RANDOM_IS_CHECKED).map {
            it ?: true
        }

    suspend fun getRandomIsCheckedRaw() = dataStore.getValueFromPreferencesStore(FILTER_RANDOM_IS_CHECKED)?: true


    //Filter Field Type

    suspend fun setTypeIsChecked(value: Boolean) {
        dataStore.saveValueToPreferencesStore(FILTER_TYPE_IS_CHECKED, value)
    }

    fun getTypeIsChecked() =
        dataStore.getValueAsFlowFromPreferencesStore(FILTER_TYPE_IS_CHECKED).map { it ?: false }

    suspend fun getTypeIsCheckedRaw() =
        dataStore.getValueFromPreferencesStore(FILTER_TYPE_IS_CHECKED)?: false

    suspend fun setTypeValue(value: String) {
        dataStore.saveValueToPreferencesStore(FILTER_TYPE_VALUE, value)
    }

    fun getTypeValue() =
        dataStore.getValueAsFlowFromPreferencesStore(FILTER_TYPE_VALUE).map {
            it ?: CategoryData.Invalid.key
        }

    suspend fun getTypeRawValue() =
        dataStore.getValueFromPreferencesStore(FILTER_TYPE_VALUE)?: CategoryData.Invalid.key


    //Filter Field Participants

    suspend fun setParticipantsIsChecked(value: Boolean) {
        dataStore.saveValueToPreferencesStore(FILTER_PARTICIPANTS_IS_CHECKED, value)
    }

    fun getParticipantsIsChecked() =
        dataStore.getValueAsFlowFromPreferencesStore(FILTER_PARTICIPANTS_IS_CHECKED).map { it ?: false }

    suspend fun getParticipantsIsCheckedRaw() =
        dataStore.getValueFromPreferencesStore(FILTER_PARTICIPANTS_IS_CHECKED) ?: false

    suspend fun setParticipantsValue(value: Int) {
        dataStore.saveValueToPreferencesStore(FILTER_PARTICIPANTS_VALUE, value)
    }

    fun getParticipantsValue() =
        dataStore.getValueAsFlowFromPreferencesStore(FILTER_PARTICIPANTS_VALUE).map { it ?: 1 }

    suspend fun getParticipantsRawValue() =
        dataStore.getValueFromPreferencesStore(FILTER_PARTICIPANTS_VALUE) ?: 1


    //Filter Field PriceRange

    suspend fun setPriceRangeIsChecked(value: Boolean) {
        dataStore.saveValueToPreferencesStore(FILTER_PRICE_RANGE_IS_CHECKED, value)
    }

    fun getPriceRangeIsChecked() =
        dataStore.getValueAsFlowFromPreferencesStore(FILTER_PRICE_RANGE_IS_CHECKED).map { it ?: false }

    suspend fun getPriceRangeIsCheckedRaw() =
        dataStore.getValueFromPreferencesStore(FILTER_PRICE_RANGE_IS_CHECKED)?: false

    suspend fun setPriceRangeStartValue(value: Float) {
        dataStore.saveValueToPreferencesStore(FILTER_PRICE_RANGE_START_VALUE, value)
    }

    fun getPriceRangeStartValue() =
        dataStore.getValueAsFlowFromPreferencesStore(FILTER_PRICE_RANGE_START_VALUE).map { it ?: 0.0f }

    suspend fun getPriceRangeStartRawValue() =
        dataStore.getValueFromPreferencesStore(FILTER_PRICE_RANGE_START_VALUE) ?: 0.0f

    suspend fun setPriceRangeEndValue(value: Float) {
        dataStore.saveValueToPreferencesStore(FILTER_PRICE_RANGE_END_VALUE, value)
    }

    fun getPriceRangeEndValue() =
        dataStore.getValueAsFlowFromPreferencesStore(FILTER_PRICE_RANGE_END_VALUE).map { it ?: 1.0f }

    suspend fun getPriceRangeEndRawValue() =
        dataStore.getValueFromPreferencesStore(FILTER_PRICE_RANGE_END_VALUE) ?: 1.0f


    //Filter Field AccessibilityRange

    suspend fun setAccessibilityRangeIsChecked(value: Boolean) {
        dataStore.saveValueToPreferencesStore(FILTER_ACCESSIBILITY_RANGE_IS_CHECKED, value)
    }

    fun getAccessibilityRangeIsChecked() =
        dataStore.getValueAsFlowFromPreferencesStore(FILTER_ACCESSIBILITY_RANGE_IS_CHECKED)
            .map { it ?: false }

    suspend fun getAccessibilityRangeIsCheckedRaw() =
        dataStore.getValueFromPreferencesStore(FILTER_ACCESSIBILITY_RANGE_IS_CHECKED) ?: false

    suspend fun setAccessibilityRangeStartValue(value: Float) {
        dataStore.saveValueToPreferencesStore(FILTER_ACCESSIBILITY_RANGE_START_VALUE, value)
    }

    fun getAccessibilityRangeStartValue() =
        dataStore.getValueAsFlowFromPreferencesStore(FILTER_ACCESSIBILITY_RANGE_START_VALUE).map { it ?: 0.0f }

    suspend fun getAccessibilityRangeStartRawValue() =
        dataStore.getValueFromPreferencesStore(FILTER_ACCESSIBILITY_RANGE_START_VALUE) ?: 0.0f

    suspend fun setAccessibilityRangeEndValue(value: Float) {
        dataStore.saveValueToPreferencesStore(FILTER_ACCESSIBILITY_RANGE_END_VALUE, value)
    }

    fun getAccessibilityRangeEndValue() =
        dataStore.getValueAsFlowFromPreferencesStore(FILTER_ACCESSIBILITY_RANGE_END_VALUE).map { it ?: 1.0f }

    suspend fun getAccessibilityRangeEndRawValue() =
        dataStore.getValueFromPreferencesStore(FILTER_ACCESSIBILITY_RANGE_END_VALUE) ?: 1.0f

}


