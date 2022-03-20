package com.akhilasdeveloper.bored.repositories

import com.akhilasdeveloper.bored.api.BoredApiService
import com.akhilasdeveloper.bored.api.response.ApiResponse
import com.akhilasdeveloper.bored.api.response.BoredApiResponse
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class BoredApiRepository
@Inject constructor(
    private val boredApiService: BoredApiService
) {
    suspend fun getRandomActivity() = flow {
        emit(ApiResponse.Loading<BoredApiResponse>())
        try {
            val data = boredApiService.getRandomActivity()

            Timber.d("Response : $data")

            data?.let {
                emit(ApiResponse.Success(data = data))
            } ?: kotlin.run {
                emit(
                    emit(ApiResponse.Error(message = "Response Broken", data = null))
                )
            }
        } catch (e: Exception) {
            emit(ApiResponse.Error(message = "Exception Occurred : ${e.toString()}", data = null))
        }

    }
}