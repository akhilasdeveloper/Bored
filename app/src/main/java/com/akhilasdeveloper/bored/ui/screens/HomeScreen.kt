package com.akhilasdeveloper.bored.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.akhilasdeveloper.bored.data.CardDao
import com.akhilasdeveloper.bored.ui.MainViewModel
import com.akhilasdeveloper.bored.ui.theme.*
import com.akhilasdeveloper.bored.util.Constants
import kotlinx.coroutines.delay
import kotlin.math.roundToInt


@Preview(showBackground = true)
@Composable
fun Greeting(viewModel: MainViewModel = viewModel()) {

    val cardStates = viewModel.cardStates
    val cards = viewModel.cards

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
                text = "Pass",
                isSelected = viewModel.passSelected.value,
                modifier = Modifier.weight(1f),
                viewModel = viewModel
            ) {
                viewModel.passSelected()
            }
            SelectionButton(
                text = "Add",
                isSelected = viewModel.addSelected.value,
                modifier = Modifier.weight(1f),
                viewModel = viewModel
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
                    }
                )
            }

            LoadingProgress(
                color = viewModel.progressBarColor.value,
                visibility = viewModel.loadingState.value
            )
        }

    }
}

@Composable
fun SelectionButton(
    modifier: Modifier = Modifier,
    text: String,
    viewModel: MainViewModel = viewModel(),
    isSelected: Boolean = false,
    onClicked: () -> Unit
) {
    var fontSize by remember { mutableStateOf(selectionDeselectedFontSize) }
    var selectionColor by remember { mutableStateOf(viewModel.transparentValue()) }
    val animFontSize by animateIntAsState(targetValue = fontSize)
    val animSelectionColor by animateColorAsState(targetValue = selectionColor)

    SideEffect {
        fontSize = if (isSelected) selectionSelectedFontSize else selectionDeselectedFontSize
        selectionColor =
            if (isSelected) viewModel.selectionColor.value else viewModel.transparentValue()
    }

    Box(
        modifier
            .fillMaxHeight()
            .background(
                animSelectionColor.copy(alpha = .3f)
            )
            .clickable(indication = rememberRipple(color = viewModel.selectionColor.value),
                interactionSource = remember { MutableInteractionSource() }) {
                onClicked()
            },
        contentAlignment = Alignment.BottomCenter
    )
    {
        CardSecondText(
            text = text,
            textColor = viewModel.systemBarColorFg.value,
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
    cardState: State<Int>,
    onSelected: (selection: Int) -> Unit,
    onDragSelect: (selection: Int) -> Unit,
    onRemoveCompleted: () -> Unit,
    onLoadCompleted: () -> Unit
) {

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var scale by remember { mutableStateOf(1f) }

    var isAnimate by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }

    val offsetXAnim by animateFloatAsState(targetValue = offsetX)
    val offsetYAnim by animateFloatAsState(targetValue = offsetY)
    val scaleAnim by animateFloatAsState(targetValue = scale, finishedListener = {
        if (it == 0f) {
            onRemoveCompleted()
            onDragSelect(Constants.IDLE_SELECTION)
        }
    })

    var moreIsVisible by remember { mutableStateOf(false) }
    val cardIsVisible = remember { MutableTransitionState(false) }
        .apply { targetState = true }

    LaunchedEffect(key1 = true) {
        delay(500)
        onLoadCompleted()
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
            shape = RoundedCornerShape(15.dp),
            backgroundColor = cardDao.cardColor.colorCardBg,
            elevation = 5.dp,
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
                    detectDragGestures(onDragEnd = {

                        isAnimate = true

                        if (offsetX >= 200) {
                            onSelected(Constants.ADD_SELECTION)
                            isFinished = true
                        }

                        if (offsetX <= -200) {
                            onSelected(Constants.PASS_SELECTION)
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
                                    onDragSelect(Constants.ADD_SELECTION)

                                if (offsetX <= -200)
                                    onDragSelect(Constants.PASS_SELECTION)

                            } else {
                                scale = 1f

                                onDragSelect(Constants.IDLE_SELECTION)
                            }
                        }
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        indication = rememberRipple(color = cardDao.cardColor.colorCardFg),
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            moreIsVisible = !moreIsVisible
                        }
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    cardDao.activityName?.let {
                        CardPrimaryText(
                            activityName = it,
                            textColor = cardDao.cardColor.colorCardFg
                        )
                    }
                }
                AnimatedVisibility(
                    visible = moreIsVisible,
                    enter = expandVertically(animationSpec = tween(durationMillis = 500)),
                    exit = shrinkVertically(animationSpec = tween(durationMillis = 500)),
                    modifier = Modifier.background(cardDao.cardColor.colorCardSecondBg)
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    MoreContent(
                        cardDao = cardDao
                    )
                }

            }

        }
    }
}


@Composable
fun CardPrimaryText(activityName: String, textColor: Color) {
    Text(
        text = activityName,
        style = TextStyle(
            color = textColor,
            fontWeight = FontWeight.Bold,
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
    fontSize: TextUnit = secondaryFontSize.sp
) {
    AnimatedVisibility(
        visible = !text.isNullOrEmpty(),
        enter = expandVertically(animationSpec = tween(durationMillis = 500)),
        exit = shrinkVertically(animationSpec = tween(durationMillis = 500))
    ) {
        text?.let {
            Text(
                modifier = modifier.padding(10.dp),
                text = text,
                style = TextStyle(
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = fontSize
                ),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun MoreContent(
    cardDao: CardDao
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        CardSecondText(text = cardDao.link, textColor = cardDao.cardColor.colorCardSecondFg)
        CardSecondText(
            text = "Type : ${cardDao.type}",
            textColor = cardDao.cardColor.colorCardSecondFg
        )
        CardSecondText(
            text = "Participants : ${cardDao.participants}",
            textColor = cardDao.cardColor.colorCardSecondFg
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
                    color = cardDao.cardColor.colorCardBg,
                    text = "Accessibility",
                    fontColor = cardDao.cardColor.colorCardSecondFg
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(10.dp)
            ) {
                LinearProgressBar(
                    percentage = cardDao.price,
                    color = cardDao.cardColor.colorCardBg,
                    text = "Price",
                    fontColor = cardDao.cardColor.colorCardSecondFg
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
            text = "$text ${(curPercentage.value * 100).roundToInt()} %",
            textColor = fontColor,
            fontSize = fontSize
        )
    }

}