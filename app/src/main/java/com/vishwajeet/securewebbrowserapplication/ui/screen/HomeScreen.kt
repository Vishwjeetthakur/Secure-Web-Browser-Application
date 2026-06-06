package com.vishwajeet.securewebbrowserapplication.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.vishwajeet.securewebbrowserapplication.R
import com.vishwajeet.securewebbrowserapplication.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToWeb: (String) -> Unit,
    onNavigateToHistory: () -> Unit,
    onLogoutTrigger: () -> Unit
) {
    val uiState = viewModel.homeUiState
    val context = LocalContext.current

    val staticCarouselImages = remember {
        listOf(
            R.drawable.images,
            R.drawable.food_2,
            R.drawable.skycolor,
            )
    }

    val pagerState = rememberPagerState(pageCount = { staticCarouselImages.size })

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("WebToNative", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("by Orufy", fontSize = 11.sp, color = Color.LightGray)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(painterResource(R.drawable.baseline_history_24), contentDescription = "View History Stack")
                    }
                    IconButton(onClick = onLogoutTrigger) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout App Session")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1F2937),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF24292F))
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { pageIndex ->
                    AsyncImage(
                        model = staticCarouselImages[pageIndex],
                        contentDescription = "Showcase Asset Graphic Slide",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }


                Row(
                    modifier = Modifier
                        .height(32.dp)
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.4f)),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(staticCarouselImages.size) { indexMarker ->
                        val trackActiveTint = if (pagerState.currentPage == indexMarker) Color(0xFF4F46E5) else Color.LightGray
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(trackActiveTint)
                        )
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "NATIVE UI BUILDER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4F46E5),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Design native screens, no code",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Compose splash, tabs and menus with a visual editor.",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Website URL",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )

                OutlinedTextField(
                    value = uiState.urlInput,
                    onValueChange = { currentString ->
                        viewModel.onUrlInputStringChanged(currentString)
                    },
                    placeholder = { Text("https://yourwebsite.com") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4F46E5),
                        cursorColor = Color(0xFF4F46E5)
                    )
                )

                Text(
                    text = "We'll wrap it into a native Android & iOS app.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.validateAndProcessUrlRoute { validatedSecureUrl ->
                        onNavigateToWeb(validatedSecureUrl)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
            ) {
                Text(
                    text = "Open App →",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}