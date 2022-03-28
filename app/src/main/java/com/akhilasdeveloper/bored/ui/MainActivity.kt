package com.akhilasdeveloper.bored.ui

import android.opengl.Visibility
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
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
import com.akhilasdeveloper.bored.ui.theme.*
import com.akhilasdeveloper.bored.util.Constants.ADD_SELECTION
import com.akhilasdeveloper.bored.util.Constants.IDLE_SELECTION
import com.akhilasdeveloper.bored.util.Constants.PASS_SELECTION
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import timber.log.Timber
import kotlin.math.roundToInt

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: MainViewModel by viewModels()

        viewModel.getRandomActivity()

        setContent {
            viewModel.isLightTheme = !isSystemInDarkTheme()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (isSystemInDarkTheme()) colorMain else colorMainLight)
            ) {
                Greeting()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Greeting(viewModel: MainViewModel = viewModel()) {

    val cardStates = viewModel.cardStates
    val cards = viewModel.cards

    val systemUiController = rememberSystemUiController()

    var progressBarColor by remember {
        mutableStateOf(accentColor)
    }

    var systemBarColor by remember {
        mutableStateOf(accentColor)
    }

    var systemBarColorFg by remember {
        mutableStateOf(colorMain)
    }

    var selectionPassColor by remember {
        mutableStateOf(viewModel.transparentValue())
    }

    val animPassFontSize by animateIntAsState(targetValue = viewModel.passFontSize.value)
    val animAddFontSize by animateIntAsState(targetValue = viewModel.addFontSize.value)

    systemUiController.setSystemBarsColor(
        color = systemBarColor,
        darkIcons = viewModel.isLightColor(systemBarColor)
    )

    LaunchedEffect(key1 = true, block = {
        systemBarColor = if (viewModel.isLightTheme) colorMainLight else colorMain
        systemBarColorFg = if (viewModel.isLightTheme) colorMain else colorMainLight
    })

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {

        Box(
            Modifier.weight(1f),
            contentAlignment = Alignment.BottomCenter
        ) {

            Column {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {

                    cards.forEachIndexed { index, card ->

                        progressBarColor = card.cardColor.colorCardBg
                        selectionPassColor = card.cardColor.colorCardBg.copy(alpha = .7f)

                        CardView(
                            cardDao = card,
                            cardState = cardStates[index],
                            onRemoveCompleted = {
                                viewModel.removeCard(card)
                                viewModel.getRandomActivity()
                            },
                            onSelected = {
                                cardStates[index].value = it
                                viewModel.getRandomActivity()
                            },
                            onLoadCompleted = {
                                viewModel.setCardLoadingCompletedState(true)
                                Timber.d("Loading Completed")
                            },
                            onDragSelect = {
                                viewModel.setDragSelectState(it)
                            }
                        )
                    }

                    LoadingProgress(color = progressBarColor, visibility = viewModel.loadingState.value)
                }

                Box(
                    Modifier
                        .padding(10.dp)
                        .background(color = systemBarColor)
                ) {
                    CardSecondText(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Activities",
                        textColor = systemBarColorFg
                    )
                }
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SelectionShade(
                    Modifier.weight(1f),
                    selectionColor = selectionPassColor,
                    visibility = viewModel.passVisibility.value
                )

                SelectionShade(
                    Modifier.weight(1f),
                    selectionColor = selectionPassColor,
                    visibility = viewModel.addVisibility.value
                )

            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(systemBarColor),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .weight(1f)
                    .clickable {
                        if (viewModel.getCardLoadingCompletedState()) {
                            cardStates.last().value = true
                            if (cardStates.size <= 1)
                                viewModel.getRandomActivity()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    Modifier
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                )
                {
                    CardSecondText(
                        text = "Pass",
                        textColor = systemBarColorFg,
                        fontSize = animPassFontSize.sp
                    )
                }
            }

            Box(
                Modifier
                    .weight(1f)
                    .clickable {
                        if (viewModel.getCardLoadingCompletedState()) {
                            cardStates.last().value = false
                            if (cardStates.size <= 1)
                                viewModel.getRandomActivity()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    Modifier.height(100.dp),
                    contentAlignment = Alignment.Center
                )
                {
                    CardSecondText(
                        text = "Add",
                        textColor = systemBarColorFg,
                        fontSize = animAddFontSize.sp
                    )
                }
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
fun SelectionShade(
    modifier: Modifier = Modifier,
    selectionColor: Color,
    viewModel: MainViewModel = viewModel(),
    visibility: Boolean
) {
    Row(modifier) {
        AnimatedVisibility(
            visible = visibility,
            enter = fadeIn(animationSpec = tween(durationMillis = 500)),
            exit = fadeOut(animationSpec = tween(durationMillis = 500))
        ) {
            Box(
                Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                viewModel.transparentValue(),
                                selectionColor
                            )
                        )
                    )
                    .padding(20.dp)
                    .fillMaxSize()
            )
        }
    }
}

@Composable
fun CardView(
    cardDao: CardDao,
    modifier: Modifier = Modifier,
    cardState: State<Boolean?>,
    onSelected: (isPass: Boolean) -> Unit,
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
            onDragSelect(IDLE_SELECTION)
        }
    })

    var moreIsVisible by remember { mutableStateOf(false) }
    val cardIsVisible = remember { MutableTransitionState(false) }
        .apply { targetState = true }

    LaunchedEffect(key1 = true) {
        delay(500)
        onLoadCompleted()
    }

    cardState.value?.let {
        val configuration = LocalConfiguration.current
        val screenDensity = configuration.densityDpi / 160f
        val screenHeightPx = configuration.screenHeightDp.toFloat() * screenDensity
        val screenWidthPx = configuration.screenWidthDp.toFloat() * screenDensity

        isAnimate = true

        val x = if (it) {
            -screenWidthPx / 4
        } else {
            screenWidthPx / 4
        }

        val y = screenHeightPx / 2

        offsetX = x
        offsetY = y

        scale = 0f

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
                            onSelected(false)
                            isFinished = true
                        }

                        if (offsetX <= -200) {
                            onSelected(true)
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
                                    onDragSelect(ADD_SELECTION)

                                if (offsetX <= -200)
                                    onDragSelect(PASS_SELECTION)

                            } else {
                                scale = 1f

                                onDragSelect(IDLE_SELECTION)
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
            fontSize = 24.sp
        ),
        textAlign = TextAlign.Center
    )
}

@Composable
fun CardSecondText(
    modifier: Modifier = Modifier,
    text: String?,
    textColor: Color,
    fontSize: TextUnit = 16.sp
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
    fontSize: TextUnit = 14.sp,
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