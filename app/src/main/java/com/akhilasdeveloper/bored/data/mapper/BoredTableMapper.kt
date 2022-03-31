package com.akhilasdeveloper.bored.data.mapper
import com.akhilasdeveloper.bored.data.CardDao
import com.akhilasdeveloper.bored.db.table.BoredTable
import com.akhilasdeveloper.bored.util.Constants

class BoredTableMapper(private val state: Int = Constants.IDLE_SELECTION) : Mapper<BoredTable, CardDao> {
    override fun fromSourceToDestination(source: BoredTable): CardDao = CardDao(
        key = source.key,
        activityName = source.activity,
        link = source.link,
        price = source.price,
        participants = source.participants,
        type = source.type,
        accessibility = source.accessibility,
        isCompleted = source.isCompleted,
        id = source.id,
        createdDate = source.createdDate
    )

    override fun toSourceFromDestination(destination: CardDao): BoredTable = BoredTable(
        key = destination.key,
        activity = destination.activityName,
        link = destination.link,
        price = destination.price,
        participants = destination.participants,
        type = destination.type,
        accessibility = destination.accessibility,
        createdDate = destination.createdDate,
        state = state,
        isCompleted = destination.isCompleted
    )

}