package com.akhilasdeveloper.bored.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.akhilasdeveloper.bored.data.CardDao
import com.akhilasdeveloper.bored.data.CategoryColorItem
import com.akhilasdeveloper.bored.data.CategoryData
import com.akhilasdeveloper.bored.data.mapper.CategoryMapper
import com.akhilasdeveloper.bored.ui.screens.viewmodels.MainViewModel
import com.akhilasdeveloper.bored.ui.theme.*
import com.akhilasdeveloper.bored.util.Constants
import kotlinx.coroutines.delay
import kotlin.math.roundToInt


@Composable
fun HomeScreen(viewModel: MainViewModel = viewModel()) {

    val cardStates = viewModel.cardStates
    val cards = viewModel.cards
    val categoryColor = viewModel.categoryColor.value

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = viewModel.transparentValue()),
            verticalAlignment = Alignment.CenterVertically
        ) {

            SelectionButton(
                text = "Skip",
                isSelected = viewModel.passSelected.value,
                modifier = Modifier.weight(1f),
                accent = categoryColor.colorBg,
                accentFg = viewModel.systemBarColorFg.value,
                transparentValue = viewModel.transparentValue()
            ) {
                viewModel.passSelected()
            }
            SelectionButton(
                text = "TODO",
                isSelected = viewModel.addSelected.value,
                modifier = Modifier.weight(1f),
                accent = categoryColor.colorBg,
                accentFg = viewModel.systemBarColorFg.value,
                transparentValue = viewModel.transparentValue()
            ) {
                viewModel.addSelected()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {

            cards.forEachIndexed { index, card ->

                CardView(
                    cardDao = card,
                    cardState = cardStates[index],
                    onRemoveCompleted = {
                        viewModel.removeCompleted(card)
                    },
                    onSelected = {
                        viewModel.onSelected(index, it)
                    },
                    onLoadCompleted = {
                        viewModel.setCardLoadingCompletedState(true)
                    },
                    onDragSelect = {
                        viewModel.setDragSelectState(it)
                    },
                    categoryColor = categoryColor
                )
            }

            LoadingProgress(
                color = categoryColor.colorBg,
                visibility = viewModel.loadingState.value
            )
        }

        FilterCard(categoryColor = categoryColor)

    }
}

