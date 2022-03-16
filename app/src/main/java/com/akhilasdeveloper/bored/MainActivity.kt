package com.akhilasdeveloper.bored

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                        activityName = "Contribute code or a monetary donation to an open-source software project",
                        accessibility = 0f,
                        type = "charity",
                        participants = 1,
                        price = 0.1f,
                        link = "https://github.com/explore",
                        modifier = Modifier
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
        activityName = "Contribute code or a monetary donation to an open-source software project",
        accessibility = 0f,
        type = "charity",
        participants = 1,
        price = 0.1f,
        link = "https://github.com/explore",
        modifier = Modifier
    )
}

@ExperimentalAnimationApi
@Preview(showBackground = true, backgroundColor = 1)
@Composable
fun PreviewMoreContent() {
    MoreContent(
        accessibility = 0f,
        type = "charity",
        participants = 1,
        price = 0.1f,
        link = "https://github.com/explore"
    )
}

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Composable
fun Greeting(
    modifier: Modifier,
    activityName: String,
    accessibility: Float,
    type: String,
    participants: Int,
    price: Float,
    link: String
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                Modifier
                    .weight(1f)
                    .background(buttonOrange),
                contentAlignment = Alignment.Center
            ) {
                Box(Modifier.padding(10.dp))
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
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        CardView(
            activityName = activityName,
            accessibility = accessibility,
            type = type,
            participants = participants,
            price = price,
            link = link
        )
    }
}

@ExperimentalAnimationApi
@Composable
fun CardView(
    activityName: String,
    accessibility: Float,
    type: String,
    participants: Int,
    price: Float,
    link: String
) {
    var moreIsVisible by remember {
        mutableStateOf(false)
    }
    var isAnimate by remember { mutableStateOf(false) }

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var scale by remember { mutableStateOf(1f) }

    val offsetXAnim by animateFloatAsState(targetValue = offsetX)
    val offsetYAnim by animateFloatAsState(targetValue = offsetY)
    val scaleAnim by animateFloatAsState(targetValue = scale)
    Card(
        shape = RoundedCornerShape(15.dp),
        backgroundColor = cardRose,
        elevation = 5.dp,
        modifier = Modifier
            .offset {
                if (isAnimate) IntOffset(offsetXAnim.roundToInt(), offsetYAnim.roundToInt()) else
                    IntOffset(offsetX.roundToInt(), offsetY.roundToInt())
            }
            .scale(scaleAnim)
            .pointerInput(Unit) {
                detectDragGestures(onDragEnd = {
                    offsetX = 0f
                    offsetY = 0f
                    scale = 1f
                    isAnimate = true
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
                    if (offsetX >= 200 || offsetX <= -200)
                        scale = .3f

                }
            }
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
                CardPrimaryText(activityName = activityName, textColor = Color.White)
            }
            Spacer(modifier = Modifier.height(20.dp))
            AnimatedVisibility(
                visible = moreIsVisible,
                enter = expandVertically(animationSpec = tween(durationMillis = 500)),
                exit = shrinkVertically(animationSpec = tween(durationMillis = 500)),
                modifier = Modifier.background(buttonBlack)
            ) {
                MoreContent(
                    accessibility,
                    type,
                    participants,
                    price,
                    link
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
    accessibility: Float,
    type: String,
    participants: Int,
    price: Float,
    link: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        CardSecondText(text = link, textColor = Color.White)
        Spacer(modifier = Modifier.height(20.dp))
        CardSecondText(text = type, textColor = Color.White)
        Spacer(modifier = Modifier.height(20.dp))
        CardSecondText(text = participants.toString(), textColor = Color.White)
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            CardSecondText(text = accessibility.toString(), textColor = Color.White)
            CardSecondText(text = price.toString(), textColor = Color.White)
        }
    }
}