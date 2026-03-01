package com.mohdshayan.habits.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.mohdshayan.habits.R
import com.mohdshayan.habits.data.AppDatabase
import com.mohdshayan.habits.data.HabitRepository
import kotlinx.coroutines.flow.first

class HabitsSummaryWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: androidx.glance.GlanceId) {
        val db = AppDatabase.create(context)
        val repo = HabitRepository(db.habitDao(), db.dailyRecordDao())
        val (done, total) = repo.observeWidgetSummary().first()

        provideContent {
            WidgetContent(done = done, total = total)
        }
    }
}

@Composable
private fun WidgetContent(done: Int, total: Int) {
    Column(
        modifier = GlanceModifier
            .background(ColorProvider(0xFFF9F9F7.toInt()))
            .padding(16.dp)
    ) {
        Text(
            text = androidx.glance.LocalContext.current.getString(R.string.widget_title),
            style = TextStyle(color = ColorProvider(0xFF1C1C1C.toInt()), fontSize = 20.sp)
        )
        Text(
            text = androidx.glance.LocalContext.current.getString(R.string.widget_done, done),
            style = TextStyle(color = ColorProvider(0xFF1C1C1C.toInt()), fontSize = 16.sp)
        )
        Text(
            text = androidx.glance.LocalContext.current.getString(R.string.widget_required, total),
            style = TextStyle(color = ColorProvider(0xFF1C1C1C.toInt()), fontSize = 16.sp)
        )
    }
}