@Composable
fun FilterCard(categoryColor: CategoryColorItem) {

    val isVisible = remember { mutableStateOf(false) }
    val cornerRadius = remember { mutableStateOf(100.dp) }
    val animCornerRadius = animateDpAsState(targetValue = cornerRadius.value)

    val mainElevation = remember { mutableStateOf(0.dp) }
    val animMainElevation = animateDpAsState(targetValue = mainElevation.value)

    val mainIconPadding = remember { mutableStateOf(10.dp) }
    val animMainIconPadding = animateDpAsState(targetValue = mainIconPadding.value)

    val backgroundDimFactor = remember { mutableStateOf(0f) }
    val animBackgroundDimFactor = animateFloatAsState(targetValue = backgroundDimFactor.value)


    Box(
        modifier = Modifier
            .background(Color.Black.copy(alpha = animBackgroundDimFactor.value))
            .fillMaxSize()
            .padding(20.dp).let {
                return@let if (isVisible.value) {
                    it.clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ) {
                        cornerRadius.value = 100.dp
                        mainElevation.value = 0.dp
                        mainIconPadding.value = 10.dp
                        backgroundDimFactor.value = 0f
                        isVisible.value = !isVisible.value
                    }
                } else it
            },
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            shape = RoundedCornerShape(animCornerRadius.value),
            backgroundColor = categoryColor.colorBg,
            elevation = animMainElevation.value
        ) {
            Column {

                Icon(
                    imageVector = Icons.Rounded.FilterAlt,
                    contentDescription = "Category Icon",
                    tint = categoryColor.colorFg,
                    modifier = Modifier
                        .clickable(
                            indication = rememberRipple(color = categoryColor.colorFg),
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = {
                                cornerRadius.value = if (isVisible.value) {
                                    100.dp
                                } else {
                                    10.dp
                                }
                                mainElevation.value = if (isVisible.value) {
                                    0.dp
                                } else {
                                    100.dp
                                }
                                mainIconPadding.value = if (isVisible.value) {
                                    10.dp
                                } else {
                                    20.dp
                                }

                                backgroundDimFactor.value = if (isVisible.value) {
                                    0f
                                } else {
                                    .5f
                                }
                                isVisible.value = !isVisible.value
                            }
                        )
                        .padding(animMainIconPadding.value)
                )

                AnimatedVisibility(
                    visible = isVisible.value,
                    enter = expandIn(animationSpec = tween(durationMillis = 500)),
                    exit = shrinkOut(animationSpec = tween(durationMillis = 500)),
                    modifier = Modifier.background(categoryColor.colorSecondFg)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = true,
                                onCheckedChange = {
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
                            CardSecondText(
                                text = "Random",
                                textColor = categoryColor.colorSecondBg
                            )

                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = true,
                                    onCheckedChange = {
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
                                CardSecondText(
                                    text = "Type",
                                    textColor = categoryColor.colorSecondBg
                                )
                            }

                            Icon(
                                imageVector = CategoryData.Education.icon,
                                contentDescription = "Category Icon",
                                tint = categoryColor.colorFg,
                                modifier = Modifier
                                    .padding(top = 8.dp, bottom = 8.dp, end = 10.dp)
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(color = categoryColor.colorBg)
                                    .padding(5.dp)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = true,
                                    onCheckedChange = {
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
                                CardSecondText(
                                    text = "Participants",
                                    textColor = categoryColor.colorSecondBg
                                )
                            }

                            CardSecondText(
                                modifier = Modifier
                                    .padding(top = 8.dp, bottom = 8.dp, end = 10.dp)
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(categoryColor.colorBg)
                                    .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
                                text = "3",
                                textColor = categoryColor.colorFg
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = true,
                                    onCheckedChange = {
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
                                CardSecondText(
                                    text = "Price Range",
                                    textColor = categoryColor.colorSecondBg
                                )
                            }

                            CardSecondText(
                                modifier = Modifier
                                    .padding(top = 8.dp, bottom = 8.dp, end = 10.dp)
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(categoryColor.colorBg)
                                    .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
                                text = "50%",
                                textColor = categoryColor.colorFg
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = true,
                                    onCheckedChange = {
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
                                CardSecondText(
                                    text = "Accessibility Range",
                                    textColor = categoryColor.colorSecondBg
                                )
                            }

                            CardSecondText(
                                modifier = Modifier
                                    .padding(top = 8.dp, bottom = 8.dp, end = 10.dp)
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(categoryColor.colorBg)
                                    .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
                                text = "20%",
                                textColor = categoryColor.colorFg
                            )

                        }

                    }
                }
            }
        }
    }
}

