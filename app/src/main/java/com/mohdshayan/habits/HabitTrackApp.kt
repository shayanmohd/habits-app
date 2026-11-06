package com.mohdshayan.habits

import android.app.Application
import androidx.glance.appwidget.updateAll
import com.mohdshayan.habits.data.AppDatabase
import com.mohdshayan.habits.data.HabitRepository
import com.mohdshayan.habits.widget.HabitsSummaryWidget

class HabitTrackApp : Application() {
    lateinit var repository: HabitRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val db = AppDatabase.create(this)
        repository = HabitRepository(db.habitDao(), db.dailyRecordDao()) {
            HabitsSummaryWidget().updateAll(this)
        }
    }
}
