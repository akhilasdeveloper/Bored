package com.akhilasdeveloper.bored

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akhilasdeveloper.bored.ui.theme.*

@ExperimentalAnimationApi
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
    var buttonIsVisible by remember {
        mutableStateOf(true)
    }
    var moreIsVisible by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            shape = RoundedCornerShape(15.dp),
            backgroundColor = cardRose,
            elevation = 5.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
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
                    visible = buttonIsVisible,
                    enter = expandVertically(animationSpec = tween(durationMillis = 500)),
                    exit = shrinkVertically(animationSpec = tween(durationMillis = 500))
                ) {
                    Button(
                        onClick = {
                            buttonIsVisible = false
                            moreIsVisible = true
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(backgroundColor = buttonBlack)
                    ) {
                        Box(modifier = Modifier.padding(10.dp)) {
                            CardSecondText(text = "More", textColor = Color.White)
                        }
                    }
                }
                AnimatedVisibility(
                    visible = moreIsVisible,
                    enter = expandVertically(animationSpec = tween(durationMillis = 500)),
                    exit = shrinkVertically(animationSpec = tween(durationMillis = 500))
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