package com.akhilasdeveloper.bored.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.akhilasdeveloper.bored.util.roundToTwoDecimals
import kotlin.math.roundToInt

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun DemoDialog(
    title: String,
    description: String,
    rangeExpanded: MutableState<Boolean>,
    foregroundColor: Color,
    backgroundColor: Color,
    onOk: () -> Unit,
) {

    ContentDialog(
        title = title,
        titleIcon = Icons.Rounded.Lightbulb,
        widthFactor = .7f,
        onOk = {
            onOk()
        }, content = {
            Column(
                Modifier
                    .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
            ) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.subtitle1,
                    color = foregroundColor
                )
            }
        },
        isExpanded = rangeExpanded,
        backgroundColor = backgroundColor,
        foregroundColor = foregroundColor
    )

}