package com.akhilasdeveloper.bored.repositories

import com.akhilasdeveloper.bored.api.BoredApiService
import com.akhilasdeveloper.bored.api.response.ApiResponse
import com.akhilasdeveloper.bored.api.response.BoredApiResponse
import com.akhilasdeveloper.bored.db.dao.BoredDao
import com.akhilasdeveloper.bored.db.table.BoredTable
import com.akhilasdeveloper.bored.util.Constants
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
            boredDao.deleteAllSkippedActivitiesByKey(key = boredTable.key)
            if (boredTable.state == Constants.ADD_SELECTION) {
                if (boredDao.countOfNotCompletedTODOActivities(key = boredTable.key) <= 0)
                    boredDao.addActivity(boredTable = boredTable)
                else
                    boredDao.updateCreatedDateOfNotCompletedTODOActivity(
                        key = boredTable.key,
                        createdDate = System.currentTimeMillis()
                    )
            } else if (boredTable.state == Constants.PASS_SELECTION) {
                boredDao.addActivity(boredTable = boredTable)
            }
        }
    }

    suspend fun updateState(id: Int, key: String, state: Int) {
        withContext(Dispatchers.IO) {

            if (state == Constants.ADD_SELECTION) {
                if (boredDao.countOfNotCompletedTODOActivities(key = key) <= 0) {
                    boredDao.updateState(id = id, state = state)
                    boredDao.deleteAllSkippedActivitiesByKey(key = key)
                } else {
                    boredDao.updateCreatedDateOfNotCompletedTODOActivity(
                        key = key,
                        createdDate = System.currentTimeMillis()
                    )
                    boredDao.deleteAllSkippedActivitiesByKey(key = key)
                }
            } else if (state == Constants.PASS_SELECTION) {
                boredDao.updateState(id = id, state = state)
            }
        }
    }

    fun fetchAddActivities() = boredDao.getAddActivities()
    fun fetchPassActivities() = boredDao.getPassActivities()

    suspend fun deleteActivityByID(id: Int){
        withContext(Dispatchers.IO){
            boredDao.deleteActivityByID(id)
        }
    }

    suspend fun updateIsCompleted(id: Int, key: String, isCompleted: Boolean) {
        withContext(Dispatchers.IO) {
            if (!isCompleted)
                boredDao.deleteAllNotCompletedTODOActivities(key = key)
            boredDao.updateIsCompleted(id, isCompleted)
        }
    }

}