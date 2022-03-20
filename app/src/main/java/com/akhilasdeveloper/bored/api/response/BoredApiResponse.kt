package com.akhilasdeveloper.bored.api.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BoredApiResponse(
    @SerializedName("accessibility") @Expose val accessibility: Float?,
    @SerializedName("activity") @Expose val activity: String?,
    @SerializedName("key") @Expose val key: String?,
    @SerializedName("link") @Expose val link: String?,
    @SerializedName("participants") @Expose val participants: Int?,
    @SerializedName("price") @Expose val price: Float?,
    @SerializedName("type") @Expose val type: String?
)