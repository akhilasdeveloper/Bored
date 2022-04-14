package com.akhilasdeveloper.bored.ui.screens.home

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
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.akhilasdeveloper.bored.data.CardDao
import com.akhilasdeveloper.bored.data.mapper.CategoryValueMapper
import com.akhilasdeveloper.bored.ui.theme.*
import com.akhilasdeveloper.bored.util.Constants
import com.akhilasdeveloper.bored.util.Constants.CARD_SWIPE_TRIED
import com.akhilasdeveloper.bored.util.Constants.CARD_TAP_TRIED
import com.akhilasdeveloper.bored.util.Constants.TERMS_DEMO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {

    val cardStates = viewModel.cardStates
    val cards = viewModel.cards
    val errorState = viewModel.errorState
    val isCardSwipeTriedIsShowing = remember { mutableStateOf(false) }
    val isCardTapTriedIsShowing = remember { mutableStateOf(false) }
    val isTermsDemoIsShowing = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (errorState.value == null) {

                SelectionButton(
                    text = "Skip",
                    isSelected = viewModel.passSelected.value,
                    modifier = Modifier.weight(1f),
                    transparentValue = MaterialTheme.colors.background.copy(alpha = 0f)
                ) {
                    viewModel.passSelected()
                    if (!viewModel.isCardSwipeTried.value)
                        isCardSwipeTriedIsShowing.value = true
                }
                SelectionButton(
                    text = "Todo",
                    isSelected = viewModel.addSelected.value,
                    modifier = Modifier.weight(1f),
                    transparentValue = MaterialTheme.colors.background.copy(alpha = 0f)
                ) {
                    viewModel.addSelected()
                    if (!viewModel.isCardSwipeTried.value)
                        isCardSwipeTriedIsShowing.value = true
                }
            } else {
                SelectionButton(
                    text = "Tap to Retry",
                    isSelected = viewModel.addSelected.value,
                    modifier = Modifier.weight(1f),
                    transparentValue = MaterialTheme.colors.background.copy(alpha = 0f),
                    alignTextCenter = true
                ) {
                    viewModel.getRandomActivity()
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {

            if (errorState.value == null) {
                cards.forEachIndexed { index, card ->
                    CardView(
                        cardDao = card,
                        cardState = cardStates[index],
                        onRemoveCompleted = {
                            viewModel.removeCompleted(card)
                        },
                        onSelected = {
                            viewModel.setIsCardSwipeTried()
                            viewModel.onSelected(index, it)
                        },
                        onLoadCompleted = {
                            if (!viewModel.isCardTapTriedTried.value)
                                isCardTapTriedIsShowing.value = true
                            viewModel.setCardLoadingCompletedState(true)
                        },
                        onDragSelect = {
                            viewModel.setDragSelectState(it)
                        },
                        onMoreVisible = {
                            viewModel.setIsCardTapTriedTried()
                            if (!viewModel.isTermsDemoIsShowing.value)
                                isTermsDemoIsShowing.value = true
                        }
                    )
                }
            } else {
                Text(
                    text = errorState.value!!,
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.primary,
                    textAlign = TextAlign.Center
                )
            }

            LoadingProgress(
                color = MaterialTheme.colors.primary,
                visibility = viewModel.loadingState.value
            )
        }

        FilterCard(viewModel = viewModel)

    }

    DemoDialog(
        title = "Tip",
        description = CARD_SWIPE_TRIED,
        rangeExpanded = isCardSwipeTriedIsShowing,
        foregroundColor = MaterialTheme.colors.onSurface,
        backgroundColor = MaterialTheme.colors.surface
    ) {
    }
    DemoDialog(
        title = "Tip",
        description = CARD_TAP_TRIED,
        rangeExpanded = isCardTapTriedIsShowing,
        foregroundColor = MaterialTheme.colors.onSurface,
        backgroundColor = MaterialTheme.colors.surface
    ) {

    }

    DemoDialog(
        title = "Terms",
        description = TERMS_DEMO,
        rangeExpanded = isTermsDemoIsShowing,
        foregroundColor = MaterialTheme.colors.onSurface,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        viewModel.setIsTermsDemoIsShowing()
    }

}

@Composable
fun SelectionButton(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean = false,
    transparentValue: Color,
    alignTextCenter: Boolean = false,
    onClicked: () -> Unit
) {
    val fontSizeFrom = MaterialTheme.typography.subtitle1.fontSize.value.toInt()
    val fontSizeTo = MaterialTheme.typography.h6.fontSize.value.toInt()
    var fontSize by remember { mutableStateOf(fontSizeFrom) }
    val animFontSize by animateIntAsState(targetValue = fontSize)
    val animSelectionColor by animateColorAsState(targetValue = if (isSelected) MaterialTheme.colors.primary else transparentValue)

    SideEffect {
        fontSize = if (isSelected) fontSizeTo else fontSizeFrom
    }

    Box(
        modifier
            .fillMaxHeight()
            .background(
                animSelectionColor.copy(alpha = .3f)
            )
            .clickable(indication = rememberRipple(color = MaterialTheme.colors.primary),
                interactionSource = remember { MutableInteractionSource() }) {
                onClicked()
            },
        contentAlignment = Alignment.BottomCenter
    )
    {
        if (!alignTextCenter) {
            Text(
                modifier = Modifier.padding(20.dp),
                text = text,
                style = MaterialTheme.typography.subtitle1.copy(fontSize = animFontSize.sp),
                color = MaterialTheme.colors.onBackground
            )
        } else {
            Box(Modifier.fillMaxSize(.5f), contentAlignment = Alignment.Center) {
                Text(
                    modifier = Modifier.padding(20.dp),
                    text = text,
                    style = MaterialTheme.typography.subtitle1.copy(fontSize = animFontSize.sp),
                    color = MaterialTheme.colors.onBackground
                )
            }
        }
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
    onMoreVisible: (() -> Unit)? = null,
    enableInteraction: Boolean = true
) {

    val categoryData by derivedStateOf { CategoryValueMapper.toSourceFromDestination(cardDao.type) }

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
            backgroundColor = MaterialTheme.colors.surface,
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
            val composableScope = rememberCoroutineScope()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        indication = rememberRipple(color = MaterialTheme.colors.onPrimary),
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            moreIsVisible = !moreIsVisible
                            if (moreIsVisible) {
                                composableScope.launch {
                                    delay(1000)
                                    onMoreVisible?.invoke()
                                }
                            }
                        }
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier
                        .background(MaterialTheme.colors.primary)
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = categoryData.icon,
                        contentDescription = "Category Icon",
                        tint = MaterialTheme.colors.primary,
                        modifier = Modifier
                            .padding(end = 20.dp)
                            .clip(RoundedCornerShape(100.dp))
                            .background(color = MaterialTheme.colors.onPrimary)
                            .padding(5.dp)
                    )
                    cardDao.activityName?.let {
                        Text(
                            text = it,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                            color = MaterialTheme.colors.onPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                AnimatedVisibility(
                    visible = moreIsVisible,
                    enter = expandVertically(animationSpec = tween(durationMillis = 500)),
                    exit = shrinkVertically(animationSpec = tween(durationMillis = 500)),
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    MoreContent(
                        cardDao = cardDao,
                        accent = MaterialTheme.colors.primary
                    )
                }

            }

        }
    }
}

