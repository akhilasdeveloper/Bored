package com.akhilasdeveloper.bored.data

import androidx.compose.ui.graphics.Color
import com.akhilasdeveloper.bored.ui.theme.accentColor
import com.akhilasdeveloper.bored.ui.theme.accentFgColor
import com.akhilasdeveloper.bored.ui.theme.colorCardSecond
import com.akhilasdeveloper.bored.ui.theme.colorCardSecondFg

data class CategoryColor(
    val colorLight: CategoryColorItem,
    val colorDark: CategoryColorItem
)

data class CategoryColorItem(
    val colorBg: Color = accentColor,
    val colorFg: Color = accentFgColor,
    val colorSecondBg: Color = colorCardSecond,
    val colorSecondFg: Color = colorCardSecondFg
)