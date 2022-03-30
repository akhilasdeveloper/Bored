package com.akhilasdeveloper.bored.data.mapper

import com.akhilasdeveloper.bored.data.CategoryData

class CategoryMapper() : Mapper<CategoryData, String> {
    override fun fromSourceToDestination(source: CategoryData): String = source.key

    override fun toSourceFromDestination(destination: String): CategoryData = when (destination){
        "education" -> {
            CategoryData.Education
        }
        "recreational" -> {
            CategoryData.Recreational
        }
        "social" -> {
            CategoryData.Social
        }
        "diy" -> {
            CategoryData.Diy
        }
        "charity" -> {
            CategoryData.Charity
        }
        "cooking" -> {
            CategoryData.Cooking
        }
        "relaxation" -> {
            CategoryData.Relaxation
        }
        "music" -> {
            CategoryData.Music
        }
        "busywork" -> {
            CategoryData.Busywork
        }
        else -> {
            CategoryData.Music
        }
    }

}