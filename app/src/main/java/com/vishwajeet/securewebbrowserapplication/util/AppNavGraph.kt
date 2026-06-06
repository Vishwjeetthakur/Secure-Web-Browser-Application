package com.vishwajeet.securewebbrowserapplication.util


import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.vishwajeet.securewebbrowserapplication.data.local.HistoryDatabase
import com.vishwajeet.securewebbrowserapplication.data.repository.BrowserRepository
import com.vishwajeet.securewebbrowserapplication.ui.screen.HistoryScreen
import com.vishwajeet.securewebbrowserapplication.ui.screen.HomeScreen
import com.vishwajeet.securewebbrowserapplication.ui.screen.SignInScreen
import com.vishwajeet.securewebbrowserapplication.ui.screen.WebViewScreen
import com.vishwajeet.securewebbrowserapplication.ui.viewmodel.AuthViewModel
import com.vishwajeet.securewebbrowserapplication.ui.viewmodel.BrowserViewModel
import com.vishwajeet.securewebbrowserapplication.ui.viewmodel.HomeViewModel
import com.vishwajeet.securewebbrowserapplication.ui.viewmodel.ViewModelFactory
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
) {
    val context = LocalContext.current
    val database = HistoryDatabase.getDatabase(context)
    val application = context.applicationContext as Application
val homeViewModel: HomeViewModel = viewModel()
val repository = BrowserRepository(database.historyDao())
    val browserViewModel: BrowserViewModel = viewModel(
        factory = ViewModelFactory(application,repository)
    )

    NavHost(
        navController = navController,
        startDestination = Screen.SignIn.route
    ) {
        composable(route = Screen.SignIn.route) {
            SignInScreen(
                viewModel = authViewModel,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Home.route) {
            val routingContext = LocalContext.current

            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToWeb = { verifiedUrlString ->
                    val encodedPath = URLEncoder.encode(verifiedUrlString, StandardCharsets.UTF_8.toString())
                    navController.navigate(Screen.WebView.passUrl(encodedPath))
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                },
                onLogoutTrigger = {
                    authViewModel.logOut(routingContext)
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.WebView.route,
            arguments = listOf(
                navArgument("url") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val encodedUrl = backStackEntry.arguments?.getString("url") ?: ""
            val targetUrl = URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8.toString())
            WebViewScreen(
                initialTargetUrl = targetUrl,
                viewModel = browserViewModel,
                onReturnHomeAction = { shouldClearField ->
                    if (shouldClearField) {
                        homeViewModel.clearUrlInputFieldContent()
                    }
                    navController.popBackStack()
                }
            )

        }

        composable(route = Screen.History.route) {

            HistoryScreen(
                viewModel = browserViewModel,
                onBackPressTrigger = {
                    navController.popBackStack()
                }
            )
        }
    }
}