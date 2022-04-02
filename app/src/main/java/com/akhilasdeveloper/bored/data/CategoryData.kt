package com.akhilasdeveloper.bored.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.akhilasdeveloper.bored.ui.theme.*

sealed class CategoryData(
    val title: String,
    val icon: ImageVector,
    val key: String,
    val categoryColor: CategoryColor
) {
    object Education : CategoryData(
        title = "Education",
        icon = Icons.Rounded.School,
        key = "education",
        categoryColor = categoryColor1
    )

    object Recreational : CategoryData(
        title = "Recreational",
        icon = Icons.Rounded.EmojiObjects,
        key = "recreational",
        categoryColor = categoryColor2
    )

    object Social : CategoryData(
        title = "Social",
        icon = Icons.Rounded.Groups,
        key = "social",
        categoryColor = categoryColor3
    )

    object Diy : CategoryData(
        title = "Diy",
        icon = Icons.Rounded.Construction,
        key = "diy",
        categoryColor = categoryColor4
    )

    object Charity : CategoryData(
        title = "Charity",
        icon = Icons.Rounded.VolunteerActivism,
        key = "charity",
        categoryColor = categoryColor5
    )

    object Cooking : CategoryData(
        title = "Cooking",
        icon = Icons.Rounded.SoupKitchen,
        key = "cooking",
        categoryColor = categoryColor6
    )

    object Relaxation : CategoryData(
        title = "Relaxation",
        icon = Icons.Rounded.SelfImprovement,
        key = "relaxation",
        categoryColor = categoryColor7
    )

    object Music : CategoryData(
        title = "Music",
        icon = Icons.Rounded.MusicNote,
        key = "music",
        categoryColor = categoryColor8
    )

    object Busywork : CategoryData(
        title = "Busywork",
        icon = Icons.Rounded.EventBusy,
        key = "busywork",
        categoryColor = categoryColor9
    )

    object Invalid : CategoryData(
        title = "None",
        icon = Icons.Rounded.Error,
        key = "invalid",
        categoryColor = categoryColorError
    )

}