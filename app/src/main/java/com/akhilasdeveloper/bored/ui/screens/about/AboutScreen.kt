package com.akhilasdeveloper.bored.ui.screens.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.akhilasdeveloper.bored.ui.screens.CardSecondText
import com.akhilasdeveloper.bored.ui.screens.home.HomeViewModel
import com.akhilasdeveloper.bored.ui.theme.accentColor
import com.akhilasdeveloper.bored.ui.theme.colorMain
import com.akhilasdeveloper.bored.ui.theme.colorMainFg
import com.akhilasdeveloper.bored.R

@Composable
fun AboutScreen(viewModel: HomeViewModel = viewModel()) {
    AboutContent()
}

@Preview(showBackground = true)
@Composable
fun AboutContent() {

    val scrollState = rememberScrollState()
    Column(
        Modifier
            .verticalScroll(scrollState)
            .fillMaxSize()
            .background(colorMain)
            .padding(top = 100.dp, bottom = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(painterResource(id = R.mipmap.ic_launcher_foreground), contentDescription = "Icon")

        CardSecondText(
            text = "Bored!",
            textColor = accentColor,
            fontSize = 28.sp,
        )

        Spacer(
            modifier = Modifier
                .padding(top = 20.dp)
        )

        CardSecondText(
            text = "Let's find you something to do",
            textColor = colorMainFg,
            fontSize = 18.sp
        )

        Spacer(
            modifier = Modifier
                .padding(top = 60.dp)
        )

        CardSecondText(
            text = "API provided by https://www.boredapi.com",
            textColor = colorMainFg,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier
                .padding(top = 60.dp)
        )

        CardSecondText(
            text = "Terms",
            textColor = colorMainFg,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray.copy(alpha = .5f))
                .padding(top = 10.dp, bottom = 10.dp, start = 12.dp, end = 12.dp),
            textAlign = TextAlign.Start
        )

        Column(
            Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 20.dp)) {

            Text(
                modifier = Modifier.fillMaxWidth(),
                text =
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Color.White.copy(alpha = .7f),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    ) {
                        withStyle(
                            style = SpanStyle(
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        ) {
                            append("Accessibility\n\n")
                        }
                        append("A factor describing how possible an event is to do with zero being the most accessible. [0% to 100%]")
                    }
                }
            )
            Spacer(
                modifier = Modifier
                    .padding(top = 20.dp)
            )
            Divider(
                color = Color.Gray.copy(alpha = .5f)
            )
            Spacer(
                modifier = Modifier
                    .padding(top = 20.dp)
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text =
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Color.White.copy(alpha = .7f),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    ) {
                        withStyle(
                            style = SpanStyle(
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        ) {
                            append("Type\n\n")
                        }
                        append("Type of the activity. [\"education\", \"recreational\", \"social\", \"diy\", \"charity\", \"cooking\", \"relaxation\", \"music\", \"busywork\"]")
                    }
                }
            )
            Spacer(
                modifier = Modifier
                    .padding(top = 20.dp)
            )
            Divider(
                color = Color.Gray.copy(alpha = .5f)
            )
            Spacer(
                modifier = Modifier
                    .padding(top = 20.dp)
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text =
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Color.White.copy(alpha = .7f),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    ) {
                        withStyle(
                            style = SpanStyle(
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        ) {
                            append("Participants\n\n")
                        }
                        append("The number of people that this activity could involve.")
                    }
                }
            )

            Spacer(
                modifier = Modifier
                    .padding(top = 20.dp)
            )
            Divider(
                color = Color.Gray.copy(alpha = .5f)
            )
            Spacer(
                modifier = Modifier
                    .padding(top = 20.dp)
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text =
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Color.White.copy(alpha = .7f),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    ) {
                        withStyle(
                            style = SpanStyle(
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        ) {
                            append("Price\n\n")
                        }
                        append("A factor describing the cost of the event with zero being free. [0% to 100%]")
                    }
                }
            )
        }

        Spacer(
            modifier = Modifier
                .padding(top = 20.dp)
        )

        CardSecondText(
            text = "Developer Contact",
            textColor = colorMainFg,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray.copy(alpha = .5f))
                .padding(top = 10.dp, bottom = 10.dp, start = 12.dp, end = 12.dp),
            textAlign = TextAlign.Start
        )

        Column(
            Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 20.dp)) {

            Text(
                modifier = Modifier.fillMaxWidth(),
                text =
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Color.White.copy(alpha = .7f),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    ) {
                        withStyle(
                            style = SpanStyle(
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        ) {
                            append("Email : ")
                        }
                        append("akhilasdeveloper@gamil.com")
                    }
                }
            )
            Spacer(
                modifier = Modifier
                    .padding(top = 20.dp)
            )

        }
    }
}