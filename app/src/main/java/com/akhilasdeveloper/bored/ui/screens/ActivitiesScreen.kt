package com.akhilasdeveloper.bored.ui.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.akhilasdeveloper.bored.data.CardDao
import com.akhilasdeveloper.bored.data.mapper.CategoryMapper
import com.akhilasdeveloper.bored.ui.screens.viewmodels.ActivitiesViewModel
import com.akhilasdeveloper.bored.ui.theme.*

@Composable
fun ActivitiesScreen(viewModel: ActivitiesViewModel = viewModel()) {
    Box(modifier = Modifier.fillMaxSize()) {
        Tabs(viewModel = viewModel)
    }
}

@Composable
fun ActivityItem(cardDao: CardDao) {

    val isSystemDarkTheme = isSystemInDarkTheme()
    val categoryData by derivedStateOf { CategoryMapper().toSourceFromDestination(cardDao.type) }
    val categoryColor by derivedStateOf { if (isSystemDarkTheme) categoryData.categoryColor.colorDark else categoryData.categoryColor.colorLight }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RectangleShape,
        backgroundColor = if (isSystemDarkTheme) colorMain else colorMainLight
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = categoryData.icon,
                contentDescription = "Category Icon",
                Modifier.padding(start = 18.dp, top = 18.dp, bottom = 18.dp),
                tint = categoryColor.colorBg
            )
            CardSecondText(
                modifier = Modifier.padding(18.dp),
                text = cardDao.activityName,
                textColor = if (isSystemDarkTheme) colorMainFg else colorMainLightFg,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
fun Tabs(viewModel: ActivitiesViewModel = viewModel()) {
    var tabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("Added Data", "Pass Data")
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
                    Modifier.tabIndicatorOffset(tabPositions[tabIndex]).height(0.dp)
                )
            }) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                    text = { Text(text = title, fontWeight = FontWeight.Bold) },
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
        items(items = lazyActivities, key = { it.key }) { item ->
            item?.let {
                ActivityItem(cardDao = it)
            }
        }
    }
}

@Composable
fun AddData(viewModel: ActivitiesViewModel = viewModel()) {
    val lazyActivities: LazyPagingItems<CardDao> =
        viewModel.activitiesAdded.collectAsLazyPagingItems()
    LazyColumn(
        contentPadding = PaddingValues(1.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        items(items = lazyActivities, key = { it.key }) { item ->
            item?.let {
                ActivityItem(cardDao = it)
            }
        }
    }
}

