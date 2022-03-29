package com.akhilasdeveloper.bored.ui

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akhilasdeveloper.bored.api.response.ApiResponse
import com.akhilasdeveloper.bored.api.response.BoredApiResponse
import com.akhilasdeveloper.bored.data.CardColor
import com.akhilasdeveloper.bored.data.CardDao
import com.akhilasdeveloper.bored.repositories.BoredApiRepository
import com.akhilasdeveloper.bored.ui.theme.*
import com.akhilasdeveloper.bored.util.Constants
import com.akhilasdeveloper.bored.util.Constants.ADD_SELECTION
import com.akhilasdeveloper.bored.util.Constants.IDLE_SELECTION
import com.akhilasdeveloper.bored.util.Constants.PASS_SELECTION
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(
    private val boredApiRepository: BoredApiRepository
) : ViewModel() {

    val cards = mutableStateListOf<CardDao>()
    val cardStates = mutableListOf<MutableState<Int>>()
    val loadingState = mutableStateOf(false)
    private var cardLoadCompletedState = true

    var passSelected = mutableStateOf(false)
    var addSelected = mutableStateOf(false)

    var progressBarColor = mutableStateOf(accentColor)
    var systemBarColor = mutableStateOf(accentColor)
    var systemBarColorFg = mutableStateOf(colorMain)
    var systemBarSecondColor = mutableStateOf(accentColor)
    var systemBarSecondColorFg = mutableStateOf(colorSecondFg)
    var selectionColor = mutableStateOf(transparentValue())
    var selectionColorFg = mutableStateOf(transparentValue())

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
        progressBarColor.value = cardDao.cardColor.colorCardBg
        selectionColor.value = cardDao.cardColor.colorCardBg
        selectionColorFg.value = cardDao.cardColor.colorCardFg
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

    private fun generateCardDaoFromResponse(boredApiResponse: BoredApiResponse) = CardDao(
        activityName = boredApiResponse.activity,
        link = boredApiResponse.link,
        price = boredApiResponse.price,
        participants = boredApiResponse.participants,
        type = boredApiResponse.type,
        accessibility = boredApiResponse.accessibility,
        cardColor = getRandomCardColor()
    )

    fun setCardLoadingCompletedState(state: Boolean) {
        cardLoadCompletedState = state
    }

    fun getCardLoadingCompletedState() = cardLoadCompletedState

    private fun getRandomCardColor(): CardColor {
        val color = generateRandomColor()
        val colorFg = getForegroundColor(color = color)
        val colorSecond = getColorSecond(color)
        val colorSecondFg = getForegroundColor(colorSecond)

        return CardColor(
            colorCardBg = color,
            colorCardFg = colorFg,
            colorCardSecondBg = colorSecond,
            colorCardSecondFg = colorSecondFg
        )
    }

    private fun generateRandomColor() = Color(
        red = generateRandomColorValue(),
        blue = generateRandomColorValue(),
        green = generateRandomColorValue()
    )

    private fun generateRandomColorValue() = if(isLightTheme) (100..254).random() else (50..180).random()

    private fun calculateBrightness(color: Color) =
        ((color.red * 299) + (color.green * 587) + (color.blue * 114)) / 1000

    fun isLightColor(color: Color, threshold:Float = 0.7f) = calculateBrightness(color = color) >= threshold

    private fun getForegroundColor(color: Color): Color =
        if (isLightColor(color = color)) {
            colorCardSecond
        } else {
            Color.White
        }

    private fun getColorSecond(color: Color): Color =
        if (isLightColor(color = color, threshold = 0.5f)) {
            colorCardSecond
        } else {
            Color.White
        }
    fun transparentValue() = if (!isLightTheme) colorMain.copy(alpha = 0f) else colorMainLight.copy(alpha = 0f)

    fun setDragSelectState(selection:Int){
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

    fun setIsLightTheme(isLight:Boolean){
        isLightTheme = isLight
        systemBarColor.value = if (isLight) colorMainLight else colorMain
        systemBarColorFg.value = if (isLight) colorMain else colorMainLight
        systemBarSecondColor.value = if (isLight) colorSecondLight else colorSecond
        systemBarSecondColorFg.value = if (isLight) colorSecondLightFg else colorSecondFg
    }

    fun passSelected(){
        if (getCardLoadingCompletedState()) {
            cardStates.last().value = PASS_SELECTION
            getRandomActivity()
        }
    }

    fun addSelected(){
        if (getCardLoadingCompletedState()) {
            cardStates.last().value = ADD_SELECTION
            getRandomActivity()
        }
    }

    fun removeCompleted(card: CardDao) {
        removeCard(card)
        getRandomActivity()
    }

    fun onSelected(index:Int,selection: Int) {
        if(cardStates.size > index) {
            cardStates[index].value = selection
            getRandomActivity()
        }
    }
}



