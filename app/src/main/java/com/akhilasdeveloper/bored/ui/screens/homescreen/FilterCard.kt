package com.akhilasdeveloper.bored.ui.screens.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.akhilasdeveloper.bored.data.CategoryColorItem
import com.akhilasdeveloper.bored.data.CategoryData
import com.akhilasdeveloper.bored.ui.screens.CardSecondText
import com.akhilasdeveloper.bored.util.roundToTwoDecimals
import kotlin.math.roundToInt

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
fun FilterCard(categoryColor: CategoryColorItem, viewModel: HomeViewModel) {

    val expanded = remember { mutableStateOf(false) }

    Box(
        Modifier
            .fillMaxSize()
            .padding(20.dp), contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            shape = RoundedCornerShape(100.dp),
            backgroundColor = categoryColor.colorBg
        ) {

            Icon(
                imageVector = Icons.Rounded.FilterAlt,
                contentDescription = "Category Icon",
                tint = categoryColor.colorFg,
                modifier = Modifier
                    .clickable(
                        indication = rememberRipple(color = categoryColor.colorFg),
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            expanded.value = true
                        }
                    )
                    .padding(10.dp)
            )

            FilterContents(
                categoryColor = categoryColor,
                viewModel = viewModel,
                expandedMain = expanded
            )

        }
    }
}

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
fun FilterContents(
    categoryColor: CategoryColorItem,
    viewModel: HomeViewModel,
    expandedMain: MutableState<Boolean>
) {
    ContentDialog(
        isExpanded = expandedMain,
        categoryColor = categoryColor,
        title = "Filter",
        titleIcon = Icons.Rounded.FilterAlt,
        onOk = {
               viewModel.getClearAndGetActivity()
        },
        content = {
            MainFilterContents(categoryColor = categoryColor, viewModel = viewModel)
        }
    )

}

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
fun MainFilterContents(categoryColor: CategoryColorItem, viewModel: HomeViewModel) {

    val stateRandomIsChecked = viewModel.stateRandomIsChecked.collectAsState(false)

    val typeExpanded = remember { mutableStateOf(false) }
    val participantsExpanded = remember { mutableStateOf(false) }
    val priceRangeExpanded = remember { mutableStateOf(false) }
    val accessibilityRangeExpanded = remember { mutableStateOf(false) }

    Column(Modifier.background(categoryColor.colorSecondFg)) {

        val categoryMainData =
            viewModel.stateTypeValue.collectAsState(initial = CategoryData.Invalid)
        val categoryMainTheme =
            derivedStateOf { if (viewModel.isLightTheme) categoryMainData.value.categoryColor.colorLight else categoryMainData.value.categoryColor.colorDark }

        FilterItem(
            categoryColor = categoryColor,
            text = "Random",
            isChecked = stateRandomIsChecked.value,
            onChecked = {
                viewModel.setRandomIsChecked(it)
            }
        )

        ExposedDropDown(expanded = typeExpanded, categoryColor = categoryColor, headerItem = {
            FilterItem(
                categoryColor = categoryColor,
                text = "Type",
                isDisabled = stateRandomIsChecked.value,
                icon = categoryMainData.value.icon,
                iconColor = categoryMainTheme.value.colorBg,
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
                    derivedStateOf { if (viewModel.isLightTheme) category.categoryColor.colorLight else category.categoryColor.colorDark }

                contentItem(onClick = {
                    viewModel.setTypeValue(category.key)
                }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = category.icon,
                            contentDescription = "Category Icon",
                            tint = categoryColor.colorFg,
                            modifier = Modifier
                                .padding(
                                    top = 8.dp,
                                    bottom = 8.dp,
                                    start = 8.dp
                                )
                                .clip(RoundedCornerShape(100.dp))
                                .background(color = categoryTheme.value.colorBg)
                                .padding(5.dp)
                        )

                        CardSecondText(
                            modifier = Modifier
                                .padding(8.dp),
                            text = category.title,
                            textColor = categoryTheme.value.colorSecondBg,
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }
        })

        ExposedDropDown(
            expanded = participantsExpanded,
            categoryColor = categoryColor,
            headerItem = {
                FilterItem(
                    categoryColor = categoryColor,
                    text = "Participants",
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

                        CardSecondText(
                            modifier = Modifier
                                .padding(8.dp),
                            text = "$it",
                            textColor = categoryColor.colorSecondBg,
                            textAlign = TextAlign.Start
                        )

                    }
                }
            })

        val priceRangeStart =
            viewModel.statePriceRangeStartValue.collectAsState(initial = 0f)
        val priceRangeEnd =
            viewModel.statePriceRangeEndValue.collectAsState(initial = 1f)
        val priceRange =
            derivedStateOf { priceRangeStart.value..priceRangeEnd.value }

        FilterItem(
            categoryColor = categoryColor,
            text = "Price Range",
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
            categoryColor = categoryColor,
            initValue = priceRange,
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
            categoryColor = categoryColor,
            text = "Accessibility Range",
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
            categoryColor = categoryColor,
            initValue = accessibilityRange,
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
    categoryColor: CategoryColorItem,
    initValue: State<ClosedFloatingPointRange<Float>>,
    onValueChange: (rangeSliderValue: ClosedFloatingPointRange<Float>) -> Unit,
    onOk: (rangeSliderValue: ClosedFloatingPointRange<Float>) -> Unit
) {

    val rangeSlider = remember {
        mutableStateOf(initValue.value.start..initValue.value.endInclusive)
    }

    ContentDialog(
        categoryColor = categoryColor,
        title = title,
        isExpanded = rangeExpanded,
        widthFactor = .7f, onOk = {
            onOk(rangeSlider.value)
        },
        content = {
            Column(Modifier.padding(12.dp)) {
                CardSecondText(
                    modifier = Modifier
                        .padding(8.dp),
                    text = "From ${(rangeSlider.value.start * 100).roundToInt()} To ${(rangeSlider.value.endInclusive * 100).roundToInt()}%",
                    textColor = categoryColor.colorSecondBg,
                    textAlign = TextAlign.Start
                )
                RangeSlider(
                    steps = 100,
                    values = rangeSlider.value,
                    onValueChange = {
                        onValueChange(it)
                        rangeSlider.value =
                            it.start.roundToTwoDecimals()..it.endInclusive.roundToTwoDecimals()
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = categoryColor.colorBg,
                        activeTrackColor = categoryColor.colorBg,
                        inactiveTrackColor = categoryColor.colorBg.copy(alpha = .5f),
                        inactiveTickColor = categoryColor.colorBg.copy(alpha = TickAlpha),
                    )
                )
            }
        }
    )

}

@ExperimentalMaterialApi
@Composable
fun ExposedDropDown(
    expanded: MutableState<Boolean>,
    categoryColor: CategoryColorItem,
    headerItem: @Composable () -> Unit,
    dropDownItem: @Composable (
        dropDownMenuItem: @Composable (
            onClick: () -> Unit,
            content: @Composable()
            (RowScope.() -> Unit)
        ) -> Unit
    ) -> Unit
) {

    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = {
        }
    ) {

        headerItem.invoke()

        ExposedDropdownMenu(
            modifier = Modifier.background(categoryColor.colorSecondFg),
            expanded = expanded.value,
            onDismissRequest = {
                expanded.value = false
            }
        ) {
            dropDownItem { onClick, content ->
                DropdownMenuItem(
                    modifier = Modifier.background(categoryColor.colorSecondFg),
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
    categoryColor: CategoryColorItem,
    title: String,
    titleIcon: ImageVector? = null,
    widthFactor: Float = .8f,
    onDismiss: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    onOk: (() -> Unit)? = null,
    content: @Composable() (() -> Unit)? = null,
    isExpanded: MutableState<Boolean>
) {

    val currentContent by rememberUpdatedState(content)
    val titlePadding = derivedStateOf { if (titleIcon == null) 18.dp else 10.dp }

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
                color = categoryColor.colorSecondFg
            ) {
                Column {
                    Row(
                        Modifier
                            .background(categoryColor.colorBg)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        titleIcon?.let { imageVector ->
                            Icon(
                                imageVector = imageVector,
                                contentDescription = "Category Icon",
                                tint = categoryColor.colorFg,
                                modifier = Modifier
                                    .padding(top = 12.dp, bottom = 12.dp, start = 10.dp)
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(color = categoryColor.colorBg)
                                    .padding(5.dp)
                            )
                        }
                        CardSecondText(
                            text = title,
                            textColor = categoryColor.colorFg,
                            modifier = Modifier.padding(
                                top = 18.dp,
                                bottom = 18.dp,
                                start = titlePadding.value
                            ),
                            fontSize = 18.sp
                        )
                    }
                    Box(Modifier.padding(12.dp)) {
                        currentContent?.invoke()
                    }
                    Row(
                        Modifier
                            .background(categoryColor.colorBg)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        onCancel?.let {
                            CardSecondText(
                                text = "Cancel",
                                textColor = categoryColor.colorFg,
                                modifier = Modifier
                                    .clickable(indication = rememberRipple(color = categoryColor.colorFg),
                                        interactionSource = remember { MutableInteractionSource() }) {
                                        onCancel.invoke()
                                        isExpanded.value = false
                                    }
                                    .fillMaxWidth(.5f)
                                    .padding(12.dp),
                                fontSize = 18.sp
                            )
                        }
                        CardSecondText(
                            text = "OK",
                            textColor = categoryColor.colorFg,
                            modifier = Modifier
                                .clickable(indication = rememberRipple(color = categoryColor.colorFg),
                                    interactionSource = remember { MutableInteractionSource() }) {
                                    onOk?.invoke()
                                    isExpanded.value = false
                                }
                                .fillMaxWidth()
                                .padding(12.dp),
                            fontSize = 18.sp
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun FilterItem(
    categoryColor: CategoryColorItem,
    text: String,
    icon: ImageVector? = null,
    value: String? = null,
    isDisabled: Boolean = false,
    isChecked: Boolean,
    onChecked: (isChecked: Boolean) -> Unit,
    onClicked: (() -> Unit)? = null,
    iconColor: Color = categoryColor.colorBg,
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
                checkedColor = categoryColor.colorSecondFg.copy(alpha = disabledAlpha.value),
                uncheckedColor = categoryColor.colorSecondBg.copy(alpha = disabledAlpha.value),
                checkmarkColor = categoryColor.colorBg.copy(alpha = disabledAlpha.value)
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
                            indication = rememberRipple(color = categoryColor.colorBg),
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

            CardSecondText(
                text = text,
                textColor = categoryColor.colorSecondBg.copy(alpha = disabledAlpha.value)
            )
            Box {
                value?.let {
                    CardSecondText(
                        modifier = Modifier
                            .padding(top = 8.dp, bottom = 8.dp, end = 10.dp)
                            .clip(RoundedCornerShape(100.dp))
                            .background(categoryColor.colorBg.copy(alpha = disabledAlpha.value))
                            .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
                        text = value,
                        textColor = categoryColor.colorFg.copy(alpha = disabledAlpha.value)
                    )
                }

                icon?.let {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Category Icon",
                        tint = categoryColor.colorFg.copy(alpha = disabledAlpha.value),
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
