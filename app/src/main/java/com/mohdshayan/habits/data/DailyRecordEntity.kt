package com.mohdshayan.habits.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "daily_records",
    primaryKeys = ["habitId", "epochDay"],
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("habitId"), Index("epochDay")]
)
data class DailyRecordEntity(
    val habitId: Long,
    val epochDay: Long,
    val done: Boolean,
    val note: String = "",
    val updatedAtEpochMillis: Long
)
