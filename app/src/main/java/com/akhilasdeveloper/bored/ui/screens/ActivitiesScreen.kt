package com.akhilasdeveloper.bored.ui.screens

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.akhilasdeveloper.bored.data.CardDao
import com.akhilasdeveloper.bored.data.mapper.CategoryMapper
import com.akhilasdeveloper.bored.ui.screens.viewmodels.ActivitiesViewModel
import com.akhilasdeveloper.bored.ui.theme.*
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.draggedItem
import org.burnoutcrew.reorderable.rememberReorderState
import org.burnoutcrew.reorderable.reorderable

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
    onChecked: ((isCompleted: Boolean) -> Unit)? = null
) {

    val isSystemDarkTheme = isSystemInDarkTheme()
    val categoryData by derivedStateOf { CategoryMapper().toSourceFromDestination(cardDao.type) }
    val categoryColor by derivedStateOf { if (isSystemDarkTheme) categoryData.categoryColor.colorDark else categoryData.categoryColor.colorLight }
    val textDecoration by derivedStateOf { if (checkBoxVisibility && cardDao.isCompleted) TextDecoration.LineThrough else TextDecoration.None }
    val backgroundColor by derivedStateOf { if (isSystemDarkTheme) colorMain else colorMainLight }
    val textColor by derivedStateOf { if (isSystemDarkTheme) colorMainFg else colorMainLightFg }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RectangleShape,
        backgroundColor = backgroundColor
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(10.dp)) {

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
                0 -> AddData(viewModel)
                1 -> PassData(viewModel)
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

@Composable
fun PassData(viewModel: ActivitiesViewModel = viewModel()) {
    val lazyActivities: LazyPagingItems<CardDao> =
        viewModel.activitiesPass.collectAsLazyPagingItems()
    LazyColumn(
        contentPadding = PaddingValues(1.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        items(items = lazyActivities, key = { it.id!! }) { item ->
            item?.let {
                ActivityItem(cardDao = it)
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun AddData(viewModel: ActivitiesViewModel = viewModel()) {

    val state = rememberReorderState()

    val lazyActivities: LazyPagingItems<CardDao> =
        viewModel.activitiesAdded.collectAsLazyPagingItems()
    LazyColumn(
        state = state.listState,
        contentPadding = PaddingValues(1.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        modifier = Modifier.reorderable(state, { from, to ->
            lazyActivities.move(from.index, to.index) { fromData, toData ->
                viewModel.updatePosition(from = fromData, to = toData)
            }
        })
    ) {
        items(items = lazyActivities, key = { it.id!! }) { item ->
            item?.let { card ->
                ActivityItem(modifier = Modifier
                    .animateItemPlacement(
                        tween(durationMillis = 500)
                    )
                    .draggedItem(state.offsetByKey(item))
                    .detectReorderAfterLongPress(state),
                    cardDao = card,
                    checkBoxVisibility = true,
                    onChecked = {
                        viewModel.updateIsCompleted(id = card.id, key = card.key, isCompleted = it)
                    })
            }
        }
    }
}

fun <T : Any> LazyPagingItems<T>.move(
    fromIdx: Int,
    toIdx: Int,
    moveData: (from: T?, to: T?) -> Unit
) {
    moveData(this[fromIdx], this[toIdx])
}

