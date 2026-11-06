package com.mohdshayan.habits.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.mohdshayan.habits.HabitTrackApp
import com.mohdshayan.habits.R
import com.mohdshayan.habits.ui.theme.BgPaper
import com.mohdshayan.habits.ui.theme.TextPrimary
import kotlinx.coroutines.flow.first

class HabitsSummaryWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: androidx.glance.GlanceId) {
        val repo = (context.applicationContext as HabitTrackApp).repository
        val summary = repo.observeWidgetSummary()
        val initial = summary.first()

        provideContent {
            // An update() while this session is still open does not call provideGlance again,
            // so the content follows the database itself instead of a value read once.
            val current by summary.collectAsState(initial)
            WidgetContent(done = current.first, total = current.second)
        }
    }
}

@Composable
internal fun WidgetContent(done: Int, total: Int) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .cornerRadius(16.dp)
            .background(ColorProvider(BgPaper))
            .padding(16.dp)
    ) {
        Text(
            text = androidx.glance.LocalContext.current.getString(R.string.widget_title),
            style = TextStyle(color = ColorProvider(TextPrimary), fontSize = 20.sp)
        )
        Text(
            text = androidx.glance.LocalContext.current.getString(R.string.widget_done, done),
            style = TextStyle(color = ColorProvider(TextPrimary), fontSize = 16.sp)
        )
        Text(
            text = androidx.glance.LocalContext.current.getString(R.string.widget_required, total),
            style = TextStyle(color = ColorProvider(TextPrimary), fontSize = 16.sp)
        )
    }
}
