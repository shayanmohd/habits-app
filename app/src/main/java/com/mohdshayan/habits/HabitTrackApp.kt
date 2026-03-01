package com.mohdshayan.habits

import android.app.Application
import com.mohdshayan.habits.data.AppDatabase
import com.mohdshayan.habits.data.HabitRepository

class HabitTrackApp : Application() {
    lateinit var repository: HabitRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val db = AppDatabase.create(this)
        repository = HabitRepository(db.habitDao(), db.dailyRecordDao())
    }
}
