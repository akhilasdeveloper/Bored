package com.akhilasdeveloper.bored.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.akhilasdeveloper.bored.data.CardDao
import com.akhilasdeveloper.bored.ui.theme.buttonGreen
import com.akhilasdeveloper.bored.ui.theme.buttonOrange
import com.akhilasdeveloper.bored.ui.theme.textGreen
import com.akhilasdeveloper.bored.ui.theme.textOrange
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getRandomActivity()

        setContent {
            Greeting()
        }
    }
}

@Composable
fun Greeting(viewModel: MainViewModel = viewModel()) {

    val cardData = viewModel.boredActivityState

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
                    .background(buttonOrange)
                    .clickable {
                        viewModel.getRandomActivity()
                    },
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
        cardData.value?.let {
            CardView(
                cardDao = it
            )
        }
    }
}

@Composable
fun CardView(
    cardDao: CardDao
) {
    var moreIsVisible by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(15.dp),
        backgroundColor = cardDao.cardColor.colorCardBg,
        elevation = 5.dp,
        modifier = Modifier
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
                cardDao.activityName?.let {
                    CardPrimaryText(activityName = it, textColor = cardDao.cardColor.colorCardFg)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            AnimatedVisibility(
                visible = moreIsVisible,
                enter = expandVertically(animationSpec = tween(durationMillis = 500)),
                exit = shrinkVertically(animationSpec = tween(durationMillis = 500)),
                modifier = Modifier.background(cardDao.cardColor.colorCardSecondBg)
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
fun CardSecondText(text: String?, textColor: Color, modifier: Modifier = Modifier) {
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
                    fontSize = 18.sp
                ),
                textAlign = TextAlign.Center
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
        CardSecondText(text = cardDao.type, textColor = cardDao.cardColor.colorCardSecondFg)
        CardSecondText(
            text = cardDao.participants.toString(),
            textColor = cardDao.cardColor.colorCardSecondFg
        )
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            LinearProgressBar(percentage = cardDao.accessibility, color = cardDao.cardColor.colorCardBg)
            LinearProgressBar(percentage = cardDao.price, color = cardDao.cardColor.colorCardBg)
        }
    }
}

@Composable
fun LinearProgressBar(
    percentage: Float?,
    fontSize: TextUnit = 16.sp,
    color: Color = textOrange,
    strokeWidth: Dp = 8.dp,
    size: Dp = 50.dp,
    animationDuration: Int = 1000,
    animationDelay: Int = 0
){
    var animationPlayed by remember {
        mutableStateOf(false)
    }

    val curPercentage = animateFloatAsState(
        targetValue = if (animationPlayed) percentage?:0f else 0f,
        animationSpec = tween(
            durationMillis = animationDuration,
            delayMillis = animationDelay
        )
    )
    
    LaunchedEffect(key1 = true ){
        animationPlayed = true
    }

    Box {
        CardSecondText(
            text = "${(curPercentage.value * 100).roundToInt()} %",
            textColor = Color.White
        )
        Canvas(modifier = Modifier.size(width = size, height = strokeWidth).padding(start = 20.dp)){
            drawLine(
                color = Color.White,
                strokeWidth = strokeWidth.toPx(),
                cap = StrokeCap.Round,
                start = Offset(0f,0f),
                end = Offset(size.toPx(),0f)
            )
            drawLine(
                color = color,
                strokeWidth = strokeWidth.toPx(),
                cap = StrokeCap.Round,
                start = Offset(0f,0f),
                end = Offset(curPercentage.value * size.toPx(),0f)
            )
        }
    }
}