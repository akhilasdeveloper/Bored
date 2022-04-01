package com.akhilasdeveloper.bored.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.akhilasdeveloper.bored.data.CardDao
import com.akhilasdeveloper.bored.data.mapper.CategoryMapper
import com.akhilasdeveloper.bored.ui.screens.viewmodels.ActivitiesViewModel
import com.akhilasdeveloper.bored.ui.theme.*
import com.akhilasdeveloper.bored.util.Constants
import timber.log.Timber
import kotlin.math.roundToInt

@ExperimentalFoundationApi
@Composable
fun ActivitiesScreen(viewModel: ActivitiesViewModel = viewModel()) {
    Box(modifier = Modifier.fillMaxSize()) {
        Tabs(viewModel = viewModel)
    }
}

@Composable
fun ActivityItem(
    modifier: Modifier = Modifier,
    cardDao: CardDao,
    checkBoxVisibility: Boolean = false,
    onDraggingRight: ((percentage: Float) -> Unit)? = null,
    onDraggingLeft: ((percentage: Float) -> Unit)? = null,
    onDragCompleted: ((isRight: Boolean) -> Unit)? = null,
    onChecked: ((isCompleted: Boolean) -> Unit)? = null,
    onClicked: (() -> Unit)? = null,
    isMoreVisible: State<Boolean> = mutableStateOf(false)
) {

    val isSystemDarkTheme = isSystemInDarkTheme()
    val configuration = LocalConfiguration.current

    val categoryData by derivedStateOf { CategoryMapper().toSourceFromDestination(cardDao.type) }
    val categoryColor by derivedStateOf { if (isSystemDarkTheme) categoryData.categoryColor.colorDark else categoryData.categoryColor.colorLight }
    val textDecoration by derivedStateOf { if (checkBoxVisibility && cardDao.isCompleted) TextDecoration.LineThrough else TextDecoration.None }
    val backgroundColor by derivedStateOf { if (isSystemDarkTheme) colorMain else colorMainLight }
    val textColor by derivedStateOf { if (isSystemDarkTheme) colorMainFg else colorMainLightFg }
    val screenWidth by derivedStateOf { (configuration.screenWidthDp.toFloat() * (configuration.densityDpi / 160f)) }
    val cardOffset by derivedStateOf { screenWidth / 4 }

    var isFinished by remember {
        mutableStateOf(false)
    }

    var isAnim by remember { mutableStateOf(false) }
    var offsetX by remember { mutableStateOf(0f) }
    val animOffsetX = animateFloatAsState(targetValue = offsetX, finishedListener = {
        if (isFinished && it > 0) {
            onDragCompleted?.invoke(true)
        } else if (isFinished && it < 0) {
            onDragCompleted?.invoke(false)
        }
    })

    animOffsetX.value.let { offset ->
        if (offset > 0) {
            onDraggingRight?.invoke(
                if (offset <= cardOffset) {
                    (offset.coerceIn(
                        0f,
                        cardOffset
                    ) / cardOffset).coerceIn(0f, 1f)
                } else if (offset >= cardOffset) {
                    (1f - ((offset.coerceIn(
                        cardOffset,
                        screenWidth
                    ) - cardOffset) / (screenWidth - cardOffset))).coerceIn(0f, 1f)
                } else {
                    1f
                }
            )
        }
        if (offset < 0) {
            onDraggingLeft?.invoke(
                if (offset >= -cardOffset) {
                    ((-offset).coerceIn(
                        1f,
                        cardOffset
                    ) / cardOffset).coerceIn(0f, 1f)
                }else if (offset <= -cardOffset){
                    (1f - (((-offset).coerceIn(
                        cardOffset,
                        screenWidth
                    ) - cardOffset) / (screenWidth - cardOffset))).coerceIn(0f, 1f)
                }else{
                    1f
                }
            )
        }
    }


    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                indication = rememberRipple(color = categoryColor.colorFg),
                interactionSource = remember { MutableInteractionSource() },
                onClick = {
                    onClicked?.invoke()
                }
            )
            .offset { IntOffset((if (isAnim) animOffsetX.value else offsetX).roundToInt(), 0) }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        isAnim = false
                    },
                    onDragEnd = {
                        isAnim = true
                        if (offsetX <= cardOffset && offsetX >= -cardOffset) {
                            offsetX = 0f
                        } else {
                            if (offsetX < 0) {
                                isFinished = true
                                offsetX = -screenWidth
                            } else if (offsetX > 0) {
                                isFinished = true
                                offsetX = screenWidth
                            }
                        }
                    }
                ) { change, dragAmount ->
                    change.consumePositionChange()
                    offsetX += dragAmount
                }
            },
        shape = RectangleShape,
        backgroundColor = backgroundColor
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(10.dp)
            ) {

                if (checkBoxVisibility) {
                    Checkbox(
                        checked = cardDao.isCompleted,
                        onCheckedChange = {
                            onChecked?.invoke(it)
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = backgroundColor,
                            uncheckedColor = textColor,
                            checkmarkColor = categoryColor.colorBg
                        ),
                        modifier = Modifier
                            .padding(
                                top = 8.dp,
                                bottom = 8.dp
                            ),
                    )

                }

                Icon(
                    imageVector = categoryData.icon,
                    contentDescription = "Category Icon",
                    tint = categoryColor.colorFg,
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 8.dp, start = 10.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(color = categoryColor.colorBg)
                        .padding(5.dp)
                )

                CardSecondText(
                    modifier = Modifier
                        .padding(
                            top = 8.dp,
                            bottom = 8.dp,
                            start = 10.dp
                        ),
                    text = cardDao.activityName,
                    textColor = textColor,
                    textAlign = TextAlign.Start,
                    textDecoration = textDecoration
                )
            }
            AnimatedVisibility(
                visible = isMoreVisible.value,
                enter = expandVertically(animationSpec = tween(durationMillis = 500)),
                exit = shrinkVertically(animationSpec = tween(durationMillis = 500)),
                modifier = Modifier.background(categoryColor.colorSecondBg)
            ) {
                MoreContent(cardDao = cardDao, categoryColor = categoryColor)
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun Tabs(viewModel: ActivitiesViewModel = viewModel()) {
    var tabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("TODO", "Skipped")
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = derivedStateOf { if (isDarkTheme) colorSecondLight else colorSecond }
    val selectedColor = derivedStateOf { if (isDarkTheme) colorSecondLightFg else colorSecondFg }
    val unSelectedColor = derivedStateOf { selectedColor.value.copy(alpha = .5f) }
    Column(Modifier.fillMaxSize()) {
        Box(Modifier.weight(1f)) {
            when (tabIndex) {
                0 -> PopulateData(isTODO = true, viewModel)
                1 -> PopulateData(isTODO = false, viewModel)
            }
        }
        TabRow(
            selectedTabIndex = tabIndex,
            backgroundColor = backgroundColor.value,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier
                        .tabIndicatorOffset(tabPositions[tabIndex])
                        .height(0.dp)
                )
            }) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                    text = { Text(text = title, fontWeight = FontWeight.ExtraBold) },
                    selectedContentColor = selectedColor.value,
                    unselectedContentColor = unSelectedColor.value
                )
            }
        }

    }
}

