package com.akhilasdeveloper.bored.repositories

import com.akhilasdeveloper.bored.api.BoredApiService
import com.akhilasdeveloper.bored.api.response.ApiResponse
import com.akhilasdeveloper.bored.api.response.BoredApiResponse
import com.akhilasdeveloper.bored.db.dao.BoredDao
import com.akhilasdeveloper.bored.db.table.BoredTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class BoredApiRepository
@Inject constructor(
    private val boredApiService: BoredApiService,
    private val boredDao: BoredDao,
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

    suspend fun insertActivity(boredTable: BoredTable) {
        withContext(Dispatchers.IO) {
            if (boredDao.countOfNotCompletedActivities(key = boredTable.key) <= 0)
                boredDao.addActivity(boredTable = boredTable)
            else
                boredDao.updateCreatedDateOfNotCompletedActivity(
                    key = boredTable.key,
                    createdDate = System.currentTimeMillis()
                )
        }
    }

    fun fetchAddActivities() = boredDao.getAddActivities()
    fun fetchPassActivities() = boredDao.getPassActivities()

    suspend fun updateCreatedDate(id:Int, createdDate:Long){
        withContext(Dispatchers.IO) {
            boredDao.updateCreatedDate(id, createdDate)
        }
    }

    suspend fun updateIsCompleted(id: Int, key: String, isCompleted: Boolean) {
        withContext(Dispatchers.IO) {
            if (!isCompleted)
                boredDao.deleteAllNotCompletedActivity(key = key)
            boredDao.updateIsCompleted(id, isCompleted)
        }
    }

}