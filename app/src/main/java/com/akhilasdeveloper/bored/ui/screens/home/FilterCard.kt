package com.akhilasdeveloper.bored.ui.screens.home

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.SliderDefaults.TickAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.akhilasdeveloper.bored.data.CategoryValueData
import com.akhilasdeveloper.bored.ui.theme.Surface2
import com.akhilasdeveloper.bored.util.roundToTwoDecimals
import kotlin.math.roundToInt

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
fun FilterCard(viewModel: HomeViewModel) {

    val expanded = remember { mutableStateOf(false) }

    val stateRandomIsChecked = viewModel.stateRandomIsChecked.collectAsState(initial = false)
    val stateTypeIsChecked = viewModel.stateTypeIsChecked.collectAsState(initial = false)
    val stateParticipantsIsChecked =
        viewModel.stateParticipantsIsChecked.collectAsState(initial = false)
    val statePriceRangeIsChecked =
        viewModel.statePriceRangeIsChecked.collectAsState(initial = false)
    val stateAccessibilityRangeIsChecked =
        viewModel.stateAccessibilityRangeIsChecked.collectAsState(initial = false)

    val isFilterOn = derivedStateOf {
        !stateRandomIsChecked.value &&
                (stateTypeIsChecked.value ||
                        stateParticipantsIsChecked.value ||
                        statePriceRangeIsChecked.value ||
                        stateAccessibilityRangeIsChecked.value)
    }

    Box(
        Modifier
            .fillMaxSize()
            .padding(20.dp), contentAlignment = Alignment.BottomCenter
    ) {
        Box {
            Card(
                shape = RoundedCornerShape(100.dp),
                backgroundColor = MaterialTheme.colors.primary
            ) {

                Icon(
                    imageVector = Icons.Rounded.FilterAlt,
                    contentDescription = "Category Icon",
                    tint = MaterialTheme.colors.onPrimary,
                    modifier = Modifier
                        .clickable(
                            indication = rememberRipple(color = MaterialTheme.colors.onPrimary),
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = {
                                expanded.value = true
                            }
                        )
                        .padding(10.dp)
                )

            }

            if (isFilterOn.value)
                Spacer(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colors.primary,
                            shape = RoundedCornerShape(100.dp)
                        )
                        .background(color = MaterialTheme.colors.onPrimary)
                        .padding(5.dp)
                        .align(Alignment.TopEnd)
                )
        }


        FilterContents(
            viewModel = viewModel,
            expandedMain = expanded,
            backgroundColor = MaterialTheme.colors.surface,
            foregroundColor = MaterialTheme.colors.onSurface
        )
    }
}

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
fun FilterContents(
    viewModel: HomeViewModel,
    expandedMain: MutableState<Boolean>,
    foregroundColor: Color,
    backgroundColor: Color
) {
    ContentDialog(
        isExpanded = expandedMain,
        title = "Filter",
        titleIcon = Icons.Rounded.FilterAlt,
        foregroundColor = foregroundColor,
        backgroundColor = backgroundColor,
        onOk = {
            viewModel.getClearAndGetActivity()
        },
        content = {
            MainFilterContents(
                viewModel = viewModel,
                backgroundColor = backgroundColor,
                foregroundColor = foregroundColor
            )
        }
    )

}

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
fun MainFilterContents(
    viewModel: HomeViewModel, foregroundColor: Color,
    backgroundColor: Color
) {

    val stateRandomIsChecked = viewModel.stateRandomIsChecked.collectAsState(false)

    val typeExpanded = remember { mutableStateOf(false) }
    val participantsExpanded = remember { mutableStateOf(false) }
    val priceRangeExpanded = remember { mutableStateOf(false) }
    val accessibilityRangeExpanded = remember { mutableStateOf(false) }

    Column(Modifier.background(backgroundColor)) {

        val categoryMainData =
            viewModel.stateTypeValue.collectAsState(initial = CategoryValueData.Invalid)
        val categoryMainTheme =
            derivedStateOf { if (viewModel.isLightTheme.value) categoryMainData.value.categoryColor.colorLight else categoryMainData.value.categoryColor.colorDark }

        FilterItem(
            text = "Random",
            foregroundColor = foregroundColor,
            backgroundColor = backgroundColor,
            isChecked = stateRandomIsChecked.value,
            onChecked = {
                viewModel.setRandomIsChecked(it)
            }
        )

        ExposedDropDown(expanded = typeExpanded, foregroundColor = foregroundColor,
            backgroundColor = backgroundColor, headerItem = {
                FilterItem(
                    text = "Type",
                    foregroundColor = foregroundColor,
                    backgroundColor = backgroundColor,
                    isDisabled = stateRandomIsChecked.value,
                    icon = categoryMainData.value.icon,
                    iconColor = categoryMainTheme.value,
                    isChecked = viewModel.stateTypeIsChecked.collectAsState(false).value,
                    onChecked = {
                        viewModel.setTypeIsChecked(it)
                    }, onClicked = {
                        typeExpanded.value = !typeExpanded.value
                    }
                )
            }, dropDownItem = { contentItem ->
                viewModel.types.value.forEach { category ->

                    val categoryTheme =
                        derivedStateOf { if (viewModel.isLightTheme.value) category.categoryColor.colorLight else category.categoryColor.colorDark }

                    contentItem(onClick = {
                        viewModel.setTypeValue(category.key)
                    }) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = category.icon,
                                contentDescription = "Category Icon",
                                tint = MaterialTheme.colors.onPrimary,
                                modifier = Modifier
                                    .padding(
                                        top = 8.dp,
                                        bottom = 8.dp,
                                        start = 8.dp
                                    )
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(color = categoryTheme.value)
                                    .padding(5.dp)
                            )

                            Text(
                                modifier = Modifier.padding(8.dp),
                                text = category.title,
                                style = MaterialTheme.typography.subtitle1,
                                color = foregroundColor
                            )

                        }
                    }
                }
            })

        ExposedDropDown(
            expanded = participantsExpanded,
            headerItem = {
                FilterItem(
                    text = "Participants",
                    foregroundColor = foregroundColor,
                    backgroundColor = backgroundColor,
                    isDisabled = stateRandomIsChecked.value,
                    value = "${viewModel.stateParticipantsValue.collectAsState(initial = 0).value}",
                    isChecked = viewModel.stateParticipantsIsChecked.collectAsState(false).value,
                    onChecked = {
                        viewModel.setParticipantsIsChecked(it)
                    },
                    onClicked = {
                        participantsExpanded.value = !participantsExpanded.value
                    }
                )
            },
            dropDownItem = { contentItem ->
                (1..5).forEach {
                    contentItem(
                        onClick = {
                            viewModel.setParticipantsValue(it)
                            participantsExpanded.value = false
                        }) {

                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = "$it",
                            style = MaterialTheme.typography.subtitle1,
                            color = foregroundColor
                        )

                    }
                }
            },
            foregroundColor = foregroundColor,
            backgroundColor = backgroundColor
        )

        val priceRangeStart =
            viewModel.statePriceRangeStartValue.collectAsState(initial = 0f)
        val priceRangeEnd =
            viewModel.statePriceRangeEndValue.collectAsState(initial = 1f)
        val priceRange =
            derivedStateOf { priceRangeStart.value..priceRangeEnd.value }

        FilterItem(
            text = "Price Range",
            foregroundColor = foregroundColor,
            backgroundColor = backgroundColor,
            isDisabled = stateRandomIsChecked.value,
            value = "${(priceRange.value.start * 100).roundToInt()}% - ${(priceRange.value.endInclusive * 100).roundToInt()}%",
            isChecked = viewModel.statePriceRangeIsChecked.collectAsState(false).value,
            onChecked = {
                viewModel.setPriceRangeIsChecked(it)
            },
            onClicked = {
                priceRangeExpanded.value = !priceRangeExpanded.value
            }
        )

        SliderDialog(title = "Accessibility Range",
            initValue = priceRange,
            backgroundColor = backgroundColor,
            foregroundColor = foregroundColor,
            onOk = {
                viewModel.setPriceRangeStartValue(
                    it.start.roundToTwoDecimals()
                )
                viewModel.setPriceRangeEndValue(it.endInclusive.roundToTwoDecimals())
            }, rangeExpanded = priceRangeExpanded, onValueChange = {
            })

        val accessibilityRangeStart =
            viewModel.stateAccessibilityRangeStartValue.collectAsState(initial = 0f)
        val accessibilityRangeEnd =
            viewModel.stateAccessibilityRangeEndValue.collectAsState(initial = 1f)
        val accessibilityRange =
            derivedStateOf { accessibilityRangeStart.value..accessibilityRangeEnd.value }

        FilterItem(
            text = "Accessibility Range",
            foregroundColor = foregroundColor,
            backgroundColor = backgroundColor,
            isDisabled = stateRandomIsChecked.value,
            value = "${(accessibilityRange.value.start * 100).roundToInt()}% - ${(accessibilityRange.value.endInclusive * 100).roundToInt()}%",
            isChecked = viewModel.stateAccessibilityRangeIsChecked.collectAsState(false).value,
            onChecked = {
                viewModel.setAccessibilityRangeIsChecked(it)
            }, onClicked = {
                accessibilityRangeExpanded.value = !accessibilityRangeExpanded.value
            }
        )

        SliderDialog(title = "Accessibility Range",
            initValue = accessibilityRange,
            backgroundColor = backgroundColor,
            foregroundColor = foregroundColor,
            onOk = {
                viewModel.setAccessibilityRangeStartValue(it.start.roundToTwoDecimals())
                viewModel.setAccessibilityRangeEndValue(it.endInclusive.roundToTwoDecimals())
            }, rangeExpanded = accessibilityRangeExpanded, onValueChange = {
            })

    }
}

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun SliderDialog(
    title: String,
    rangeExpanded: MutableState<Boolean>,
    foregroundColor: Color,
    backgroundColor: Color,
    initValue: State<ClosedFloatingPointRange<Float>>,
    onValueChange: (rangeSliderValue: ClosedFloatingPointRange<Float>) -> Unit,
    onOk: (rangeSliderValue: ClosedFloatingPointRange<Float>) -> Unit,
) {

    val rangeSlider = remember {
        mutableStateOf(initValue.value.start..initValue.value.endInclusive)
    }

    ContentDialog(
        title = title,
        widthFactor = .7f,
        onOk = {
            onOk(rangeSlider.value)
        }, content = {
            Column(Modifier.padding(top = 12.dp)) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "From ${(rangeSlider.value.start * 100).roundToInt()} To ${(rangeSlider.value.endInclusive * 100).roundToInt()}%",
                    style = MaterialTheme.typography.subtitle1,
                    color = foregroundColor
                )
                Box(Modifier.padding(12.dp)) {
                    RangeSlider(
                        steps = 100,
                        values = rangeSlider.value,
                        onValueChange = {
                            onValueChange(it)
                            rangeSlider.value =
                                it.start.roundToTwoDecimals()..it.endInclusive.roundToTwoDecimals()
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colors.primary,
                            activeTrackColor = backgroundColor.copy(alpha = .5f),
                            inactiveTickColor = MaterialTheme.colors.primary,
                        )
                    )
                }
            }
        },
        isExpanded = rangeExpanded,
        backgroundColor = backgroundColor,
        foregroundColor = foregroundColor
    )

}

