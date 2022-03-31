package com.akhilasdeveloper.bored.db.table

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "bored_table")
data class BoredTable(
    @SerializedName("key") @Expose val key: String,
    @SerializedName("activity") @Expose val activity: String?,
    @SerializedName("type") @Expose val type: String,
    @SerializedName("accessibility") @Expose val accessibility: Float?,
    @SerializedName("link") @Expose val link: String?,
    @SerializedName("participants") @Expose val participants: Int?,
    @SerializedName("price") @Expose val price: Float?,
    @SerializedName("createdDate") @Expose val createdDate: Long,
    @SerializedName("state") @Expose val state: Int,
    @SerializedName("isCompleted") @Expose val isCompleted: Boolean
){
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id") @Expose var id: Int? = null
}