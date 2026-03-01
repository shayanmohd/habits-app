package com.mohdshayan.habits.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mohdshayan.habits.R
import com.mohdshayan.habits.domain.FrequencyType
import com.mohdshayan.habits.ui.AddHabitUiState
import java.time.DayOfWeek

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun AddHabitScreen(
    ui: AddHabitUiState,
    onBack: () -> Unit,
    onNameChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onFrequencyChange: (FrequencyType) -> Unit,
    onIntervalInputChange: (String) -> Unit,
    onToggleWeekday: (DayOfWeek) -> Unit,
    onSave: () -> Unit,
    onSaved: () -> Unit
) {
    LaunchedEffect(ui.saved) {
        if (ui.saved) onSaved()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = stringResource(R.string.add_habit), style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = ui.name,
            onValueChange = onNameChange,
            label = { Text(stringResource(R.string.habit_name)) },
            modifier = Modifier.fillMaxWidth(),
            isError = ui.nameError,
            singleLine = true
        )
        if (ui.nameError) {
            Text(
                text = stringResource(R.string.validation_name_required),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        OutlinedTextField(
            value = ui.notes,
            onValueChange = onNotesChange,
            label = { Text(stringResource(R.string.habit_notes)) },
            modifier = Modifier.fillMaxWidth()
        )

        Text(text = stringResource(R.string.frequency), style = MaterialTheme.typography.titleLarge)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FrequencyButton(
                text = stringResource(R.string.frequency_daily),
                selected = ui.frequencyType == FrequencyType.DAILY,
                onClick = { onFrequencyChange(FrequencyType.DAILY) }
            )
            FrequencyButton(
                text = stringResource(R.string.frequency_weekly),
                selected = ui.frequencyType == FrequencyType.WEEKLY,
                onClick = { onFrequencyChange(FrequencyType.WEEKLY) }
            )
            FrequencyButton(
                text = stringResource(R.string.frequency_interval),
                selected = ui.frequencyType == FrequencyType.INTERVAL,
                onClick = { onFrequencyChange(FrequencyType.INTERVAL) }
            )
        }

        if (ui.frequencyType == FrequencyType.WEEKLY) {
            WeekdayChooser(ui.weekdayMask, onToggleWeekday)
        }

        if (ui.frequencyType == FrequencyType.INTERVAL) {
            OutlinedTextField(
                value = ui.intervalInput,
                onValueChange = onIntervalInputChange,
                label = { Text(stringResource(R.string.interval_days)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = ui.intervalError
            )
            if (ui.intervalError) {
                Text(
                    text = stringResource(R.string.validation_interval_required),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onSave) { Text(stringResource(R.string.save)) }
            OutlinedButton(onClick = onBack) { Text(stringResource(R.string.cancel)) }
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun WeekdayChooser(mask: Int, onToggleWeekday: (DayOfWeek) -> Unit) {
    val days = listOf(
        DayOfWeek.SUNDAY to R.string.weekday_sun,
        DayOfWeek.MONDAY to R.string.weekday_mon,
        DayOfWeek.TUESDAY to R.string.weekday_tue,
        DayOfWeek.WEDNESDAY to R.string.weekday_wed,
        DayOfWeek.THURSDAY to R.string.weekday_thu,
        DayOfWeek.FRIDAY to R.string.weekday_fri,
        DayOfWeek.SATURDAY to R.string.weekday_sat
    )

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = 4
    ) {
        days.forEach { (day, resId) ->
            val bit = 1 shl (day.value % 7)
            FrequencyButton(
                text = stringResource(resId),
                selected = mask and bit != 0,
                onClick = { onToggleWeekday(day) }
            )
        }
    }
}

@Composable
private fun FrequencyButton(text: String, selected: Boolean, onClick: () -> Unit) {
    if (selected) {
        Button(onClick = onClick) {
            Text(text = text, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    } else {
        OutlinedButton(onClick = onClick) {
            Text(text = text, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}
