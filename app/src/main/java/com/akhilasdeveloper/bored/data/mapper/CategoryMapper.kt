package com.akhilasdeveloper.bored.data.mapper

import com.akhilasdeveloper.bored.data.CategoryData

object CategoryMapper : Mapper<CategoryData, String> {

    val categoriesList
        get() = categoriesMap.values

    private val categoriesMap
        get() = mapOf(
            Pair(CategoryData.Invalid.key,CategoryData.Invalid),
            Pair(CategoryData.Education.key,CategoryData.Education),
            Pair(CategoryData.Busywork.key,CategoryData.Busywork),
            Pair(CategoryData.Charity.key,CategoryData.Charity),
            Pair(CategoryData.Cooking.key,CategoryData.Cooking),
            Pair(CategoryData.Music.key,CategoryData.Music),
            Pair(CategoryData.Diy.key,CategoryData.Diy),
            Pair(CategoryData.Recreational.key,CategoryData.Recreational),
            Pair(CategoryData.Relaxation.key,CategoryData.Relaxation),
            Pair(CategoryData.Social.key,CategoryData.Social)
        )


    override fun fromSourceToDestination(source: CategoryData): String = source.key

    override fun toSourceFromDestination(destination: String): CategoryData = categoriesMap[destination]?:CategoryData.Invalid

}