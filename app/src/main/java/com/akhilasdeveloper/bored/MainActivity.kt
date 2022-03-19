package com.akhilasdeveloper.bored

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akhilasdeveloper.bored.data.CardDao
import com.akhilasdeveloper.bored.ui.theme.*
import kotlin.math.roundToInt

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BoredTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting(
                        cardDao = CardDao(
                            activityName = "Contribute code or a monetary donation to an open-source software project",
                            accessibility = 0f,
                            type = "charity",
                            participants = 1,
                            price = 0.1f,
                            link = "https://github.com/explore",
                        )
                    )
                }
            }
        }
    }
}

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Preview(showBackground = true)
@Composable
fun PreviewGreetings() {
    Greeting(
        cardDao = CardDao(
            activityName = "Contribute code or a monetary donation to an open-source software project",
            accessibility = 0f,
            type = "charity",
            participants = 1,
            price = 0.1f,
            link = "https://github.com/explore",
        )
    )
}

@ExperimentalAnimationApi
@Preview(showBackground = true, backgroundColor = 1)
@Composable
fun PreviewMoreContent() {
    MoreContent(
        CardDao(
            activityName = "Contribute code or a monetary donation to an open-source software project",
            accessibility = 0f,
            type = "charity",
            participants = 1,
            price = 0.1f,
            link = "https://github.com/explore"
        )
    )
}

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Composable
fun Greeting(
    cardDao: CardDao
) {
    var leftTrans by remember { mutableStateOf(Color.Transparent) }
    var rightTrans by remember { mutableStateOf(Color.Transparent) }

    val leftTransAnim by animateColorAsState(targetValue = leftTrans)
    val rightTransAnim by animateColorAsState(targetValue = rightTrans)

    var isAnimate by remember { mutableStateOf(false) }

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var scale by remember { mutableStateOf(1f) }

    val offsetXAnim by animateFloatAsState(targetValue = offsetX)
    val offsetYAnim by animateFloatAsState(targetValue = offsetY)
    val scaleAnim by animateFloatAsState(targetValue = scale)

    val configuration = LocalConfiguration.current
    val screenDensity = configuration.densityDpi / 160f
    val screenHeightPx = configuration.screenHeightDp.toFloat() * screenDensity
    val screenWidthPx = configuration.screenWidthDp.toFloat() * screenDensity

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(Modifier.fillMaxSize()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .background(leftTransAnim)
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .background(rightTransAnim)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                Modifier
                    .weight(1f)
                    .background(buttonOrange),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    Modifier
                        .padding(10.dp)
                )
                {
                    CardSecondText(text = "Pass", textColor = textOrange)
                }
            }

            Box(
                Modifier
                    .weight(1f)
                    .background(buttonGreen),
                contentAlignment = Alignment.Center
            ) {
                Box(Modifier.padding(10.dp))
                {
                    CardSecondText(text = "Add", textColor = textGreen)
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        CardView(
            cardDao = cardDao,
            modifier = Modifier
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

                        if (scale == 1f) {
                            offsetX = 0f
                            offsetY = 0f
                        } else {
                            scale = 0f
                        }
                        isAnimate = true
                        rightTrans = buttonGreenTransparent
                        leftTrans = buttonOrangeTransparent

                        if (offsetX >= 200) {
                            val x = screenWidthPx / 4
                            val y = screenHeightPx / 2
                            offsetX = x
                            offsetY = y
                        }

                        if (offsetX <= -200) {
                            val x = -screenWidthPx / 4
                            val y = screenHeightPx / 2
                            offsetX = x
                            offsetY = y
                        }

                    }) { change, dragAmount ->

                        change.consumeAllChanges()

                        isAnimate = false

                        val (x, y) = dragAmount
                        when {
                            x > 0 -> { /* right */
                            }
                            x < 0 -> { /* left */
                            }
                        }
                        when {
                            y > 0 -> { /* down */
                            }
                            y < 0 -> { /* up */
                            }
                        }

                        offsetX += dragAmount.x * scale
                        if (offsetY + (dragAmount.y * scale) >= 0)
                            offsetY += dragAmount.y * scale
                        if (offsetX >= 200 || offsetX <= -200) {
                            scale = .3f

                            if (offsetX >= 200)
                                rightTrans = buttonGreenLite
                            if (offsetX <= -200)
                                leftTrans = buttonOrangeLite

                        } else {
                            scale = 1f
                            rightTrans = buttonGreenTransparent
                            leftTrans = buttonOrangeTransparent
                        }

                    }
                }
        )
    }
}

@ExperimentalAnimationApi
@Composable
fun CardView(
    cardDao: CardDao,
    modifier: Modifier
) {
    var moreIsVisible by remember {
        mutableStateOf(false)
    }


    Card(
        shape = RoundedCornerShape(15.dp),
        backgroundColor = cardRose,
        elevation = 5.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = {
                        moreIsVisible = !moreIsVisible
                    }
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                CardPrimaryText(activityName = cardDao.activityName, textColor = Color.White)
            }
            Spacer(modifier = Modifier.height(20.dp))
            AnimatedVisibility(
                visible = moreIsVisible,
                enter = expandVertically(animationSpec = tween(durationMillis = 500)),
                exit = shrinkVertically(animationSpec = tween(durationMillis = 500)),
                modifier = Modifier.background(buttonBlack)
            ) {
                MoreContent(
                    cardDao = cardDao
                )
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
fun CardSecondText(text: String, textColor: Color) {
    Text(
        text = text,
        style = TextStyle(
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        ),
        textAlign = TextAlign.Center
    )
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
        CardSecondText(text = cardDao.link, textColor = Color.White)
        Spacer(modifier = Modifier.height(20.dp))
        CardSecondText(text = cardDao.type, textColor = Color.White)
        Spacer(modifier = Modifier.height(20.dp))
        CardSecondText(text = cardDao.participants.toString(), textColor = Color.White)
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            CardSecondText(text = cardDao.accessibility.toString(), textColor = Color.White)
            CardSecondText(text = cardDao.price.toString(), textColor = Color.White)
        }
    }
}