@Composable
fun SelectionButton(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean = false,
    transparentValue: Color,
    accent: Color,
    accentFg: Color,
    onClicked: () -> Unit
) {
    var fontSize by remember { mutableStateOf(selectionDeselectedFontSize) }
    var selectionColor by remember { mutableStateOf(transparentValue) }
    val animFontSize by animateIntAsState(targetValue = fontSize)
    val animSelectionColor by animateColorAsState(targetValue = selectionColor)

    SideEffect {
        fontSize = if (isSelected) selectionSelectedFontSize else selectionDeselectedFontSize
        selectionColor =
            if (isSelected) accent else transparentValue
    }

    Box(
        modifier
            .fillMaxHeight()
            .background(
                animSelectionColor.copy(alpha = .3f)
            )
            .clickable(indication = rememberRipple(color = accent),
                interactionSource = remember { MutableInteractionSource() }) {
                onClicked()
            },
        contentAlignment = Alignment.BottomCenter
    )
    {
        CardSecondText(
            text = text,
            textColor = accentFg,
            fontSize = animFontSize.sp,
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Composable
fun LoadingProgress(visibility: Boolean, color: Color) {
    AnimatedVisibility(
        visible = visibility,
        enter = fadeIn(animationSpec = tween(durationMillis = 500)),
        exit = fadeOut(animationSpec = tween(durationMillis = 500))
    ) {
        CircularProgressIndicator(color = color)
    }
}

@Composable
fun CardView(
    cardDao: CardDao,
    modifier: Modifier = Modifier,
    cardState: State<Int> = mutableStateOf(Constants.IDLE_SELECTION),
    onSelected: ((selection: Int) -> Unit)? = null,
    onDragSelect: ((selection: Int) -> Unit)? = null,
    onRemoveCompleted: (() -> Unit)? = null,
    onLoadCompleted: (() -> Unit)? = null,
    enableInteraction: Boolean = true,
    categoryColor: CategoryColorItem = CategoryColorItem()
) {

    val categoryData by derivedStateOf { CategoryMapper().toSourceFromDestination(cardDao.type) }

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var scale by remember { mutableStateOf(1f) }

    var isAnimate by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }

    val offsetXAnim by animateFloatAsState(targetValue = offsetX)
    val offsetYAnim by animateFloatAsState(targetValue = offsetY)
    val scaleAnim by animateFloatAsState(targetValue = scale, finishedListener = {
        if (it == 0f) {
            onRemoveCompleted?.invoke()
            onDragSelect?.invoke(Constants.IDLE_SELECTION)
        }
    })

    var moreIsVisible by remember { mutableStateOf(false) }
    val cardIsVisible = if (enableInteraction) remember { MutableTransitionState(false) }
        .apply { targetState = true } else remember { MutableTransitionState(true) }

    LaunchedEffect(key1 = true) {
        delay(400)
        onLoadCompleted?.invoke()
    }

    cardState.value.let {

        if (it != Constants.IDLE_SELECTION) {
            val configuration = LocalConfiguration.current
            val screenDensity = configuration.densityDpi / 160f
            val screenHeightPx = configuration.screenHeightDp.toFloat() * screenDensity
            val screenWidthPx = configuration.screenWidthDp.toFloat() * screenDensity

            isAnimate = true

            val x = when (it) {
                Constants.PASS_SELECTION -> {
                    -screenWidthPx / 4
                }
                Constants.ADD_SELECTION -> {
                    screenWidthPx / 4
                }
                else -> {
                    screenWidthPx / 2
                }
            }

            val y = screenHeightPx / 2

            offsetX = x
            offsetY = y

            scale = 0f
        }

    }

    AnimatedVisibility(
        visibleState = cardIsVisible,
        enter = expandVertically(animationSpec = tween(durationMillis = 500)),
        exit = shrinkVertically(animationSpec = tween(durationMillis = 500))
    ) {
        Card(
            shape = RoundedCornerShape(10.dp),
            backgroundColor = categoryColor.colorBg,
            modifier = modifier
                .offset {
                    if (isAnimate) IntOffset(
                        offsetXAnim.roundToInt(),
                        offsetYAnim.roundToInt()
                    ) else
                        IntOffset(offsetX.roundToInt(), offsetY.roundToInt())
                }
                .scale(scaleAnim)
                .pointerInput(Unit) {
                    if (enableInteraction) {
                        detectDragGestures(onDragEnd = {

                            isAnimate = true

                            if (offsetX >= 200) {
                                onSelected?.invoke(Constants.ADD_SELECTION)
                                isFinished = true
                            }

                            if (offsetX <= -200) {
                                onSelected?.invoke(Constants.PASS_SELECTION)
                                isFinished = true
                            }

                            if (!isFinished) {
                                offsetX = 0f
                                offsetY = 0f
                            }

                        }) { change, dragAmount ->

                            change.consumeAllChanges()

                            isAnimate = false

                            if (!isFinished) {
                                offsetX += dragAmount.x * scale
                                if (offsetY + (dragAmount.y * scale) >= 0)
                                    offsetY += dragAmount.y * scale


                                if (offsetX >= 200 || offsetX <= -200) {
                                    scale = .3f

                                    if (offsetX >= 200)
                                        onDragSelect?.invoke(Constants.ADD_SELECTION)

                                    if (offsetX <= -200)
                                        onDragSelect?.invoke(Constants.PASS_SELECTION)

                                } else {
                                    scale = 1f

                                    onDragSelect?.invoke(Constants.IDLE_SELECTION)
                                }
                            }
                        }
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        indication = rememberRipple(color = categoryColor.colorFg),
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            moreIsVisible = !moreIsVisible
                        }
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = categoryData.icon,
                        contentDescription = "Category Icon",
                        tint = categoryColor.colorBg,
                        modifier = Modifier
                            .padding(end = 20.dp)
                            .clip(RoundedCornerShape(100.dp))
                            .background(color = categoryColor.colorFg)
                            .padding(5.dp)
                    )
                    cardDao.activityName?.let {
                        CardPrimaryText(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            activityName = it,
                            textColor = categoryColor.colorFg
                        )
                    }
                }
                AnimatedVisibility(
                    visible = moreIsVisible,
                    enter = expandVertically(animationSpec = tween(durationMillis = 500)),
                    exit = shrinkVertically(animationSpec = tween(durationMillis = 500)),
                    modifier = Modifier.background(categoryColor.colorSecondBg)
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    MoreContent(
                        cardDao = cardDao,
                        categoryColor = categoryColor
                    )
                }

            }

        }
    }
}


