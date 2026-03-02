package com.mohdshayan.habits.data

import com.mohdshayan.habits.domain.FrequencyEvaluator
import com.mohdshayan.habits.domain.HabitDayCell
import com.mohdshayan.habits.domain.HabitDayStatus
import com.mohdshayan.habits.domain.HabitTodayItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class HabitRepository(
    private val habitDao: HabitDao,
    private val recordDao: DailyRecordDao
) {
    fun observeAllHabits(): Flow<List<HabitEntity>> = habitDao.observeActiveHabits()

    fun observeTodayHabits(today: LocalDate = LocalDate.now()): Flow<List<HabitTodayItem>> {
        val day = today.toEpochDay()
        return combine(
            habitDao.observeActiveHabits(),
            recordDao.observeRecordsInRange(day, day)
        ) { habits, records ->
            val doneMap = records.associateBy { it.habitId }
            habits.asSequence()
                .filter { FrequencyEvaluator.isRequiredOn(it, today) }
                .map {
                    HabitTodayItem(
                        id = it.id,
                        name = it.name,
                        notes = it.notes,
                        done = doneMap[it.id]?.done == true
                    )
                }
                .toList()
        }
    }

    fun observeHabit(habitId: Long): Flow<HabitEntity?> = habitDao.observeHabit(habitId)

    fun observeHabitCalendar(habitId: Long): Flow<List<DailyRecordEntity>> =
        recordDao.observeRecordsForHabit(habitId)

    suspend fun addHabit(habit: HabitEntity): Long = habitDao.insert(habit)

    suspend fun deleteHabit(habitId: Long) = habitDao.deleteById(habitId)

    suspend fun setDone(habitId: Long, date: LocalDate, done: Boolean) {
        recordDao.upsert(
            DailyRecordEntity(
                habitId = habitId,
                epochDay = date.toEpochDay(),
                done = done,
                updatedAtEpochMillis = System.currentTimeMillis()
            )
        )
    }

    fun observeWidgetSummary(today: LocalDate = LocalDate.now()): Flow<Pair<Int, Int>> {
        return observeTodayHabits(today).map { items ->
            val done = items.count { it.done }
            done to items.size
        }
    }

    fun buildMonthCells(
        habit: HabitEntity,
        records: List<DailyRecordEntity>,
        month: LocalDate
    ): List<HabitDayCell> {
        val first = month.withDayOfMonth(1)
        val last = month.withDayOfMonth(month.lengthOfMonth())
        val today = LocalDate.now()
        val recordMap = records.associateBy { it.epochDay }

        return generateSequence(first) { current ->
            if (current.isBefore(last)) current.plusDays(1) else null
        }.map { date ->
            val epoch = date.toEpochDay()
            val required = FrequencyEvaluator.isRequiredOn(habit, date)
            val done = recordMap[epoch]?.done == true

            val status = when {
                !required -> HabitDayStatus.NOT_REQUIRED
                done -> HabitDayStatus.DONE
                date.isBefore(today) || date == today -> HabitDayStatus.MISSED
                else -> HabitDayStatus.NOT_REQUIRED
            }

            HabitDayCell(date, status)
        }.toList()
    }
}
