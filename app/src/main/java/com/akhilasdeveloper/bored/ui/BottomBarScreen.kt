package com.akhilasdeveloper.bored.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomBarScreen(
        route = "Home",
        title = "Home",
        icon = Icons.Default.Home
    )

    object Activities : BottomBarScreen(
        route = "Activities",
        title = "Activities",
        icon = Icons.Default.Menu
    )

    object About : BottomBarScreen(
        route = "About",
        title = "About",
        icon = Icons.Default.Info
    )
}