@Composable
fun HideableSubTitleText(
    modifier: Modifier = Modifier,
    text: String?
) {
    AnimatedVisibility(
        visible = !text.isNullOrEmpty(),
        enter = expandVertically(
            animationSpec = tween(durationMillis = 500)
        ),
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = 500)
        )
    ) {
        text?.let {
            Text(
                modifier = modifier,
                text = text,
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CardSecondText(
    modifier: Modifier = Modifier,
    text: String?,
    textColor: Color,
    fontSize: TextUnit = secondaryFontSize.sp,
    textAlign: TextAlign = TextAlign.Center,
    textDecoration: TextDecoration = TextDecoration.None,
    fontWeight: FontWeight = FontWeight.ExtraBold,
    animIsHorizontal: Boolean = false
) {
    AnimatedVisibility(
        visible = !text.isNullOrEmpty(),
        enter = if (animIsHorizontal) expandHorizontally(animationSpec = tween(durationMillis = 500)) else expandVertically(
            animationSpec = tween(durationMillis = 500)
        ),
        exit = if (animIsHorizontal) shrinkHorizontally(animationSpec = tween(durationMillis = 500)) else shrinkVertically(
            animationSpec = tween(durationMillis = 500)
        )
    ) {
        text?.let {
            Text(
                modifier = modifier,
                text = text,
                style = TextStyle(
                    color = textColor,
                    fontWeight = fontWeight,
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
    accent: Color
) {
    val uriHandler = LocalUriHandler.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.Start,
    ) {

        HideableSubTitleText(text = cardDao.link, modifier = Modifier
            .clickable(
                indication = rememberRipple(color = MaterialTheme.colors.onSurface),
                interactionSource = remember { MutableInteractionSource() },
                onClick = {
                    uriHandler.openUri(cardDao.link!!)
                }
            )
            .fillMaxWidth()
            .padding(20.dp))

        Text(
            modifier = Modifier.padding(12.dp),
            text = "Type : ${cardDao.type}",
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onSurface
        )
        Text(
            modifier = Modifier.padding(12.dp),
            text = "Participants : ${cardDao.participants}",
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onSurface
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
                    color = accent,
                    text = "Accessibility",
                    fontColor = MaterialTheme.colors.onSurface
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(10.dp)
            ) {
                LinearProgressBar(
                    percentage = cardDao.price,
                    color = accent,
                    text = "Price",
                    fontColor = MaterialTheme.colors.onSurface
                )
            }
        }
    }
}

@Composable
fun LinearProgressBar(
    percentage: Float?,
    color: Color,
    colorSecond: Color = MaterialTheme.colors.onSurface.copy(alpha = .1f),
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

        Text(
            modifier = Modifier.padding(8.dp),
            text = "$text ${(curPercentage.value * 100).roundToInt()} %",
            style = MaterialTheme.typography.subtitle1,
            color = fontColor,
            textAlign = TextAlign.Center
        )
    }

}