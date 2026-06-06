package com.vishwajeet.securewebbrowserapplication.ui.screen

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vishwajeet.securewebbrowserapplication.data.local.HistoryEntity
import com.vishwajeet.securewebbrowserapplication.ui.viewmodel.BrowserViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: BrowserViewModel,
    onBackPressTrigger: () -> Unit
) {
    val historyLogsList by viewModel.historyStateFlow.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Browsing History", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackPressTrigger) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Return Back to Home")
                    }
                },
                actions = {
                    if (historyLogsList.isNotEmpty()) {
                        IconButton(onClick = { viewModel.purgeCompleteHistoryLogs() }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Purge All Data Metrics Logs",
                                tint = Color(0xFFEF4444)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1F2937),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPaddingConstraints ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPaddingConstraints)
                .background(Color(0xFFF3F4F6))
        ) {
            if (historyLogsList.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No history records found", fontSize = 16.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                    Text("Websites you visit will appear here.", fontSize = 12.sp, color = Color.LightGray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = historyLogsList,
                        key = { historyItem -> historyItem.id } // Performance mapping key line
                    ) { item ->
                        HistoryCardRowItem(historyEntry = item)
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryCardRowItem(historyEntry: HistoryEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = historyEntry.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Box(
                    modifier = Modifier
                        .background(Color(0xFFEEF2FF), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Visited ${historyEntry.visitCount} ${if (historyEntry.visitCount > 1) "times" else "time"}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF4F46E5)
                    )
                }
            }


            Text(
                text = historyEntry.url,
                fontSize = 13.sp,
                color = Color(0xFF6B7280),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Divider(modifier = Modifier.padding(vertical = 4.dp), color = Color(0xFFF3F4F6))

            Text(
                text = "Last visited: ${getFormattedDateTimeLabel(historyEntry.lastVisitedTime)}",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

private fun getFormattedDateTimeLabel(timestampEpoch: Long): String {
    val calendarInstance = Calendar.getInstance().apply { timeInMillis = timestampEpoch }
    return DateFormat.format("dd MMM yyyy, hh:mm a", calendarInstance).toString()
}
