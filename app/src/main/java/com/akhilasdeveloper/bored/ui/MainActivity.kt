package com.akhilasdeveloper.bored.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.akhilasdeveloper.bored.data.CardDao
import com.akhilasdeveloper.bored.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@ExperimentalAnimationApi
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

@ExperimentalAnimationApi
@Composable
fun Greeting(viewModel: MainViewModel = viewModel()) {

    val cardData = viewModel.boredActivityState

    var color by remember { mutableStateOf(Color.White) }
    var text by remember { mutableStateOf("") }

    val colorAnim = animateColorAsState(targetValue = color)

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
            color = it.cardColor.colorCardBg
            text = it.activityName.toString()
            CardView(
                cardDao = it,
                colorAnim = colorAnim,
                text = text
            )
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun CardView(
    cardDao: CardDao,
    colorAnim: State<Color>,
    text: String
) {
    var moreIsVisible by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(15.dp),
        backgroundColor = colorAnim.value,
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
                    AnimatedContent(targetState = text) { targetCount ->
                        CardPrimaryText(activityName = targetCount, textColor = cardDao.cardColor.colorCardFg)
                    }
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
                    cardDao = cardDao,
                    colorAnim = colorAnim
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
    cardDao: CardDao,
    colorAnim: State<Color>
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        CardSecondText(text = cardDao.link, textColor = cardDao.cardColor.colorCardSecondFg)
        CardSecondText(text = "Type : ${cardDao.type}", textColor = cardDao.cardColor.colorCardSecondFg)
        CardSecondText(
            text = "Participants : ${cardDao.participants}",
            textColor = cardDao.cardColor.colorCardSecondFg
        )
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier
                .weight(1f)
                .padding(10.dp)){
                LinearProgressBar(percentage = cardDao.accessibility, color = colorAnim)
            }
            Box(modifier = Modifier
                .weight(1f)
                .padding(10.dp)){
                LinearProgressBar(percentage = cardDao.price, color = colorAnim)
            }
        }
    }
}

@Composable
fun LinearProgressBar(
    percentage: Float?,
    fontSize: TextUnit = 16.sp,
    color: State<Color>,
    strokeWidth: Dp = 8.dp,
    size: Int = 80,
    animationDuration: Int = 1000,
    animationDelay: Int = 0,
//    onTextChange: (text:String)->Unit
){
    var animationPlayed by remember { mutableStateOf(false) }
    var sizeAct by remember { mutableStateOf(size) }

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

    Box(modifier = Modifier.onGloballyPositioned {
        sizeAct = it.size.width
    }) {

        CardSecondText(
            text = "${(curPercentage.value * 100).roundToInt()} %",
            textColor = Color.White
        )

//        Text(text = "${(curPercentage.value * 100).roundToInt()} %")

        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier
                .size(width = sizeAct.dp, height = strokeWidth)){
                drawLine(
                    color = Color.White,
                    strokeWidth = strokeWidth.toPx(),
                    cap = StrokeCap.Round,
                    start = Offset(0f,0f),
                    end = Offset(sizeAct.toFloat(),0f)
                )
                drawLine(
                    color = color.value,
                    strokeWidth = strokeWidth.toPx(),
                    cap = StrokeCap.Round,
                    start = Offset(0f,0f),
                    end = Offset(curPercentage.value * sizeAct,0f)
                )
            }
        }
    }

}