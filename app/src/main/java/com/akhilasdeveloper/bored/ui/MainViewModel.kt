package com.akhilasdeveloper.bored.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(
    private val boredApiRepository: BoredApiRepository
) : ViewModel() {

    val boredActivityState : MutableState<CardDao?> = mutableStateOf(null)

    fun getRandomActivity() {
        viewModelScope.launch {
            boredApiRepository.getRandomActivity()
                .onEach { response ->
                    when(response){
                        is ApiResponse.Success<*> -> {
                            response.data?.let { data->
                                (data as BoredApiResponse).let {
                                    boredActivityState.value = generateCardDaoFromResponse(it)
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