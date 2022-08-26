package com.movie.app.homeScreen

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.movie.app.R

sealed class NavigationItem(var route: String, var icon: Int, var title: String) {
    object Movies : NavigationItem("movies", R.drawable.ic_movie, "Movies")
    object Favorites : NavigationItem("favorites", R.drawable.ic_fav, "Favorites")
    object Watchlist : NavigationItem("watchlist", R.drawable.ic_later, "Watchlist")
}

@Composable
fun NavHostController.HomeUI() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        content = {
            Navigation(navController = navController)
        },
        backgroundColor = Color.White
    )
}

@Composable
fun BottomNavigationBar(navController: NavController) {

    val items = listOf(
        NavigationItem.Movies,
        NavigationItem.Favorites,
        NavigationItem.Watchlist
    )

    BottomNavigation(
        backgroundColor = Color.DarkGray,
        contentColor = Color.White
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.title) },
                label = { Text(text = item.title) },
                selectedContentColor = Color.White,
                unselectedContentColor = Color.White.copy(0.4f),
                alwaysShowLabel = true,
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController, startDestination = NavigationItem.Movies.route) {
        composable(NavigationItem.Movies.route) {
            MovieUI()
        }
        composable(NavigationItem.Favorites.route) {
            Text(text = "Favorites")
        }
        composable(NavigationItem.Watchlist.route) {
            Text(text = "Watchlist")
        }
    }
}