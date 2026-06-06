package com.vishwajeet.securewebbrowserapplication.ui.screen

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.vishwajeet.securewebbrowserapplication.ui.viewmodel.BrowserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(
    initialTargetUrl: String,
    viewModel: BrowserViewModel,
    onReturnHomeAction: (shouldClearField: Boolean) -> Unit
) {
    val uiState = viewModel.webViewUiState
    var localWebViewInstanceRef by remember { mutableStateOf<WebView?>(null) }

    BackHandler {
        if (localWebViewInstanceRef?.canGoBack() == true) {
            localWebViewInstanceRef?.goBack()
        } else {
            onReturnHomeAction(false)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (uiState.webPageTitle.isBlank()) "Loading..." else uiState.webPageTitle,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = uiState.currentUrl.ifBlank { initialTargetUrl },
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (localWebViewInstanceRef?.canGoBack() == true) { localWebViewInstanceRef?.goBack() }
                        else { onReturnHomeAction(false) }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Page Level Back Pipeline Control")
                    }
                },
                actions = {
                    IconButton(onClick = { onReturnHomeAction(true) }) {
                        Icon(Icons.Default.Close, contentDescription = "Kill WebView Container Frame")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1F2937),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { innerPaddingConstraints ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPaddingConstraints)
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                url?.let { viewModel.onWebPageMetadataReceived(it, view?.title ?: "") }
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                url?.let { viewModel.onWebPageMetadataReceived(it, view?.title ?: "") }
                            }

                            override fun onReceivedError(
                                view: WebView?,
                                request: WebResourceRequest?,
                                error: WebResourceError?
                            ) {
                                super.onReceivedError(view, request, error)
                                if (request?.isForMainFrame == true) {
                                    viewModel.triggerNetworkErrorState(error?.description?.toString() ?: "Connection Broken Context.")
                                }
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                super.onProgressChanged(view, newProgress)
                                viewModel.updateWebProgressState(newProgress)
                            }
                        }

                        loadUrl(initialTargetUrl)
                        localWebViewInstanceRef = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            if (uiState.isLoadingActive) {
                LinearProgressIndicator(
                    progress = { uiState.loadingProgress / 100f },
                    modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
                    color = Color(0xFF4F46E5),
                    trackColor = Color.Transparent
                )
            }

            uiState.networkErrorMessage?.let { errorMessage ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No Internet Connection", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(errorMessage, fontSize = 14.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}