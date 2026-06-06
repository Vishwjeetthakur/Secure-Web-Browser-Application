package com.vishwajeet.securewebbrowserapplication.data.repository

import com.vishwajeet.securewebbrowserapplication.data.local.HistoryDao
import com.vishwajeet.securewebbrowserapplication.data.local.HistoryEntity
import kotlinx.coroutines.flow.Flow

class BrowserRepository(private val historyDao: HistoryDao) {

    val allHistoryFlow: Flow<List<HistoryEntity>> = historyDao.observeAllHistorySorted()

    suspend fun addUrlToBrowsingHistory(targetUrl: String, webpageTitle: String) {
        val existingRecord = historyDao.getRecordByUrl(targetUrl)

        if (existingRecord != null) {
            val updatedRecord = existingRecord.copy(
                visitCount = existingRecord.visitCount + 1,
                lastVisitedTime = System.currentTimeMillis(),
                title = webpageTitle.ifBlank { existingRecord.title }
            )
            historyDao.insertOrUpdateRecord(updatedRecord)
        } else {
            val newRecord = HistoryEntity(
                url = targetUrl,
                title = webpageTitle.ifBlank { targetUrl },
                visitCount = 1,
                lastVisitedTime = System.currentTimeMillis()
            )
            historyDao.insertOrUpdateRecord(newRecord)
        }
    }

    suspend fun clearAllBrowsingHistory() {
        historyDao.clearCompleteHistory()
    }
}