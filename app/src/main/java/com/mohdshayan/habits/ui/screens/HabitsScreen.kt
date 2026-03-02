package com.mohdshayan.habits.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mohdshayan.habits.R
import com.mohdshayan.habits.data.HabitEntity
import com.mohdshayan.habits.ui.HabitsUiState

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HabitsScreen(
    ui: HabitsUiState,
    onBackToToday: () -> Unit,
    onAdd: () -> Unit,
    onOpenHabit: (Long) -> Unit,
    onSelectHabit: (Long) -> Unit,
    onDeleteSelected: () -> Unit,
    onClearSelection: () -> Unit
) {
    val inSelectionMode = ui.selectedHabitId != null
    val clearSelectionInteraction = remember { MutableInteractionSource() }

    BackHandler(enabled = inSelectionMode, onBack = onClearSelection)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.screen_habits)) },
                actions = {
                    if (inSelectionMode) {
                        IconButton(onClick = onDeleteSelected) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_menu_delete),
                                contentDescription = stringResource(R.string.delete)
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(20.dp)
                .clickable(
                    enabled = inSelectionMode,
                    interactionSource = clearSelectionInteraction,
                    indication = null,
                    onClick = onClearSelection
                )
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onBackToToday) {
                    Text(stringResource(R.string.screen_today))
                }
                Button(onClick = onAdd) {
                    Text(stringResource(R.string.add_habit))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            if (ui.items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.TopStart
                ) {
                    Text(
                        text = stringResource(R.string.no_habits_any),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ui.items, key = { it.id }) { habit ->
                        HabitListItem(
                            habit = habit,
                            selected = habit.id == ui.selectedHabitId,
                            inSelectionMode = inSelectionMode,
                            onOpenHabit = onOpenHabit,
                            onSelectHabit = onSelectHabit,
                            onClearSelection = onClearSelection
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HabitListItem(
    habit: HabitEntity,
    selected: Boolean,
    inSelectionMode: Boolean,
    onOpenHabit: (Long) -> Unit,
    onSelectHabit: (Long) -> Unit,
    onClearSelection: () -> Unit
) {
    val selectedColor = if (selected) {
        MaterialTheme.colorScheme.secondary.copy(alpha = 0.14f)
    } else {
        MaterialTheme.colorScheme.background
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = selectedColor, shape = RoundedCornerShape(12.dp))
            .combinedClickable(
                onClick = {
                    if (inSelectionMode) onClearSelection() else onOpenHabit(habit.id)
                },
                onLongClick = { onSelectHabit(habit.id) }
            )
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Text(text = habit.name, style = MaterialTheme.typography.titleLarge)
        if (habit.notes.isNotBlank()) {
            Text(
                text = habit.notes,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
