package com.akhilasdeveloper.bored.data.mapper

interface Mapper <Source, Destination> {
    fun fromSourceToDestination(source: Source): Destination
    fun toSourceFromDestination(destination: Destination): Source
}