@Composable
fun CardPrimaryText(modifier: Modifier, activityName: String, textColor: Color) {
    Text(
        modifier = modifier,
        text = activityName,
        style = TextStyle(
            color = textColor,
            fontWeight = FontWeight.ExtraBold,
            fontSize = cardFontSize.sp
        ),
        textAlign = TextAlign.Center
    )
}

@Composable
fun CardSecondText(
    modifier: Modifier = Modifier,
    text: String?,
    textColor: Color,
    fontSize: TextUnit = secondaryFontSize.sp,
    textAlign: TextAlign = TextAlign.Center,
    textDecoration: TextDecoration = TextDecoration.None
) {
    AnimatedVisibility(
        visible = !text.isNullOrEmpty(),
        enter = expandVertically(animationSpec = tween(durationMillis = 500)),
        exit = shrinkVertically(animationSpec = tween(durationMillis = 500))
    ) {
        text?.let {
            Text(
                modifier = modifier,
                text = text,
                style = TextStyle(
                    color = textColor,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = fontSize,
                    textDecoration = textDecoration
                ),
                textAlign = textAlign,
            )
        }
    }
}

@Composable
fun MoreContent(
    cardDao: CardDao,
    categoryColor: CategoryColorItem
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        CardSecondText(
            modifier = Modifier.padding(12.dp),
            text = cardDao.link,
            textColor = categoryColor.colorSecondFg
        )
        CardSecondText(
            modifier = Modifier.padding(12.dp),
            text = "Type : ${cardDao.type}",
            textColor = categoryColor.colorSecondFg
        )
        CardSecondText(
            modifier = Modifier.padding(12.dp),
            text = "Participants : ${cardDao.participants}",
            textColor = categoryColor.colorSecondFg
        )
        Spacer(modifier = Modifier.padding(10.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(10.dp)
            ) {
                LinearProgressBar(
                    percentage = cardDao.accessibility,
                    color = categoryColor.colorBg,
                    text = "Accessibility",
                    fontColor = categoryColor.colorSecondFg
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(10.dp)
            ) {
                LinearProgressBar(
                    percentage = cardDao.price,
                    color = categoryColor.colorBg,
                    text = "Price",
                    fontColor = categoryColor.colorSecondFg
                )
            }
        }
    }
}

@Composable
fun LinearProgressBar(
    percentage: Float?,
    fontSize: TextUnit = secondaryFontSize.sp,
    color: Color,
    colorSecond: Color = colorProgressSecond,
    fontColor: Color = Color.White,
    strokeWidth: Dp = 8.dp,
    size: Int = 80,
    animationDuration: Int = 1000,
    animationDelay: Int = 0,
    text: String
) {
    var animationPlayed by remember { mutableStateOf(false) }
    var sizeAct by remember { mutableStateOf(size) }

    val curPercentage = animateFloatAsState(
        targetValue = if (animationPlayed) percentage ?: 0f else 0f,
        animationSpec = tween(
            durationMillis = animationDuration,
            delayMillis = animationDelay
        )
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Column(modifier = Modifier.onGloballyPositioned {
        sizeAct = it.size.width
    }, horizontalAlignment = Alignment.CenterHorizontally) {

        Box(contentAlignment = Alignment.Center) {
            Canvas(
                modifier = Modifier
                    .size(width = sizeAct.dp, height = strokeWidth)
            ) {
                drawLine(
                    color = colorSecond,
                    strokeWidth = strokeWidth.toPx(),
                    cap = StrokeCap.Round,
                    start = Offset(0f, 0f),
                    end = Offset(sizeAct.toFloat(), 0f)
                )
                drawLine(
                    color = color,
                    strokeWidth = strokeWidth.toPx(),
                    cap = if (curPercentage.value != 0f) StrokeCap.Round else Stroke.DefaultCap,
                    start = Offset(0f, 0f),
                    end = Offset(curPercentage.value * sizeAct, 0f)
                )
            }
        }

        CardSecondText(
            modifier = Modifier.padding(8.dp),
            text = "$text ${(curPercentage.value * 100).roundToInt()} %",
            textColor = fontColor,
            fontSize = fontSize
        )
    }

}