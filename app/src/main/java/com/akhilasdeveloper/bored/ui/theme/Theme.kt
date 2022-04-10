package com.akhilasdeveloper.bored.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.akhilasdeveloper.bored.data.CategoryValueData

private val DarkColorPalette = darkColors(
    primary = Pink700,
    primaryVariant = Pink900,
    secondary = Grey850,
    secondaryVariant = Grey850,
    background = Grey900,
    surface = Grey900,
    error = Red600,
    onPrimary = Grey50,
    onSecondary = Grey50,
    onBackground = Grey300,
    onSurface = Grey300,
    onError = Grey50
)

private val LightColorPalette = lightColors(
    primary = Pink500,
    primaryVariant = Pink700,
    secondary = Grey100,
    secondaryVariant = Grey100,
    background = Grey50,
    surface = Grey50,
    error = Red500,
    onPrimary = Grey50,
    onSecondary = Grey875,
    onBackground = Grey700,
    onSurface = Grey700,
    onError = Grey50
)

val Colors.Surface2
    get() = if (isLight) Grey50 else Grey875

@Composable
fun BoredTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    categoryTheme: State<CategoryValueData> = mutableStateOf(CategoryValueData.Invalid),
    content: @Composable() () -> Unit
) {

    val colors = if (darkTheme) {
        if (categoryTheme.value != CategoryValueData.Invalid) {
            DarkColorPalette.copy(
                primary = categoryTheme.value.categoryColor.colorDark
            )
        } else {
            DarkColorPalette
        }
    } else {
        if (categoryTheme.value != CategoryValueData.Invalid) {
            LightColorPalette.copy(
                primary = categoryTheme.value.categoryColor.colorLight
            )
        } else {
            LightColorPalette
        }
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
