package com.akhilasdeveloper.bored.ui.screens.homescreen

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.akhilasdeveloper.bored.api.response.ApiResponse
import com.akhilasdeveloper.bored.api.response.BoredApiResponse
import com.akhilasdeveloper.bored.data.CardDao
import com.akhilasdeveloper.bored.data.CategoryColorItem
import com.akhilasdeveloper.bored.data.CategoryData
import com.akhilasdeveloper.bored.data.mapper.BoredResponseMapper
import com.akhilasdeveloper.bored.data.mapper.BoredTableMapper
import com.akhilasdeveloper.bored.data.mapper.CategoryMapper
import com.akhilasdeveloper.bored.repositories.BoredApiRepository
import com.akhilasdeveloper.bored.ui.theme.*
import com.akhilasdeveloper.bored.util.Constants.ADD_SELECTION
import com.akhilasdeveloper.bored.util.Constants.IDLE_SELECTION
import com.akhilasdeveloper.bored.util.Constants.PASS_SELECTION
import com.akhilasdeveloper.bored.util.FilterCardFunctions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.reflect.full.declaredMembers

@HiltViewModel
class HomeViewModel
@Inject constructor(
    private val boredApiRepository: BoredApiRepository,
    private val filterCardFunctions: FilterCardFunctions
) : ViewModel() {

    val cards = mutableStateListOf<CardDao>()
    val cardStates = mutableListOf<MutableState<Int>>()
    val loadingState = mutableStateOf(false)
    private var cardLoadCompletedState = true

    var passSelected = mutableStateOf(false)
    var addSelected = mutableStateOf(false)

    var systemBarColor = mutableStateOf(accentColor)
    var systemBarColorFg = mutableStateOf(colorMainFg)
    var systemBarSecondColor = mutableStateOf(accentColor)
    var systemBarSecondColorFg = mutableStateOf(colorSecondFg)
    var categoryColor = mutableStateOf(CategoryColorItem())

    var isLightTheme = true

    fun getRandomActivity() {
        if (cardLoadCompletedState && cards.isEmpty()) {
            setCardLoadingCompletedState(false)
            viewModelScope.launch {
                boredApiRepository.getRandomActivity()
                    .onEach { response ->
                        when (response) {
                            is ApiResponse.Success<*> -> {
                                response.data?.let { data ->
                                    (data as BoredApiResponse).let {
                                        generateCardDaoFromResponse(it).let { cardDao ->
                                            cards.add(cardDao)
                                            cardStates.add(mutableStateOf(IDLE_SELECTION))
                                            setCurrentCard(cardDao)
                                        }
                                    }
                                }
                                loadingState.value = false
                            }
                            is ApiResponse.Error<*> -> {
                                loadingState.value = false
                            }
                            is ApiResponse.Loading<*> -> {
                                loadingState.value = true
                            }
                        }
                    }
                    .launchIn(this)
            }
        }
    }

    private fun setCurrentCard(cardDao: CardDao) {
        CategoryMapper.toSourceFromDestination(cardDao.type).categoryColor.let { color ->
            categoryColor.value = if (isLightTheme) color.colorLight else color.colorDark
        }
    }

    fun removeCard(cardDao: CardDao) {
        if (cardLoadCompletedState && cards.isNotEmpty()) {
            try {
                val index = cards.indexOf(cardDao)
                cards.removeAt(index)
                cardStates.removeAt(index)
            } catch (e: Exception) {
                Timber.d("card remove exception : $e")
            }
        }
    }

    private fun generateCardDaoFromResponse(boredApiResponse: BoredApiResponse) =
        BoredResponseMapper().fromSourceToDestination(boredApiResponse)

    fun setCardLoadingCompletedState(state: Boolean) {
        cardLoadCompletedState = state
    }

    private fun calculateBrightness(color: Color) =
        ((color.red * 299) + (color.green * 587) + (color.blue * 114)) / 1000

    fun isLightColor(color: Color, threshold: Float = 0.6f) =
        calculateBrightness(color = color) >= threshold

    fun transparentValue() =
        if (!isLightTheme) colorMain.copy(alpha = 0f) else colorMainLight.copy(alpha = 0f)

    fun setDragSelectState(selection: Int) {
        when (selection) {
            PASS_SELECTION -> {
                addSelected.value = false
                passSelected.value = true
            }
            ADD_SELECTION -> {
                addSelected.value = true
                passSelected.value = false
            }
            else -> {
                addSelected.value = false
                passSelected.value = false
            }
        }
    }

    fun setIsLightTheme(isLight: Boolean) {
        isLightTheme = isLight
        systemBarColor.value = if (isLight) colorSecondLight else colorSecond
        systemBarColorFg.value = if (isLight) colorSecondLightFg else colorSecondFg
        systemBarSecondColor.value = if (isLight) colorSecondLight else colorSecond
        systemBarSecondColorFg.value = if (isLight) colorSecondLightFg else colorSecondFg
    }

    fun passSelected() {
        onSelected(cardStates.size - 1, selection = PASS_SELECTION)
    }

    fun addSelected() {
        onSelected(cardStates.size - 1, selection = ADD_SELECTION)
    }

    fun removeCompleted(card: CardDao) {
        removeCard(card)
        getRandomActivity()
    }

    fun onSelected(index: Int, selection: Int) {
        if (cardStates.size > index && cardLoadCompletedState) {
            cardStates[index].value = selection
            saveCard(card = cards[index], selection = selection)
            getRandomActivity()
        }
    }

    private fun saveCard(card: CardDao, selection: Int) {
        viewModelScope.launch {
            boredApiRepository.insertActivity(
                boredTable = BoredTableMapper(state = selection).toSourceFromDestination(destination = card)
            )
        }
    }

    //Filter Card Data

    val stateRandomIsChecked = filterCardFunctions.getRandomIsChecked()
    val stateTypeIsChecked = filterCardFunctions.getTypeIsChecked()
    val stateParticipantsIsChecked = filterCardFunctions.getParticipantsIsChecked()
    val statePriceRangeIsChecked = filterCardFunctions.getPriceRangeIsChecked()
    val stateAccessibilityRangeIsChecked = filterCardFunctions.getAccessibilityRangeIsChecked()

    val stateTypeValue = filterCardFunctions.getTypeValue().map {
        CategoryMapper.toSourceFromDestination(it)
    }
    val stateParticipantsValue = filterCardFunctions.getParticipantsValue()
    val statePriceRangeStartValue = filterCardFunctions.getPriceRangeStartValue()
    val statePriceRangeEndValue = filterCardFunctions.getPriceRangeEndValue()
    val stateAccessibilityRangeStartValue = filterCardFunctions.getAccessibilityRangeStartValue()
    val stateAccessibilityRangeEndValue = filterCardFunctions.getAccessibilityRangeEndValue()

    val types = mutableStateOf(
        CategoryMapper.categoriesList
    )

    //Filter Field Random

    fun setRandomIsChecked(value: Boolean) {
        viewModelScope.launch {
            filterCardFunctions.setRandomIsChecked(value)
        }
    }


    //Filter Field Type

    fun setTypeIsChecked(value: Boolean) {
        viewModelScope.launch {
            filterCardFunctions.setTypeIsChecked(value)
        }
    }


    fun setTypeValue(value: String) {
        viewModelScope.launch {
            filterCardFunctions.setTypeValue(value)
        }
    }


    //Filter Field Participants

    fun setParticipantsIsChecked(value: Boolean) {
        viewModelScope.launch {
            filterCardFunctions.setParticipantsIsChecked(value)
        }
    }


    fun setParticipantsValue(value: Int) {
        viewModelScope.launch {
            filterCardFunctions.setParticipantsValue(value)
        }
    }

    //Filter Field PriceRange

    fun setPriceRangeIsChecked(value: Boolean) {
        viewModelScope.launch {
            filterCardFunctions.setPriceRangeIsChecked(value)
        }
    }


    fun setPriceRangeStartValue(value: Float) {
        viewModelScope.launch {
            filterCardFunctions.setPriceRangeStartValue(value)
        }
    }

    fun setPriceRangeEndValue(value: Float) {
        viewModelScope.launch {
            filterCardFunctions.setPriceRangeEndValue(value)
        }
    }

    //Filter Field AccessibilityRange

    fun setAccessibilityRangeIsChecked(value: Boolean) {
        viewModelScope.launch {
            filterCardFunctions.setAccessibilityRangeIsChecked(value)
        }
    }


    fun setAccessibilityRangeStartValue(value: Float) {
        viewModelScope.launch {
            filterCardFunctions.setAccessibilityRangeStartValue(value)
        }
    }

    fun setAccessibilityRangeEndValue(value: Float) {
        viewModelScope.launch {
            filterCardFunctions.setAccessibilityRangeEndValue(value)
        }
    }
}






