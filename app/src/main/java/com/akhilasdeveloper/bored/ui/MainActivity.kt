package com.akhilasdeveloper.bored.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.akhilasdeveloper.bored.ui.screens.Greeting
import com.akhilasdeveloper.bored.ui.theme.colorMain
import com.akhilasdeveloper.bored.ui.theme.colorMainLight
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: MainViewModel by viewModels()

        viewModel.getRandomActivity()

        setContent {

            viewModel.setIsLightTheme(!isSystemInDarkTheme())

            val systemUiController = rememberSystemUiController()
            val navController = rememberNavController()

            systemUiController.setStatusBarColor(
                color = viewModel.systemBarColor.value,
                darkIcons = viewModel.isLightColor(viewModel.systemBarColor.value)
            )

            systemUiController.setNavigationBarColor(
                color = viewModel.systemBarSecondColor.value,
                darkIcons = viewModel.isLightColor(viewModel.systemBarSecondColor.value)
            )


            Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(if (isSystemInDarkTheme()) colorMain else colorMainLight)
                ) {

                    NavHost(
                        navController = navController,
                        startDestination = BottomBarScreen.Home.route,
                        Modifier.weight(1f)
                    ) {
                        composable(BottomBarScreen.Home.route) {
                            Greeting(viewModel = viewModel)
                        }
                        composable(BottomBarScreen.Activities.route) {

                        }
                        composable(BottomBarScreen.About.route) {

                        }
                    }
                    BottomBar(navController = navController, viewModel = viewModel)
                }

        }
    }
}

@Composable
fun BottomBar(navController: NavHostController, viewModel: MainViewModel) {
    val screens = listOf(
        BottomBarScreen.Home,
        BottomBarScreen.Activities,
        BottomBarScreen.About
    )
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    BottomNavigation(
        backgroundColor = viewModel.systemBarSecondColor.value
    ) {
        screens.forEach { screen ->
            AddItem(
                screen = screen,
                currentDestination = currentDestination,
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomBarScreen,
    currentDestination: NavDestination?,
    navController: NavHostController,
    viewModel: MainViewModel
) {
    BottomNavigationItem(
        label = {
            Text(text = screen.title)
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
        selectedContentColor = viewModel.systemBarSecondColorFg.value,
        unselectedContentColor = viewModel.systemBarSecondColorFg.value.copy(alpha = .5f)
    )
}

