package com.akhilasdeveloper.bored.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.akhilasdeveloper.bored.ui.theme.*

sealed class CategoryValueData(
    val title: String,
    val icon: ImageVector,
    val key: String,
    val categoryColor: CategoryColors
) {
    object Education : CategoryValueData(
        title = "Education",
        icon = Icons.Rounded.School,
        key = "education",
        categoryColor = categoryValueColor1
    )

    object Recreational : CategoryValueData(
        title = "Recreational",
        icon = Icons.Rounded.EmojiObjects,
        key = "recreational",
        categoryColor = categoryValueColor2
    )

    object Social : CategoryValueData(
        title = "Social",
        icon = Icons.Rounded.Groups,
        key = "social",
        categoryColor = categoryValueColor3
    )

    object Diy : CategoryValueData(
        title = "Diy",
        icon = Icons.Rounded.Construction,
        key = "diy",
        categoryColor = categoryValueColor4
    )

    object Charity : CategoryValueData(
        title = "Charity",
        icon = Icons.Rounded.VolunteerActivism,
        key = "charity",
        categoryColor = categoryValueColor5
    )

    object Cooking : CategoryValueData(
        title = "Cooking",
        icon = Icons.Rounded.SoupKitchen,
        key = "cooking",
        categoryColor = categoryValueColor6
    )

    object Relaxation : CategoryValueData(
        title = "Relaxation",
        icon = Icons.Rounded.SelfImprovement,
        key = "relaxation",
        categoryColor = categoryValueColor7
    )

    object Music : CategoryValueData(
        title = "Music",
        icon = Icons.Rounded.MusicNote,
        key = "music",
        categoryColor = categoryValueColor8
    )

    object Busywork : CategoryValueData(
        title = "Busywork",
        icon = Icons.Rounded.EventBusy,
        key = "busywork",
        categoryColor = categoryValueColor9
    )

    object Invalid : CategoryValueData(
        title = "None",
        icon = Icons.Rounded.Error,
        key = "invalid",
        categoryColor = categoryValueColorError
    )

}
