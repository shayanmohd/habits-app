package com.mohdshayan.habits.domain

import com.mohdshayan.habits.data.HabitEntity
import java.time.DayOfWeek
import java.time.LocalDate

object FrequencyEvaluator {
    fun isRequiredOn(habit: HabitEntity, date: LocalDate): Boolean {
        val targetDay = date.toEpochDay()
        if (targetDay < habit.startEpochDay) return false

        return when (FrequencyType.valueOf(habit.frequencyType)) {
            FrequencyType.DAILY -> true
            FrequencyType.WEEKLY -> {
                val bit = dayToBit(date.dayOfWeek)
                habit.weekdayMask and bit != 0
            }
            FrequencyType.INTERVAL -> {
                val every = if (habit.intervalDays <= 0) 1 else habit.intervalDays
                val delta = targetDay - habit.startEpochDay
                delta % every == 0L
            }
        }
    }

    fun dayToBit(dayOfWeek: DayOfWeek): Int = 1 shl (dayOfWeek.value % 7)
}
