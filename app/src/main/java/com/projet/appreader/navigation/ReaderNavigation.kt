package com.projet.appreader.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.projet.appreader.screens.ReaderSplashScreen
import com.projet.appreader.screens.details.ReaderBookDetailScreen
import com.projet.appreader.screens.home.Home
import com.projet.appreader.screens.home.HomeViewModel
import com.projet.appreader.screens.login.ReaderLoginScreen
import com.projet.appreader.screens.search.BookSearchViewModel
import com.projet.appreader.screens.search.ReaderSearchScreen
import com.projet.appreader.screens.stats.ReaderStatsScreen
import com.projet.appreader.screens.update.ReaderUpdateScreen

@Composable
fun ReaderNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = ReaderScreens.SplashScreen.name
    ){
        composable(ReaderScreens.SplashScreen.name){
            ReaderSplashScreen(navController = navController)
        }
        composable(ReaderScreens.ReaderHomeScreen.name){
            val vieWmodel = hiltViewModel<HomeViewModel>()
            Home(navController = navController,vieWmodel= vieWmodel)
        }

        val detailName = ReaderScreens.DetailScreen.name
        composable("$detailName/{bookId}",
            arguments = listOf(navArgument("bookId"){
                type = NavType.StringType
            })
        ){ backStackEntry ->
            backStackEntry.arguments?.getString("bookId").let {
                ReaderBookDetailScreen(navController = navController, bookId= it.toString())
            }

        }
        composable(ReaderScreens.LoginScreen.name){
            ReaderLoginScreen(navController = navController)
        }
        composable(ReaderScreens.SearchScreen.name){
            val vieWmodel = hiltViewModel<BookSearchViewModel>()
            ReaderSearchScreen(navController = navController,vieWmodel)
        }
        composable(ReaderScreens.ReaderStatsScreen.name){
            val vieWmodel2 = hiltViewModel<HomeViewModel>()
            ReaderStatsScreen(navController = navController, homeViewmodel= vieWmodel2)
        }
        val updName = ReaderScreens.UpdateScreen.name
        composable("$updName/{bookItemId}",
            arguments = listOf(navArgument("bookItemId"){
                type = NavType.StringType
            })
        ){ backStackEntry ->
            backStackEntry.arguments?.getString("bookItemId").let {
                ReaderUpdateScreen(navController = navController, bookId= it.toString())
            }

        }
    }

}