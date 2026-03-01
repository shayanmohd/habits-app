package com.mohdshayan.habits.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(record: DailyRecordEntity)

    @Query("SELECT * FROM daily_records WHERE habitId = :habitId")
    fun observeRecordsForHabit(habitId: Long): Flow<List<DailyRecordEntity>>

    @Query("SELECT * FROM daily_records WHERE epochDay BETWEEN :startDay AND :endDay")
    fun observeRecordsInRange(startDay: Long, endDay: Long): Flow<List<DailyRecordEntity>>
}
