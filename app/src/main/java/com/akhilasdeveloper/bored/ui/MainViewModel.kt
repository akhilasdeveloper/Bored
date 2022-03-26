package com.akhilasdeveloper.bored.ui

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akhilasdeveloper.bored.api.response.ApiResponse
import com.akhilasdeveloper.bored.api.response.BoredApiResponse
import com.akhilasdeveloper.bored.data.CardColor
import com.akhilasdeveloper.bored.data.CardDao
import com.akhilasdeveloper.bored.repositories.BoredApiRepository
import com.akhilasdeveloper.bored.ui.theme.colorCardSecond
import com.akhilasdeveloper.bored.ui.theme.colorCardSecondFg
import com.akhilasdeveloper.bored.ui.theme.colorMain
import com.akhilasdeveloper.bored.ui.theme.colorMainLight
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
    val cardStates = mutableListOf<MutableState<Boolean?>>()
    val loadingState = mutableStateOf(false)
    private var cardLoadCompletedState = true

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
                                            cardStates.add(mutableStateOf(null))
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
}