@ExperimentalFoundationApi
@Composable
fun PopulateData(isTODO: Boolean = true, viewModel: ActivitiesViewModel = viewModel()) {

    val lazyActivities: LazyPagingItems<CardDao> =
        if (isTODO)
            viewModel.activitiesAdded.collectAsLazyPagingItems()
        else
            viewModel.activitiesPass.collectAsLazyPagingItems()

    val isSystemDarkTheme = isSystemInDarkTheme()


    LazyColumn(
        contentPadding = PaddingValues(5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(items = lazyActivities, key = { it.id!! }) { item ->
            item?.let { card ->


                val categoryData by derivedStateOf { CategoryMapper().toSourceFromDestination(card.type) }
                val categoryColor by derivedStateOf { if (isSystemDarkTheme) categoryData.categoryColor.colorDark else categoryData.categoryColor.colorLight }
                var leftDragValue by remember {
                    mutableStateOf(0f)
                }
                var rightDragValue by remember {
                    mutableStateOf(0f)
                }

                LaunchedEffect(key1 = true) {
                    if (viewModel.stateListMore[card.id!!] == null) {
                        viewModel.stateListMore[card.id] = mutableStateOf(false)
                    }
                }

                val isMoreVisible = viewModel.stateListMore[card.id!!] ?: mutableStateOf(false)

                val scaleFrom = .7f

                val leftScaleValue = derivedStateOf {
                    val fact = (1f - scaleFrom) * leftDragValue
                    (scaleFrom + fact).coerceIn(0f, 1f)
                }
                val rightScaleValue = derivedStateOf {
                    val fact = (1f - scaleFrom) * rightDragValue
                    (scaleFrom + fact).coerceIn(0f, 1f)
                }

                Box(
                    modifier = Modifier
                        .animateItemPlacement(
                            tween(durationMillis = 500)
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        CardSecondText(
                            modifier = Modifier
                                .padding(top = 8.dp, bottom = 8.dp, start = 10.dp)
                                .scale(rightScaleValue.value)
                                .clip(RoundedCornerShape(100.dp))
                                .background(categoryColor.colorBg.copy(alpha = rightDragValue))
                                .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
                            text = if (isTODO) "Skip" else "TODO",
                            textColor = categoryColor.colorFg.copy(alpha = rightDragValue)
                        )

                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Category Icon",
                            tint = Color.White.copy(alpha = leftDragValue),
                            modifier = Modifier
                                .scale(leftScaleValue.value)
                                .padding(top = 8.dp, bottom = 8.dp, end = 10.dp)
                                .clip(RoundedCornerShape(100.dp))
                                .background(color = Color.Red.copy(alpha = leftDragValue))
                                .padding(5.dp)
                        )
                    }
                    ActivityItem(
                        cardDao = card,
                        checkBoxVisibility = isTODO,
                        onDraggingLeft = {
                            leftDragValue = it
                        }, onDraggingRight = {
                            rightDragValue = it
                        }, onDragCompleted = { isRight ->
                            if (!isRight) {
                                viewModel.deleteActivityByID(id = card.id)
                            } else {
                                if (isTODO)
                                    viewModel.updateState(
                                        card.id,
                                        card.key,
                                        Constants.PASS_SELECTION
                                    )
                                else
                                    viewModel.updateState(
                                        card.id,
                                        card.key,
                                        Constants.ADD_SELECTION
                                    )
                            }
                        },
                        onChecked = if (isTODO) {
                            {
                                viewModel.updateIsCompleted(
                                    id = card.id,
                                    key = card.key,
                                    isCompleted = it
                                )
                            }
                        } else null,
                        onClicked = {
                            viewModel.stateListMore[card.id]?.value = !isMoreVisible.value
                        },
                        isMoreVisible = isMoreVisible
                    )
                }
            }

        }
    }
}