@ExperimentalMaterialApi
@Composable
fun ExposedDropDown(
    expanded: MutableState<Boolean>,
    headerItem: @Composable () -> Unit,
    dropDownItem: @Composable (dropDownMenuItem: @Composable (onClick: () -> Unit, content: @Composable() (RowScope.() -> Unit)) -> Unit) -> Unit,
    foregroundColor: Color,
    backgroundColor: Color
) {

    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = {
        }
    ) {

        headerItem.invoke()

        ExposedDropdownMenu(
            modifier = Modifier.background(MaterialTheme.colors.Surface2),
            expanded = expanded.value,
            onDismissRequest = {
                expanded.value = false
            }
        ) {
            dropDownItem { onClick, content ->
                DropdownMenuItem(
                    modifier = Modifier.background(MaterialTheme.colors.Surface2),
                    onClick = {
                        onClick.invoke()
                        expanded.value = false
                    }, content = content
                )
            }
        }

    }
}

@ExperimentalComposeUiApi
@Composable
fun ContentDialog(
    modifier: Modifier = Modifier,
    title: String,
    titleIcon: ImageVector? = null,
    widthFactor: Float = .8f,
    onDismiss: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    onOk: (() -> Unit)? = null,
    content: @Composable() (() -> Unit)? = null,
    isExpanded: MutableState<Boolean>,
    backgroundColor: Color,
    foregroundColor: Color
) {

    val currentContent by rememberUpdatedState(content)
    val titlePadding = derivedStateOf { if (titleIcon == null) 18.dp else 10.dp }
    val scrollState = rememberScrollState()

    if (isExpanded.value) {

        Dialog(
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            ),
            onDismissRequest = {
                onDismiss?.invoke()
                isExpanded.value = false
            }) {
            Surface(
                modifier = modifier.fillMaxWidth(widthFactor.coerceIn(0f, 1f)),
                shape = RoundedCornerShape(10.dp),
                color = backgroundColor
            ) {
                Column(
                    Modifier.verticalScroll(scrollState)
                ) {
                    Row(
                        Modifier
                            .background(MaterialTheme.colors.primary)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        titleIcon?.let { imageVector ->
                            Icon(
                                imageVector = imageVector,
                                contentDescription = "Category Icon",
                                tint = MaterialTheme.colors.onPrimary,
                                modifier = Modifier
                                    .padding(top = 12.dp, bottom = 12.dp, start = 10.dp)
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(color = MaterialTheme.colors.primary)
                                    .padding(5.dp)
                            )
                        }
                        Text(
                            modifier = Modifier.padding(
                                top = 18.dp,
                                bottom = 18.dp,
                                start = titlePadding.value
                            ),
                            text = title,
                            style = MaterialTheme.typography.subtitle1,
                            color = MaterialTheme.colors.onPrimary
                        )
                    }
                    Box(Modifier.padding(12.dp)) {
                        currentContent?.invoke()
                    }
                    Row(
                        Modifier
                            .background(MaterialTheme.colors.primary)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier
                                .clickable(indication = rememberRipple(color = MaterialTheme.colors.onPrimary),
                                    interactionSource = remember { MutableInteractionSource() }) {
                                    onOk?.invoke()
                                    isExpanded.value = false
                                }
                                .fillMaxWidth()
                                .padding(12.dp),
                            text = "OK",
                            style = MaterialTheme.typography.subtitle1,
                            color = MaterialTheme.colors.onPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterItem(
    text: String,
    icon: ImageVector? = null,
    value: String? = null,
    isDisabled: Boolean = false,
    isChecked: Boolean,
    onChecked: (isChecked: Boolean) -> Unit,
    foregroundColor: Color,
    backgroundColor: Color,
    onClicked: (() -> Unit)? = null,
    iconColor: Color = MaterialTheme.colors.primary,
    attachComposable: @Composable() (() -> Unit)? = null
) {

    val disabledAlpha = derivedStateOf { if (isDisabled) .5f else 1f }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = {
                if (!isDisabled)
                    onChecked(it)
            },
            colors = CheckboxDefaults.colors(
                checkedColor = backgroundColor.copy(alpha = disabledAlpha.value),
                uncheckedColor = foregroundColor.copy(alpha = disabledAlpha.value),
                checkmarkColor = MaterialTheme.colors.primary.copy(alpha = disabledAlpha.value)
            ),
            modifier = Modifier
                .padding(
                    top = 8.dp,
                    bottom = 8.dp
                ),
        )
        Row(
            Modifier
                .let {
                    return@let if (!isDisabled && onClicked != null) {
                        it.clickable(
                            indication = rememberRipple(color = foregroundColor),
                            interactionSource = remember { MutableInteractionSource() },
                        ) {
                            onClicked.invoke()
                        }
                    } else it
                }
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = text,
                style = MaterialTheme.typography.subtitle1,
                color = foregroundColor.copy(alpha = disabledAlpha.value),
                textAlign = TextAlign.Center
            )

            Box {
                value?.let {

                    Text(
                        modifier = Modifier
                            .padding(top = 8.dp, bottom = 8.dp, end = 10.dp)
                            .clip(RoundedCornerShape(100.dp))
                            .background(MaterialTheme.colors.onSurface.copy(alpha = .1f))
                            .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
                        text = value,
                        style = MaterialTheme.typography.subtitle2,
                        color = MaterialTheme.colors.onSurface.copy(alpha = disabledAlpha.value),
                        textAlign = TextAlign.Center
                    )
                }

                icon?.let {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Category Icon",
                        tint = MaterialTheme.colors.onPrimary.copy(alpha = disabledAlpha.value),
                        modifier = Modifier
                            .padding(top = 8.dp, bottom = 8.dp, end = 10.dp)
                            .clip(RoundedCornerShape(100.dp))
                            .background(color = iconColor.copy(alpha = disabledAlpha.value))
                            .padding(5.dp)
                    )
                }
                attachComposable?.invoke()
            }
        }

    }
}
