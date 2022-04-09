package com.akhilasdeveloper.bored.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.akhilasdeveloper.bored.data.mapper.ThemeValueMapper
import com.akhilasdeveloper.bored.ui.screens.activities.ActivitiesScreen
import com.akhilasdeveloper.bored.ui.screens.home.HomeScreen
import com.akhilasdeveloper.bored.ui.screens.about.AboutScreen
import com.akhilasdeveloper.bored.ui.screens.activities.ActivitiesViewModel
import com.akhilasdeveloper.bored.ui.screens.home.HomeViewModel
import com.akhilasdeveloper.bored.ui.theme.BoredTheme
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

        viewModel.getRandomActivity()

        setContent {

            val categoryTypeItem = viewModel.categoryTypeItem
            val theme = viewModel.currentTheme.collectAsState(
                initial = ThemeValueMapper.getSystemInDarkValueFromBoolean(
                    isSystemInDarkTheme()
                )
            )

            val  isSystemInDarkTheme = isSystemInDarkTheme()

            val isDarkTheme = derivedStateOf { ThemeValueMapper.getSystemInDarkValueFromConst(theme.value) ?: isSystemInDarkTheme }

            BoredTheme(categoryTheme = categoryTypeItem,
            darkTheme = isDarkTheme.value) {

                viewModel.setIsLightTheme(!isSystemInDarkTheme())

                val systemUiController = rememberSystemUiController()
                val navController = rememberNavController()

                systemUiController.setStatusBarColor(
                    color = MaterialTheme.colors.background,
                    darkIcons = viewModel.isLightColor(MaterialTheme.colors.background)
                )

                systemUiController.setNavigationBarColor(
                    color = MaterialTheme.colors.primary,
                    darkIcons = viewModel.isLightColor(MaterialTheme.colors.primary)
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.background),
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
                    BottomBar(navController = navController)
                }
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val screens = listOf(
        BottomBarScreen.Home,
        BottomBarScreen.Activities,
        BottomBarScreen.About
    )
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    BottomNavigation(
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.onPrimary
    ) {
        screens.forEach { screen ->
            AddItem(
                screen = screen,
                currentDestination = currentDestination,
                navController = navController
            )
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomBarScreen,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
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
        selectedContentColor = MaterialTheme.colors.onPrimary,
        unselectedContentColor = MaterialTheme.colors.onPrimary.copy(alpha = .6f)
    )
}

