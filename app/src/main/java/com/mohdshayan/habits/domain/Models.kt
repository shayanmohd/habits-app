package com.mohdshayan.habits.domain

import java.time.LocalDate

enum class HabitDayStatus {
    DONE,
    MISSED,
    NOT_REQUIRED
}

data class HabitTodayItem(
    val id: Long,
    val name: String,
    val notes: String,
    val done: Boolean
)

data class HabitDayCell(
    val date: LocalDate,
    val status: HabitDayStatus
)
