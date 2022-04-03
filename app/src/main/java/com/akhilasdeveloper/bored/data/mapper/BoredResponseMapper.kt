package com.akhilasdeveloper.bored.data.mapper

import com.akhilasdeveloper.bored.api.response.BoredApiResponse
import com.akhilasdeveloper.bored.data.CardDao
import com.akhilasdeveloper.bored.data.CategoryData

class BoredResponseMapper : Mapper<BoredApiResponse, CardDao> {
    override fun fromSourceToDestination(source: BoredApiResponse): CardDao = CardDao(
        key = source.key ?: CategoryData.Invalid.key,
        activityName = source.activity,
        link = source.link,
        price = source.price,
        participants = source.participants,
        type = source.type ?: CategoryData.Invalid.key,
        accessibility = source.accessibility,
        isCompleted = false,
        id = null,
        createdDate = System.currentTimeMillis()
    )

    override fun toSourceFromDestination(destination: CardDao): BoredApiResponse = BoredApiResponse(
        key = destination.key,
        activity = destination.activityName,
        link = destination.link,
        price = destination.price,
        participants = destination.participants,
        type = destination.type,
        accessibility = destination.accessibility,
        error = null
    )
}