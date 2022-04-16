package com.akhilasdeveloper.bored.repositories

import com.akhilasdeveloper.bored.api.BoredApiService
import com.akhilasdeveloper.bored.api.response.ApiResponse
import com.akhilasdeveloper.bored.api.response.BoredApiResponse
import com.akhilasdeveloper.bored.db.dao.BoredDao
import com.akhilasdeveloper.bored.db.table.BoredTable
import com.akhilasdeveloper.bored.util.Constants
import com.akhilasdeveloper.bored.util.FilterCardFunctions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.http.Query
import timber.log.Timber
import java.net.UnknownHostException
import javax.inject.Inject

class BoredApiRepository
@Inject constructor(
    private val boredApiService: BoredApiService,
    private val boredDao: BoredDao,
    private val filterCardFunctions: FilterCardFunctions
) {

    suspend fun getRandomActivity(): Flow<Any> {

        val isRandom: Boolean = filterCardFunctions.getRandomIsCheckedRaw()

        var type: String? = null
        var participants: Int? = null
        var minprice: Float? = null
        var maxprice: Float? = null
        var minaccessibility: Float? = null
        var maxaccessibility: Float? = null

        if (!isRandom) {

            val isType: Boolean = filterCardFunctions.getTypeIsCheckedRaw()
            val isParticipants: Boolean = filterCardFunctions.getParticipantsIsCheckedRaw()
            val isPrice: Boolean = filterCardFunctions.getPriceRangeIsCheckedRaw()
            val isAccessibility: Boolean = filterCardFunctions.getAccessibilityRangeIsCheckedRaw()

            if (isType)
                type = filterCardFunctions.getTypeRawValue()
            if (isParticipants)
                participants = filterCardFunctions.getParticipantsRawValue()
            if (isPrice) {
                minprice = filterCardFunctions.getPriceRangeStartRawValue()
                maxprice = filterCardFunctions.getPriceRangeEndRawValue()
            }
            if (isAccessibility) {
                minaccessibility = filterCardFunctions.getAccessibilityRangeStartRawValue()
                maxaccessibility = filterCardFunctions.getAccessibilityRangeEndRawValue()
            }
        }

        return getRandomActivity(
            type = type,
            participants = participants,
            minprice = minprice,
            maxprice = maxprice,
            minaccessibility = minaccessibility,
            maxaccessibility = maxaccessibility
        )
    }

    private suspend fun getRandomActivity(
        type: String? = null,
        participants: Int? = null,
        minprice: Float? = null,
        maxprice: Float? = null,
        minaccessibility: Float? = null,
        maxaccessibility: Float? = null
    ) = flow {
        emit(ApiResponse.Loading<BoredApiResponse>())

        val response = withTimeoutOrNull(10000L){

            try {
                val data = boredApiService.getRandomActivityByQuery(
                    type = type,
                    participants = participants,
                    minprice = minprice,
                    maxprice = maxprice,
                    minaccessibility = minaccessibility,
                    maxaccessibility = maxaccessibility
                )

                if (data == null){
                    return@withTimeoutOrNull ApiResponse.Error(message = "Response Broken", data = null)
                }else{
                    if (data.error == null)
                        return@withTimeoutOrNull ApiResponse.Success(data = data)
                    else
                        return@withTimeoutOrNull ApiResponse.Error(message = data.error, data = null)
                }

            }catch (e: UnknownHostException){
                Timber.e("getRandomActivity UnknownHostException Occurred : ${e.toString()}")
                return@withTimeoutOrNull ApiResponse.Error(message = "Please check the network connection", data = null)
            }
            catch (e: Exception) {
                Timber.e("getRandomActivity Exception Occurred : ${e.toString()}")
                return@withTimeoutOrNull ApiResponse.Error(message = "Unable to fetch the Activity.", data = null)
            }
        }?:ApiResponse.Error(message = "Network timed out.", data = null)

        emit(response)

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

    suspend fun deleteActivityByID(id: Int) {
        withContext(Dispatchers.IO) {
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