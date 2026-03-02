package com.mohdshayan.habits.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mohdshayan.habits.data.HabitEntity
import com.mohdshayan.habits.data.HabitRepository
import com.mohdshayan.habits.domain.FrequencyType
import com.mohdshayan.habits.domain.HabitDayCell
import com.mohdshayan.habits.domain.HabitTodayItem
import com.mohdshayan.habits.domain.StoicDisciplineQuotes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

data class TodayUiState(
    val items: List<HabitTodayItem> = emptyList(),
    val quote: String = StoicDisciplineQuotes.first()
) {
    val doneCount: Int get() = items.count { it.done }
    val totalCount: Int get() = items.size
}

class TodayViewModel(private val repository: HabitRepository) : ViewModel() {
    private val launchQuote: String = StoicDisciplineQuotes.random()

    val ui: StateFlow<TodayUiState> = repository.observeTodayHabits()
        .map { TodayUiState(items = it, quote = launchQuote) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TodayUiState())

    fun toggleDone(habitId: Long, done: Boolean) {
        viewModelScope.launch { repository.setDone(habitId, LocalDate.now(), done) }
    }
}

data class HabitsUiState(
    val items: List<HabitEntity> = emptyList(),
    val selectedHabitId: Long? = null
)

class HabitsViewModel(private val repository: HabitRepository) : ViewModel() {
    private val selectedHabitId = MutableStateFlow<Long?>(null)

    val ui: StateFlow<HabitsUiState> = combine(
        repository.observeAllHabits(),
        selectedHabitId
    ) { items, selectedId ->
        val safeSelection = selectedId?.takeIf { selected -> items.any { it.id == selected } }
        HabitsUiState(items = items, selectedHabitId = safeSelection)
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HabitsUiState())

    fun selectHabit(habitId: Long) {
        selectedHabitId.value = habitId
    }

    fun clearSelection() {
        selectedHabitId.value = null
    }

    fun deleteSelectedHabit() {
        val selected = selectedHabitId.value ?: return
        viewModelScope.launch {
            repository.deleteHabit(selected)
            selectedHabitId.value = null
        }
    }
}

data class AddHabitUiState(
    val name: String = "",
    val notes: String = "",
    val frequencyType: FrequencyType = FrequencyType.DAILY,
    val weekdayMask: Int = 0b1111111,
    val intervalInput: String = "2",
    val intervalError: Boolean = false,
    val startDate: LocalDate = LocalDate.now(),
    val nameError: Boolean = false,
    val saved: Boolean = false
)

class AddHabitViewModel(private val repository: HabitRepository) : ViewModel() {
    private val _ui = MutableStateFlow(AddHabitUiState())
    val ui: StateFlow<AddHabitUiState> = _ui.asStateFlow()

    fun updateName(value: String) { _ui.value = _ui.value.copy(name = value, nameError = false) }
    fun updateNotes(value: String) { _ui.value = _ui.value.copy(notes = value) }
    fun updateFrequency(type: FrequencyType) { _ui.value = _ui.value.copy(frequencyType = type) }
    fun updateIntervalInput(value: String) {
        if (value.all { it.isDigit() }) {
            _ui.value = _ui.value.copy(intervalInput = value, intervalError = false)
        }
    }

    fun toggleWeekday(day: DayOfWeek) {
        val bit = 1 shl (day.value % 7)
        val current = _ui.value
        val nextMask = current.weekdayMask xor bit
        _ui.value = current.copy(weekdayMask = if (nextMask == 0) current.weekdayMask else nextMask)
    }

    fun save() {
        val current = _ui.value
        if (current.name.isBlank()) {
            _ui.value = current.copy(nameError = true)
            return
        }
        val intervalDays = if (current.frequencyType == FrequencyType.INTERVAL) {
            val parsed = current.intervalInput.toIntOrNull()
            if (parsed == null || parsed < 1) {
                _ui.value = current.copy(intervalError = true)
                return
            }
            parsed
        } else {
            1
        }
        viewModelScope.launch {
            repository.addHabit(
                HabitEntity(
                    name = current.name.trim(),
                    notes = current.notes.trim(),
                    frequencyType = current.frequencyType.name,
                    weekdayMask = current.weekdayMask,
                    intervalDays = intervalDays,
                    startEpochDay = current.startDate.toEpochDay(),
                    createdAtEpochMillis = System.currentTimeMillis()
                )
            )
            _ui.value = _ui.value.copy(saved = true)
        }
    }
}

data class HabitDetailUiState(
    val habitId: Long,
    val title: String = "",
    val notes: String = "",
    val cells: List<HabitDayCell> = emptyList()
)

class HabitDetailViewModel(
    habitId: Long,
    private val repository: HabitRepository
) : ViewModel() {
    private val month = MutableStateFlow(LocalDate.now().withDayOfMonth(1))

    val ui: StateFlow<HabitDetailUiState> = combine(
        repository.observeHabit(habitId),
        repository.observeHabitCalendar(habitId),
        month
    ) { habit, records, targetMonth ->
        if (habit == null) {
            HabitDetailUiState(habitId = habitId)
        } else {
            HabitDetailUiState(
                habitId = habitId,
                title = habit.name,
                notes = habit.notes,
                cells = repository.buildMonthCells(habit, records, targetMonth)
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HabitDetailUiState(habitId = habitId))

    fun toggleDone(epochDay: Long, done: Boolean) {
        viewModelScope.launch {
            repository.setDone(ui.value.habitId, LocalDate.ofEpochDay(epochDay), done)
        }
    }
}

class HabitViewModelFactory(
    private val repository: HabitRepository,
    private val habitId: Long? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(TodayViewModel::class.java) -> TodayViewModel(repository) as T
            modelClass.isAssignableFrom(HabitsViewModel::class.java) -> HabitsViewModel(repository) as T
            modelClass.isAssignableFrom(AddHabitViewModel::class.java) -> AddHabitViewModel(repository) as T
            modelClass.isAssignableFrom(HabitDetailViewModel::class.java) -> {
                HabitDetailViewModel(habitId ?: 0L, repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
