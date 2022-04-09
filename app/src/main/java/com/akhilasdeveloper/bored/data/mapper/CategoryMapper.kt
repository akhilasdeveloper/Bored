package com.akhilasdeveloper.bored.data.mapper

import com.akhilasdeveloper.bored.data.CategoryValueData

object CategoryValueMapper : Mapper<CategoryValueData, String> {

    val categoriesList
        get() = categoriesMap.values

    private val categoriesMap
        get() = mapOf(
            Pair(CategoryValueData.Invalid.key,CategoryValueData.Invalid),
            Pair(CategoryValueData.Education.key,CategoryValueData.Education),
            Pair(CategoryValueData.Busywork.key,CategoryValueData.Busywork),
            Pair(CategoryValueData.Charity.key,CategoryValueData.Charity),
            Pair(CategoryValueData.Cooking.key,CategoryValueData.Cooking),
            Pair(CategoryValueData.Music.key,CategoryValueData.Music),
            Pair(CategoryValueData.Diy.key,CategoryValueData.Diy),
            Pair(CategoryValueData.Recreational.key,CategoryValueData.Recreational),
            Pair(CategoryValueData.Relaxation.key,CategoryValueData.Relaxation),
            Pair(CategoryValueData.Social.key,CategoryValueData.Social)
        )


    override fun fromSourceToDestination(source: CategoryValueData): String = source.key

    override fun toSourceFromDestination(destination: String): CategoryValueData = categoriesMap[destination]?:CategoryValueData.Invalid

}