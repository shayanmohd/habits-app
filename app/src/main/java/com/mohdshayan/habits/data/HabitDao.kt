package com.mohdshayan.habits.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(habit: HabitEntity): Long

    @Query("SELECT * FROM habits WHERE archived = 0 ORDER BY createdAtEpochMillis DESC")
    fun observeActiveHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE id = :habitId LIMIT 1")
    fun observeHabit(habitId: Long): Flow<HabitEntity?>

    @Query("DELETE FROM habits WHERE id = :habitId")
    suspend fun deleteById(habitId: Long)
}
