package com.akhilasdeveloper.bored.ui.screens.activities

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.ExperimentalComposeUiApi
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
import androidx.paging.compose.itemsIndexed
import com.akhilasdeveloper.bored.data.CardDao
import com.akhilasdeveloper.bored.data.mapper.CategoryValueMapper
import com.akhilasdeveloper.bored.ui.screens.home.CardSecondText
import com.akhilasdeveloper.bored.ui.screens.home.DemoDialog
import com.akhilasdeveloper.bored.ui.screens.home.HomeViewModel
import com.akhilasdeveloper.bored.ui.screens.home.MoreContent
import com.akhilasdeveloper.bored.ui.theme.*
import com.akhilasdeveloper.bored.util.Constants
import kotlin.math.roundToInt

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun ActivitiesScreen(viewModel: ActivitiesViewModel = viewModel(), homeViewModel: HomeViewModel = viewModel()) {
    Box(modifier = Modifier.fillMaxSize()) {
        Tabs(viewModel = viewModel, homeViewModel = homeViewModel)
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

    val categoryData by derivedStateOf { CategoryValueMapper.toSourceFromDestination(cardDao.type) }
    val categoryColor by derivedStateOf { if (isSystemDarkTheme) categoryData.categoryColor.colorDark else categoryData.categoryColor.colorLight }
    val textDecoration by derivedStateOf { if (checkBoxVisibility && cardDao.isCompleted) TextDecoration.LineThrough else TextDecoration.None }

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
                when {
                    offset <= cardOffset -> {
                        (offset.coerceIn(
                            0f,
                            cardOffset
                        ) / cardOffset).coerceIn(0f, 1f)
                    }
                    offset >= cardOffset -> {
                        (1f - ((offset.coerceIn(
                            cardOffset,
                            screenWidth
                        ) - cardOffset) / (screenWidth - cardOffset))).coerceIn(0f, 1f)
                    }
                    else -> {
                        1f
                    }
                }
            )
        }
        if (offset < 0) {
            onDraggingLeft?.invoke(
                when {
                    offset >= -cardOffset -> {
                        ((-offset).coerceIn(
                            1f,
                            cardOffset
                        ) / cardOffset).coerceIn(0f, 1f)
                    }
                    offset <= -cardOffset -> {
                        (1f - (((-offset).coerceIn(
                            cardOffset,
                            screenWidth
                        ) - cardOffset) / (screenWidth - cardOffset))).coerceIn(0f, 1f)
                    }
                    else -> {
                        1f
                    }
                }
            )
        }
    }


    Card(
        border = BorderStroke(2.dp, if (isMoreVisible.value) categoryColor else Color.Transparent),
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 1.dp,
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                indication = rememberRipple(color = MaterialTheme.colors.onSurface),
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
        shape = RectangleShape
    ) {
        Column {

            Card(
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 1.dp,
                modifier = modifier
                    .fillMaxWidth()
            ) {

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
                                checkedColor = Color.Transparent,
                                uncheckedColor = MaterialTheme.colors.onSurface,
                                checkmarkColor = categoryColor
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
                        tint = onAccentColor,
                        modifier = Modifier
                            .padding(top = 8.dp, bottom = 8.dp, start = 10.dp)
                            .clip(RoundedCornerShape(100.dp))
                            .background(color = categoryColor)
                            .padding(5.dp)
                    )

                    Text(
                        modifier = Modifier
                            .padding(
                                top = 8.dp,
                                bottom = 8.dp,
                                start = 12.dp
                            ),
                        text = cardDao.activityName!!,
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.onSurface,
                        textDecoration = textDecoration
                    )

                }
            }
            AnimatedVisibility(
                visible = isMoreVisible.value,
                enter = expandVertically(animationSpec = tween(durationMillis = 500)),
                exit = shrinkVertically(animationSpec = tween(durationMillis = 500))
            ) {
                MoreContent(cardDao = cardDao, accent = categoryColor)
            }
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun Tabs(viewModel: ActivitiesViewModel = viewModel(), homeViewModel: HomeViewModel = viewModel()) {
    var tabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("TODO", "Skipped")

    Column(Modifier.fillMaxSize()) {
        Box(Modifier.weight(1f)) {
            when (tabIndex) {
                0 -> PopulateData(isTODO = true, viewModel, homeViewModel = homeViewModel)
                1 -> PopulateData(isTODO = false, viewModel, homeViewModel = homeViewModel)
            }
        }
        TabRow(
            selectedTabIndex = tabIndex,
            backgroundColor = MaterialTheme.colors.onSecondary,
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
                    selectedContentColor =  MaterialTheme.colors.secondary,
                    unselectedContentColor = MaterialTheme.colors.secondary.copy(alpha = .5f)
                )
            }
        }

    }
}

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun PopulateData(isTODO: Boolean = true, viewModel: ActivitiesViewModel = viewModel(), homeViewModel: HomeViewModel = viewModel()) {

    val lazyActivities: LazyPagingItems<CardDao> =
        if (isTODO)
            viewModel.activitiesAdded.collectAsLazyPagingItems()
        else
            viewModel.activitiesPass.collectAsLazyPagingItems()

    val isActivityCardSwipeTriedIsShowing = remember { mutableStateOf(false) }

    DemoDialog(
        title = "Tip",
        description = Constants.ACTIVITY_CARD_SWIPE_TRIED,
        rangeExpanded = isActivityCardSwipeTriedIsShowing,
        foregroundColor = MaterialTheme.colors.surface,
        backgroundColor = MaterialTheme.colors.onSurface
    ) {

    }

    LazyColumn(
        contentPadding = PaddingValues(5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {

        itemsIndexed(items = lazyActivities, key = {index, item -> item.id!! }) { index, item ->
            item?.let { card ->

                if (index == 0 && !homeViewModel.isActivityCardSwipeTriedIsShowing.value){
                    isActivityCardSwipeTriedIsShowing.value = true
                }

                val categoryData by derivedStateOf {
                    CategoryValueMapper.toSourceFromDestination(
                        card.type
                    )
                }

                val isComplete by derivedStateOf { isTODO && card.isCompleted }

                val categoryColor by derivedStateOf { if (!homeViewModel.isLightTheme.value) categoryData.categoryColor.colorDark else categoryData.categoryColor.colorLight }
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
                        .background(MaterialTheme.colors.background)
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

                        if (isComplete) {
                            Icon(
                                imageVector = Icons.Rounded.Delete,
                                contentDescription = "Category Icon",
                                tint = Color.White.copy(alpha = rightDragValue),
                                modifier = Modifier
                                    .scale(rightScaleValue.value)
                                    .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(color = Color.Red.copy(alpha = rightDragValue))
                                    .padding(5.dp)
                            )
                        } else {

                            Text(
                                modifier = Modifier
                                    .padding(top = 8.dp, bottom = 8.dp, start = 10.dp)
                                    .scale(rightScaleValue.value)
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(categoryColor.copy(alpha = rightDragValue))
                                    .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
                                text = if (isTODO) "Skip" else "TODO",
                                style = MaterialTheme.typography.subtitle1,
                                color = onAccentColor.copy(alpha = rightDragValue)
                            )

                        }


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
                            homeViewModel.setIsActivityCardSwipeTriedIsShowing()
                            if (!isRight || isComplete) {
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


