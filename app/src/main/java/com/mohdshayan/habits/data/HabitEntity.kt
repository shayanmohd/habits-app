package com.mohdshayan.habits.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val notes: String,
    val frequencyType: String,
    val weekdayMask: Int,
    val intervalDays: Int,
    val startEpochDay: Long,
    val createdAtEpochMillis: Long,
    val archived: Boolean = false
)
