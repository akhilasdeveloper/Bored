package com.akhilasdeveloper.bored.api

import com.akhilasdeveloper.bored.api.response.BoredApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface BoredApiService {

    @GET("activity")
    suspend fun getRandomActivity(): BoredApiResponse?

    @GET("activity")
    suspend fun getRandomActivityByQuery(
        @Query("type") type: String? = null,
        @Query("participants") participants: Int? = null,
        @Query("minprice") minprice: Float? = null,
        @Query("maxprice") maxprice: Float? = null,
        @Query("minaccessibility") minaccessibility: Float? = null,
        @Query("maxaccessibility") maxaccessibility: Float? = null
    ): BoredApiResponse?

}