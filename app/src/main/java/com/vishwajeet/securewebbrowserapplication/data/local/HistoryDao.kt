package com.vishwajeet.securewebbrowserapplication.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM browsing_history WHERE url = :targetUrl LIMIT 1")
    suspend fun getRecordByUrl(targetUrl: String): HistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateRecord(entity: HistoryEntity)

    @Query("SELECT * FROM browsing_history ORDER BY lastVisitedTime DESC")
    fun observeAllHistorySorted(): Flow<List<HistoryEntity>>

    @Query("DELETE FROM browsing_history")
    suspend fun clearCompleteHistory()
}