package com.akhilasdeveloper.bored.ui

import android.R
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akhilasdeveloper.bored.api.response.ApiResponse
import com.akhilasdeveloper.bored.api.response.BoredApiResponse
import com.akhilasdeveloper.bored.data.CardColor
import com.akhilasdeveloper.bored.data.CardDao
import com.akhilasdeveloper.bored.repositories.BoredApiRepository
import com.akhilasdeveloper.bored.ui.theme.cardColors
import com.akhilasdeveloper.bored.ui.theme.colorCardSecond
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

    private fun getRandomCardColor(): CardColor {
        val color = generateRandomColor()
        val colorFg = getForegroundColor(color = color)
        val colorSecond = getColorSecond(color)
        val colorSecondFg = getForegroundColor(colorSecond)
        Timber.d("Brightness : ${calculateBrightness(color = color)}")
        return CardColor(
            colorCardBg = color,
            colorCardFg = colorFg,
            colorCardSecondBg = colorSecond,
            colorCardSecondFg = colorSecondFg
        )
    }

    private fun getColorSecond(color: Color): Color =
        if (calculateBrightness(color = color) >= 0.5f) {
            colorCardSecond
        } else {
            Color.White
        }

    fun setCardLoadingCompletedState(state: Boolean) {
        cardLoadCompletedState = state
    }

    fun getCardLoadingCompletedState() = cardLoadCompletedState

    private fun generateRandomColor() = Color(
        red = generateRandomColorValue(),
        blue = generateRandomColorValue(),
        green = generateRandomColorValue()
    )

    private fun generateRandomColorValue() = (0..254).random()
    private fun calculateBrightness(color: Color) =
        ((color.red * 299) + (color.green * 587) + (color.blue * 114)) / 1000

    private fun getForegroundColor(color: Color): Color =
        if (calculateBrightness(color = color) >= 0.6f) {
            colorCardSecond
        } else {
            Color.White
        }

}



