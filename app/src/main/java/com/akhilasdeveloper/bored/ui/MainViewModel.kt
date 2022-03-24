package com.akhilasdeveloper.bored.ui

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akhilasdeveloper.bored.api.response.ApiResponse
import com.akhilasdeveloper.bored.api.response.BoredApiResponse
import com.akhilasdeveloper.bored.data.CardDao
import com.akhilasdeveloper.bored.repositories.BoredApiRepository
import com.akhilasdeveloper.bored.ui.theme.cardColor1
import com.akhilasdeveloper.bored.ui.theme.cardColors
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

    val boredActivityState : MutableState<CardDao?> = mutableStateOf(null)
    val cards = mutableStateListOf<CardDao>()
    val cardStates = mutableListOf<MutableState<Boolean?>>()

    fun getRandomActivity() {
        viewModelScope.launch {
            boredApiRepository.getRandomActivity()
                .onEach { response ->
                    when(response){
                        is ApiResponse.Success<*> -> {
                            response.data?.let { data->
                                (data as BoredApiResponse).let {
                                    generateCardDaoFromResponse(it).let { cardDao ->
                                        boredActivityState.value = cardDao
                                        cards.add(cardDao)
                                        cardStates.add(mutableStateOf(null))
                                    }
                                }
                            }
                        }
                        is ApiResponse.Error<*> -> {

                        }
                        is ApiResponse.Loading<*> -> {

                        }
                    }
                }
                .launchIn(this)
        }
    }

    fun removeCardAt(index:Int){
        try {
            cards.removeAt(index)
            cardStates.removeAt(index)
        }catch (e:Exception){
            Timber.d("card remove exception : $e")
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


    private fun getRandomCardColor() = cardColors[(cardColors.indices).random()]

}