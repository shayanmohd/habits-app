package com.mohdshayan.habits

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mohdshayan.habits.ui.AddHabitViewModel
import com.mohdshayan.habits.ui.HabitDetailViewModel
import com.mohdshayan.habits.ui.HabitViewModelFactory
import com.mohdshayan.habits.ui.HabitsViewModel
import com.mohdshayan.habits.ui.TodayViewModel
import com.mohdshayan.habits.ui.screens.AddHabitScreen
import com.mohdshayan.habits.ui.screens.HabitDetailScreen
import com.mohdshayan.habits.ui.screens.HabitsScreen
import com.mohdshayan.habits.ui.screens.TodayScreen
import com.mohdshayan.habits.ui.theme.HabitTrackTheme

class MainActivity : ComponentActivity() {
    private val todayViewModel: TodayViewModel by viewModels {
        HabitViewModelFactory((application as HabitTrackApp).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HabitTrackTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNav(todayViewModel)
                }
            }
        }
    }
}

@Composable
private fun AppNav(todayViewModel: TodayViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "today") {
        composable("today") {
            val ui by todayViewModel.ui.collectAsStateWithLifecycle()
            TodayScreen(
                ui = ui,
                onAdd = { navController.navigate("add") },
                onViewHabits = { navController.navigate("habits") },
                onOpenHabit = { id -> navController.navigate("detail/$id") },
                onToggleDone = todayViewModel::toggleDone
            )
        }

        composable("habits") {
            val vm: HabitsViewModel = viewModel(
                factory = HabitViewModelFactory((navController.context.applicationContext as HabitTrackApp).repository)
            )
            val ui by vm.ui.collectAsStateWithLifecycle()
            HabitsScreen(
                ui = ui,
                onBackToToday = { navController.popBackStack() },
                onAdd = { navController.navigate("add") },
                onOpenHabit = { id -> navController.navigate("detail/$id") },
                onSelectHabit = vm::selectHabit,
                onDeleteSelected = vm::deleteSelectedHabit,
                onClearSelection = vm::clearSelection
            )
        }

        composable("add") {
            val vm: AddHabitViewModel = viewModel(
                factory = HabitViewModelFactory((navController.context.applicationContext as HabitTrackApp).repository)
            )
            val ui by vm.ui.collectAsStateWithLifecycle()
            AddHabitScreen(
                ui = ui,
                onBack = { navController.popBackStack() },
                onNameChange = vm::updateName,
                onNotesChange = vm::updateNotes,
                onFrequencyChange = vm::updateFrequency,
                onIntervalInputChange = vm::updateIntervalInput,
                onToggleWeekday = vm::toggleWeekday,
                onSave = vm::save,
                onSaved = {
                    navController.popBackStack("today", inclusive = false)
                }
            )
        }

        composable(
            route = "detail/{habitId}",
            arguments = listOf(navArgument("habitId") { type = NavType.LongType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getLong("habitId") ?: 0L
            val vm: HabitDetailViewModel = viewModel(
                key = "detail_$habitId",
                factory = HabitViewModelFactory(
                    repository = (navController.context.applicationContext as HabitTrackApp).repository,
                    habitId = habitId
                )
            )
            val ui by vm.ui.collectAsStateWithLifecycle()
            HabitDetailScreen(
                ui = ui,
                onBack = { navController.popBackStack() },
                onToggleDone = vm::toggleDone
            )
        }
    }
}
