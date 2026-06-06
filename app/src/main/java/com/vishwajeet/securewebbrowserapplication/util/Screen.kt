package com.vishwajeet.securewebbrowserapplication.util

sealed class Screen(val route: String) {
    object SignIn : Screen("signin_route")

    object Home : Screen("home_route")

    object WebView : Screen("webview_route/{url}") {
        fun passUrl(encodedUrl: String): String {
            return "webview_route/$encodedUrl"
        }
    }

    object History : Screen("history_route")
}