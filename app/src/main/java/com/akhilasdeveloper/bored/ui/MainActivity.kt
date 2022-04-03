package com.akhilasdeveloper.bored.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.akhilasdeveloper.bored.data.CategoryColorItem
import com.akhilasdeveloper.bored.ui.screens.ActivitiesScreen
import com.akhilasdeveloper.bored.ui.screens.HomeScreen
import com.akhilasdeveloper.bored.ui.screens.about.AboutScreen
import com.akhilasdeveloper.bored.ui.screens.activities.ActivitiesViewModel
import com.akhilasdeveloper.bored.ui.screens.home.HomeViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: HomeViewModel by viewModels()
        val activitiesViewModel: ActivitiesViewModel by viewModels()
        val categoryColor = viewModel.categoryColor

        viewModel.getRandomActivity()

        setContent {

            viewModel.setIsLightTheme(!isSystemInDarkTheme())

            val systemUiController = rememberSystemUiController()
            val navController = rememberNavController()
            val animAccentColor = animateColorAsState(targetValue = categoryColor.value.colorBg)

            systemUiController.setStatusBarColor(
                color = viewModel.systemBarColor.value,
                darkIcons = viewModel.isLightColor(viewModel.systemBarColor.value)
            )

            systemUiController.setNavigationBarColor(
                color = animAccentColor.value,
                darkIcons = viewModel.isLightColor(animAccentColor.value)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(viewModel.systemBarColor.value),
                verticalArrangement = Arrangement.Bottom
            ) {

                NavHost(
                    navController = navController,
                    startDestination = BottomBarScreen.Home.route,
                    Modifier.weight(1f)
                ) {
                    composable(BottomBarScreen.Home.route) {
                        HomeScreen(viewModel = viewModel)
                    }
                    composable(BottomBarScreen.Activities.route) {
                        ActivitiesScreen(viewModel = activitiesViewModel)
                    }
                    composable(BottomBarScreen.About.route) {
                        AboutScreen(viewModel = viewModel)
                    }
                }
                BottomBar(navController = navController, categoryColor = categoryColor.value)
            }

        }
    }
}

@Composable
fun BottomBar(navController: NavHostController, categoryColor: CategoryColorItem) {
    val screens = listOf(
        BottomBarScreen.Home,
        BottomBarScreen.Activities,
        BottomBarScreen.About
    )
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val animAccentColor = animateColorAsState(targetValue = categoryColor.colorBg)
    BottomNavigation(
        backgroundColor = animAccentColor.value,
        elevation = 0.dp
    ) {
        screens.forEach { screen ->
            AddItem(
                screen = screen,
                currentDestination = currentDestination,
                navController = navController,
                colorFg = categoryColor.colorFg
            )
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomBarScreen,
    currentDestination: NavDestination?,
    navController: NavHostController,
    colorFg: Color
) {
    val animAccentFgColor = animateColorAsState(targetValue = colorFg)
    BottomNavigationItem(
        label = {
            Text(text = screen.title, fontWeight = FontWeight.ExtraBold)
        },
        icon = { Icon(imageVector = screen.icon, contentDescription = "Navigation Icon") },
        selected = currentDestination?.hierarchy?.any {
            it.route == screen.route
        } == true,
        onClick = {
            navController.navigate(screen.route) {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            }
        },
        selectedContentColor = animAccentFgColor.value,
        unselectedContentColor = animAccentFgColor.value.copy(alpha = .6f)
    )
}

