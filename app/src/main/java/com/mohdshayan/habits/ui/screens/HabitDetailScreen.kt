package com.mohdshayan.habits.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mohdshayan.habits.R
import com.mohdshayan.habits.domain.HabitDayStatus
import com.mohdshayan.habits.ui.HabitDetailUiState
import com.mohdshayan.habits.ui.theme.DoneGreen
import com.mohdshayan.habits.ui.theme.MissedRed
import com.mohdshayan.habits.ui.theme.NotRequiredGray
import java.time.LocalDate

@Composable
fun HabitDetailScreen(
    ui: HabitDetailUiState,
    onBack: () -> Unit,
    onToggleDone: (Long, Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        Button(onClick = onBack) { Text(stringResource(R.string.back)) }
        Spacer(modifier = Modifier.height(12.dp))

        Text(text = ui.title, style = MaterialTheme.typography.headlineMedium)
        if (ui.notes.isNotBlank()) {
            Text(
                text = ui.notes,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = stringResource(R.string.calendar), style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(ui.cells, key = { it.date.toEpochDay() }) { cell ->
                val color = when (cell.status) {
                    HabitDayStatus.DONE -> DoneGreen
                    HabitDayStatus.MISSED -> MissedRed
                    HabitDayStatus.NOT_REQUIRED -> NotRequiredGray
                }
                DayCell(
                    day = cell.date.dayOfMonth,
                    color = color,
                    clickable = cell.status != HabitDayStatus.NOT_REQUIRED,
                    checked = cell.status == HabitDayStatus.DONE,
                    onToggle = {
                        val next = cell.status != HabitDayStatus.DONE
                        onToggleDone(cell.date.toEpochDay(), next)
                    }
                )
            }
        }
    }
}

@Composable
private fun DayCell(
    day: Int,
    color: Color,
    clickable: Boolean,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(color)
            .clickable(enabled = clickable, onClick = onToggle)
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = day.toString(), style = MaterialTheme.typography.bodySmall)
        if (clickable) {
            Checkbox(checked = checked, onCheckedChange = { onToggle() })
        } else {
            Box(modifier = Modifier.height(24.dp))
        }
    }
}
