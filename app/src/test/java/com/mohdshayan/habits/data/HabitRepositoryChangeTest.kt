package com.mohdshayan.habits.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

/** The home-screen widget is refreshed through onDataChanged, so every write must call it. */
class HabitRepositoryChangeTest {
    private val habitDao = object : HabitDao {
        override suspend fun insert(habit: HabitEntity): Long = 1
        override fun observeActiveHabits(): Flow<List<HabitEntity>> = flowOf(emptyList())
        override fun observeHabit(habitId: Long): Flow<HabitEntity?> = flowOf(null)
        override suspend fun deleteById(habitId: Long) {}
    }
    private val recordDao = object : DailyRecordDao {
        override suspend fun upsert(record: DailyRecordEntity) {}
        override fun observeRecordsForHabit(habitId: Long): Flow<List<DailyRecordEntity>> = flowOf(emptyList())
        override fun observeRecordsInRange(startDay: Long, endDay: Long): Flow<List<DailyRecordEntity>> = flowOf(emptyList())
    }

    private var changes = 0
    private val repo = HabitRepository(habitDao, recordDao, onDataChanged = { changes++ })

    @Test
    fun toggleNotifiesChange() = runBlocking {
        repo.setDone(1, LocalDate.now(), true)
        assertEquals(1, changes)
    }

    @Test
    fun addNotifiesChange() = runBlocking {
        repo.addHabit(HabitEntity(name = "read", notes = "", frequencyType = "DAILY", weekdayMask = 127,
            intervalDays = 1, startEpochDay = 0, createdAtEpochMillis = 0))
        assertEquals(1, changes)
    }

    @Test
    fun deleteNotifiesChange() = runBlocking {
        repo.deleteHabit(1)
        assertEquals(1, changes)
    }
}
