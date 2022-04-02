package com.akhilasdeveloper.bored.ui.screens.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
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
    if (expandedMain.value) {
        ContentDialog(
            onDismiss = {
                expandedMain.value = false
            },
            categoryColor = categoryColor,
            title = "Filter",
            titleIcon = Icons.Rounded.FilterAlt,
            onOk = {
                expandedMain.value = false
            }, onCancel = {
                expandedMain.value = false
            },
            content = {
                MainFilterContents(categoryColor = categoryColor, viewModel = viewModel)
            }
        )
    }

}

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
fun MainFilterContents(categoryColor: CategoryColorItem, viewModel: HomeViewModel) {

    var typeExpanded by remember { mutableStateOf(false) }
    var participantsExpanded by remember { mutableStateOf(false) }
    var priceRangeExpanded by remember { mutableStateOf(false) }
    var accessibilityRangeExpanded by remember { mutableStateOf(false) }

    Column(Modifier.background(categoryColor.colorSecondFg)) {

        val categoryMainData =
            viewModel.stateTypeValue.collectAsState(initial = CategoryData.Invalid)
        val categoryMainTheme =
            derivedStateOf { if (viewModel.isLightTheme) categoryMainData.value.categoryColor.colorLight else categoryMainData.value.categoryColor.colorDark }

        FilterItem(
            categoryColor = categoryColor,
            text = "Random",
            isChecked = viewModel.stateRandomIsChecked.collectAsState(false).value,
            onChecked = {
                viewModel.setRandomIsChecked(it)
            },
            iconColor = categoryMainTheme.value.colorBg
        )

        ExposedDropdownMenuBox(
            expanded = typeExpanded,
            onExpandedChange = {
            }
        ) {

            FilterItem(
                categoryColor = categoryColor,
                text = "Type",
                icon = categoryMainData.value.icon,
                iconColor = categoryMainTheme.value.colorBg,
                isChecked = viewModel.stateTypeIsChecked.collectAsState(false).value,
                onChecked = {
                    viewModel.setTypeIsChecked(it)
                }, onClicked = {
                    typeExpanded = !typeExpanded
                }
            )
            ExposedDropdownMenu(
                modifier = Modifier.background(categoryColor.colorSecondFg),
                expanded = typeExpanded,
                onDismissRequest = {
                    typeExpanded = false
                }
            ) {
                viewModel.types.value.forEach { category ->

                    val categoryTheme =
                        derivedStateOf { if (viewModel.isLightTheme) category.categoryColor.colorLight else category.categoryColor.colorDark }

                    DropdownMenuItem(
                        modifier = Modifier.background(categoryTheme.value.colorSecondFg),
                        onClick = {
                            viewModel.setTypeValue(category.key)
                            typeExpanded = false
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
            }

        }


        ExposedDropdownMenuBox(
            expanded = participantsExpanded,
            onExpandedChange = {
            }
        ) {
            FilterItem(
                categoryColor = categoryColor,
                text = "Participants",
                value = "${viewModel.stateParticipantsValue.collectAsState(initial = 0).value}",
                isChecked = viewModel.stateParticipantsIsChecked.collectAsState(false).value,
                onChecked = {
                    viewModel.setParticipantsIsChecked(it)
                },
                onClicked = {
                    participantsExpanded = !participantsExpanded
                }
            )

            ExposedDropdownMenu(
                modifier = Modifier.background(categoryColor.colorSecondFg),
                expanded = participantsExpanded,
                onDismissRequest = {
                    participantsExpanded = false
                }
            ) {

                (1..5).forEach {
                    DropdownMenuItem(
                        modifier = Modifier.background(categoryColor.colorSecondFg),
                        onClick = {
                            viewModel.setParticipantsValue(it)
                            participantsExpanded = false
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
            }
        }

        val priceRangeStart =
            viewModel.statePriceRangeStartValue.collectAsState(initial = 0f)
        val priceRangeEnd =
            viewModel.statePriceRangeEndValue.collectAsState(initial = 1f)
        val priceRange =
            derivedStateOf { priceRangeStart.value..priceRangeEnd.value }
        val priceRangeSlider = remember {
            mutableStateOf(priceRange.value.start..priceRange.value.endInclusive)
        }

        FilterItem(
            categoryColor = categoryColor,
            text = "Price Range",
            value = "${(priceRange.value.start * 100).roundToInt()}% - ${(priceRange.value.endInclusive * 100).roundToInt()}%",
            isChecked = viewModel.statePriceRangeIsChecked.collectAsState(false).value,
            onChecked = {
                viewModel.setPriceRangeIsChecked(it)
            },
            onClicked = {
                priceRangeExpanded = !priceRangeExpanded
            }
        )

        if (priceRangeExpanded) {

            ContentDialog(
                onDismiss = {
                    priceRangeExpanded = false
                },
                title = "Price Range",
                categoryColor = categoryColor,
                onOk = {
                    viewModel.setPriceRangeStartValue(
                        priceRangeSlider.value.start.roundToTwoDecimals()
                    )
                    viewModel.setPriceRangeEndValue(priceRangeSlider.value.endInclusive.roundToTwoDecimals())
                    priceRangeExpanded = false
                }, onCancel = {
                    priceRangeExpanded = false
                },
                content = {
                    Column {
                        CardSecondText(
                            modifier = Modifier
                                .padding(8.dp),
                            text = "From ${(priceRangeSlider.value.start * 100).roundToInt()} To ${(priceRangeSlider.value.endInclusive * 100).roundToInt()}%",
                            textColor = categoryColor.colorSecondBg,
                            textAlign = TextAlign.Start
                        )
                        RangeSlider(
                            steps = 100,
                            values = priceRangeSlider.value,
                            colors = SliderDefaults.colors(
                                thumbColor = categoryColor.colorBg,

                                ),
                            onValueChange = {
                                priceRangeSlider.value =
                                    it.start.roundToTwoDecimals()..it.endInclusive.roundToTwoDecimals()
                            })
                    }
                }
            )
        }

        val accessibilityRangeStart =
            viewModel.stateAccessibilityRangeStartValue.collectAsState(initial = 0f)
        val accessibilityRangeEnd =
            viewModel.stateAccessibilityRangeEndValue.collectAsState(initial = 1f)
        val accessibilityRange =
            derivedStateOf { accessibilityRangeStart.value..accessibilityRangeEnd.value }
        val accessibilityRangeSlider = remember {
            mutableStateOf(accessibilityRange.value.start..accessibilityRange.value.endInclusive)
        }

        FilterItem(
            categoryColor = categoryColor,
            text = "Accessibility Range",
            value = "${(accessibilityRange.value.start * 100).roundToInt()}% - ${(accessibilityRange.value.endInclusive * 100).roundToInt()}%",
            isChecked = viewModel.stateAccessibilityRangeIsChecked.collectAsState(false).value,
            onChecked = {
                viewModel.setAccessibilityRangeIsChecked(it)
            }, onClicked = {
                accessibilityRangeExpanded = !accessibilityRangeExpanded
            }
        )

        if (accessibilityRangeExpanded) {

            ContentDialog(
                onDismiss = {
                    accessibilityRangeExpanded = false
                },
                title = "Accessibility Range",
                categoryColor = categoryColor,
                onOk = {
                    viewModel.setAccessibilityRangeStartValue(
                        accessibilityRangeSlider.value.start.roundToTwoDecimals()
                    )
                    viewModel.setAccessibilityRangeEndValue(
                        accessibilityRangeSlider.value.endInclusive.roundToTwoDecimals()
                    )
                    accessibilityRangeExpanded = false
                }, onCancel = {
                    accessibilityRangeExpanded = false
                },
                content = {
                    Column {
                        CardSecondText(
                            modifier = Modifier
                                .padding(8.dp),
                            text = "From ${(accessibilityRangeSlider.value.start * 100).roundToInt()} To ${(accessibilityRangeSlider.value.endInclusive * 100).roundToInt()}%",
                            textColor = categoryColor.colorSecondBg,
                            textAlign = TextAlign.Start
                        )
                        RangeSlider(
                            steps = 100,
                            values = accessibilityRangeSlider.value,
                            onValueChange = {
                                accessibilityRangeSlider.value =
                                    it.start.roundToTwoDecimals()..it.endInclusive.roundToTwoDecimals()
                            })
                    }
                }
            )
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
    onDismiss: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    onOk: (() -> Unit)? = null,
    content: (@Composable () -> Unit)? = null
) {
    val currentContent by rememberUpdatedState(content)
    val titlePadding = derivedStateOf { if (titleIcon == null) 18.dp else 10.dp }

    Dialog(
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
        onDismissRequest = {
            onDismiss?.invoke()
        }) {
        Surface(
            modifier = modifier.fillMaxWidth(.8f),
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
                    CardSecondText(
                        text = "Cancel",
                        textColor = categoryColor.colorFg,
                        modifier = Modifier
                            .clickable(indication = rememberRipple(color = categoryColor.colorFg),
                                interactionSource = remember { MutableInteractionSource() }) {
                                onCancel?.invoke()
                            }
                            .fillMaxWidth(.5f)
                            .padding(12.dp),
                        fontSize = 18.sp
                    )
                    CardSecondText(
                        text = "OK",
                        textColor = categoryColor.colorFg,
                        modifier = Modifier
                            .clickable(indication = rememberRipple(color = categoryColor.colorFg),
                                interactionSource = remember { MutableInteractionSource() }) {
                                onOk?.invoke()
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

@Composable
fun FilterItem(
    categoryColor: CategoryColorItem,
    text: String,
    icon: ImageVector? = null,
    value: String? = null,
    isChecked: Boolean,
    onChecked: (isChecked: Boolean) -> Unit,
    onClicked: (() -> Unit)? = null,
    iconColor: Color = categoryColor.colorBg,
    attachComposable: @Composable() (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = {
                onChecked(it)
            },
            colors = CheckboxDefaults.colors(
                checkedColor = categoryColor.colorSecondFg,
                uncheckedColor = categoryColor.colorSecondBg,
                checkmarkColor = categoryColor.colorBg
            ),
            modifier = Modifier
                .padding(
                    top = 8.dp,
                    bottom = 8.dp
                ),
        )
        Row(
            Modifier
                .clickable(
                    indication = rememberRipple(color = categoryColor.colorBg),
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {
                        onClicked?.invoke()
                    }
                )
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            CardSecondText(
                text = text,
                textColor = categoryColor.colorSecondBg
            )
            Box {
                value?.let {
                    CardSecondText(
                        modifier = Modifier
                            .padding(top = 8.dp, bottom = 8.dp, end = 10.dp)
                            .clip(RoundedCornerShape(100.dp))
                            .background(categoryColor.colorBg)
                            .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
                        text = value,
                        textColor = categoryColor.colorFg
                    )
                }

                icon?.let {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Category Icon",
                        tint = categoryColor.colorFg,
                        modifier = Modifier
                            .padding(top = 8.dp, bottom = 8.dp, end = 10.dp)
                            .clip(RoundedCornerShape(100.dp))
                            .background(color = iconColor)
                            .padding(5.dp)
                    )
                }
                attachComposable?.invoke()
            }
        }


    }
}
