package com.akhilasdeveloper.bored.api

import com.akhilasdeveloper.bored.api.response.BoredApiResponse
import retrofit2.http.GET

interface BoredApiService {

    @GET("activity")
    suspend fun getRandomActivity(): BoredApiResponse?

}