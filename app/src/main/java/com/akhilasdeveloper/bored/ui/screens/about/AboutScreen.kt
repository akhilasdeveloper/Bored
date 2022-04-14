package com.akhilasdeveloper.bored.ui.screens.about

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import com.akhilasdeveloper.bored.ui.screens.home.CardSecondText
import com.akhilasdeveloper.bored.ui.screens.home.HomeViewModel
import com.akhilasdeveloper.bored.ui.theme.accentColor
import com.akhilasdeveloper.bored.R
import com.akhilasdeveloper.bored.data.mapper.ThemeValueMapper
import com.akhilasdeveloper.bored.ui.theme.onAccentColor

@Preview(showBackground = true)
@Composable
fun AboutScreen(viewModel: HomeViewModel = viewModel()) {

    val theme = viewModel.currentTheme.collectAsState(
        initial = ThemeValueMapper.getSystemInDarkValueFromBoolean(
            isSystemInDarkTheme()
        )
    )
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val isDarkTheme = derivedStateOf {
        ThemeValueMapper.getSystemInDarkValueFromConst(theme.value) ?: isSystemInDarkTheme
    }
    val selectedColor = MaterialTheme.colors.primary
    val onSelectedColor = MaterialTheme.colors.onPrimary
    val deselectedColor = MaterialTheme.colors.surface
    val onDeselectedColor = MaterialTheme.colors.onSurface
    val systemThemeButtonBackgroundColor = derivedStateOf { if (theme.value == ThemeValueMapper.SYSTEM_THEME) selectedColor else deselectedColor }
    val systemThemeButtonForegroundColor = derivedStateOf { if (theme.value == ThemeValueMapper.SYSTEM_THEME) onSelectedColor else onDeselectedColor }
    val darkThemeButtonBackgroundColor = derivedStateOf { if (theme.value == ThemeValueMapper.DARK_THEME) selectedColor else deselectedColor }
    val darkThemeButtonForegroundColor = derivedStateOf { if (theme.value == ThemeValueMapper.DARK_THEME) onSelectedColor else onDeselectedColor }
    val lightThemeButtonBackgroundColor = derivedStateOf { if (theme.value == ThemeValueMapper.LIGHT_THEME) selectedColor else deselectedColor }
    val lightThemeButtonForegroundColor = derivedStateOf { if (theme.value == ThemeValueMapper.LIGHT_THEME) onSelectedColor else onDeselectedColor }

    val scrollState = rememberScrollState()
    Column(
        Modifier
            .verticalScroll(scrollState)
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(top = 100.dp, bottom = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painterResource(
                id =
                if (isDarkTheme.value)
                    R.mipmap.ic_launcher_foreground
                else
                    R.mipmap.ic_launcher_foreground_dark
            ), contentDescription = "Icon"
        )

        Text(
            text = "Bored!",
            style = MaterialTheme.typography.h4,
            color = accentColor,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier
                .padding(top = 16.dp)
        )

        Text(
            text = "Let's find you something to do",
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier
                .padding(top = 60.dp)
        )
        val typo1 = MaterialTheme.typography.subtitle2.copy(color = MaterialTheme.colors.onBackground)
        val typo2 = MaterialTheme.typography.subtitle1.copy(color = MaterialTheme.colors.onBackground)

        Text(
            text = "Theme",
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.onBackground.copy(alpha = .1f))
                .padding(top = 10.dp, bottom = 10.dp, start = 12.dp, end = 12.dp),
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onBackground
        )

        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            Card(
                modifier = Modifier
                    .clickable(
                        indication = rememberRipple(color = MaterialTheme.colors.onPrimary),
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            if (theme.value != ThemeValueMapper.SYSTEM_THEME)
                                viewModel.setCurrentThemeValue(ThemeValueMapper.SYSTEM_THEME)
                        }
                    )
                    .weight(1f)
                    .padding(top = 10.dp, bottom = 10.dp, start = 12.dp, end = 12.dp),
                backgroundColor = systemThemeButtonBackgroundColor.value
            ) {
                Text(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 10.dp, start = 12.dp, end = 12.dp),
                    text = "System",
                    style = MaterialTheme.typography.subtitle2,
                    color = systemThemeButtonForegroundColor.value,
                    textAlign = TextAlign.Center
                )
            }

            Card(
                modifier = Modifier
                    .clickable(
                        indication = rememberRipple(color = MaterialTheme.colors.onPrimary),
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            if (theme.value != ThemeValueMapper.DARK_THEME)
                                viewModel.setCurrentThemeValue(ThemeValueMapper.DARK_THEME)
                        }
                    )
                    .weight(1f)
                    .padding(top = 10.dp, bottom = 10.dp, end = 12.dp),
                backgroundColor = darkThemeButtonBackgroundColor.value
            ) {
                Text(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 10.dp, start = 12.dp, end = 12.dp),
                    text = "Dark",
                    style = MaterialTheme.typography.subtitle2,
                    color = darkThemeButtonForegroundColor.value,
                    textAlign = TextAlign.Center
                )
            }

            Card(
                modifier = Modifier
                    .clickable(
                        indication = rememberRipple(color = MaterialTheme.colors.onPrimary),
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            if (theme.value != ThemeValueMapper.LIGHT_THEME)
                                viewModel.setCurrentThemeValue(ThemeValueMapper.LIGHT_THEME)
                        }
                    )
                    .weight(1f)
                    .padding(top = 10.dp, bottom = 10.dp, end = 12.dp),
                backgroundColor = lightThemeButtonBackgroundColor.value
            ) {
                Text(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 10.dp, start = 12.dp, end = 12.dp),
                    text = "Light",
                    style = MaterialTheme.typography.subtitle2,
                    color = lightThemeButtonForegroundColor.value,
                    textAlign = TextAlign.Center
                )
            }

        }

        Text(
            text = "Terms",
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.onBackground.copy(alpha = .1f))
                .padding(top = 10.dp, bottom = 10.dp, start = 12.dp, end = 12.dp),
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onBackground
        )

        Column(
            Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 20.dp)
        ) {

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = typo1.toSpanStyle()
                    ) {
                        withStyle(
                            style = typo2.toSpanStyle()
                        ) {
                            append("Accessibility\n\n")
                        }
                        append("A factor describing how possible an event is to do with zero being the most accessible. [0% to 100%]")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onBackground
            )

            Spacer(
                modifier = Modifier
                    .padding(top = 20.dp)
            )
            Divider(
                color = MaterialTheme.colors.onBackground.copy(alpha = .5f)
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
                        style = typo1.toSpanStyle()
                    ) {
                        withStyle(
                            style = typo2.toSpanStyle()
                        ) {
                            append("Type\n\n")
                        }
                        append("Type of the activity. [\"education\", \"recreational\", \"social\", \"diy\", \"charity\", \"cooking\", \"relaxation\", \"music\", \"busywork\"]")
                    }
                },
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onBackground
            )
            Spacer(
                modifier = Modifier
                    .padding(top = 20.dp)
            )
            Divider(
                color = MaterialTheme.colors.onBackground.copy(alpha = .5f)
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
                        style = typo1.toSpanStyle()
                    ) {
                        withStyle(
                            style = typo2.toSpanStyle()
                        ) {
                            append("Participants\n\n")
                        }
                        append("The number of people that this activity could involve.")
                    }
                },
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onBackground
            )

            Spacer(
                modifier = Modifier
                    .padding(top = 20.dp)
            )
            Divider(
                color = MaterialTheme.colors.onBackground.copy(alpha = .5f)
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
                        style = typo1.toSpanStyle()
                    ) {
                        withStyle(
                            style = typo2.toSpanStyle()
                        ) {
                            append("Price\n\n")
                        }
                        append("A factor describing the cost of the event with zero being free. [0% to 100%]")
                    }
                },
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onBackground
            )
        }

        Spacer(
            modifier = Modifier
                .padding(top = 20.dp)
        )

        Text(
            text = "Developer Contact",
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.onBackground.copy(alpha = .1f))
                .padding(top = 10.dp, bottom = 10.dp, start = 12.dp, end = 12.dp),
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onBackground
        )

        Column(
            Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 20.dp)
        ) {

            Text(
                modifier = Modifier.fillMaxWidth(),
                text =
                buildAnnotatedString {
                    withStyle(
                        style = typo1.toSpanStyle()
                    ) {
                        withStyle(
                            style = typo2.toSpanStyle()
                        ) {
                            append("Email : ")
                        }
                        append("akhilasdeveloper@gamil.com")
                    }
                }
            )

        }
        Text(
            text = "API provided by",
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.onBackground.copy(alpha = .1f))
                .padding(top = 10.dp, bottom = 10.dp, start = 12.dp, end = 12.dp),
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onBackground
        )

        Column(
            Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 20.dp)
        ) {

            Text(
                modifier = Modifier.fillMaxWidth(),
                text =
                buildAnnotatedString {
                    withStyle(
                        style = typo1.toSpanStyle()
                    ) {
                        withStyle(
                            style = typo2.toSpanStyle()
                        ) {
                            append("Site : ")
                        }
                        append("https://www.boredapi.com")
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