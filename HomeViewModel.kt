package com.macroai.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    // UI State
    var state by mutableStateOf(HomeScreenState())
        private set

    init {
        // Initialize with current date and mock data
        // In a real app, we would fetch data from a repository
        selectDate(LocalDate.now())
    }

    fun selectDate(date: LocalDate) {
        // In a real app, we would fetch data for the specific date
        // For now, we'll just update the selected date and use mock data
        state = state.copy(
            selectedDate = date,
            // Mock data - would be fetched from repository in real app
            calories = CaloriesData(
                goal = 2000,
                consumed = 1200,
                burned = 300
            ),
            macros = MacrosData(
                protein = MacroNutrient("Protein", 120, 170),
                carbs = MacroNutrient("Carbs", 145, 200),
                fat = MacroNutrient("Fat", 35, 55)
            ),
            foodLogs = listOf(
                FoodLog("Breakfast", "Eggs and Toast", 350),
                FoodLog("Lunch", "Chicken Salad", 450),
                FoodLog("Dinner", "Salmon with Vegetables", 400)
            )
        )
    }

    // Navigation to previous date
    fun navigateToPreviousDay() {
        selectDate(state.selectedDate.minusDays(1))
    }

    // Navigation to next date
    fun navigateToNextDay() {
        selectDate(state.selectedDate.plusDays(1))
    }

    // Check if the date is today
    fun isToday(date: LocalDate): Boolean {
        return date.isEqual(LocalDate.now())
    }
}

// UI State model
data class HomeScreenState(
    val selectedDate: LocalDate = LocalDate.now(),
    val calories: CaloriesData = CaloriesData(),
    val macros: MacrosData = MacrosData(),
    val foodLogs: List<FoodLog> = emptyList()
)

data class CaloriesData(
    val goal: Int = 0,
    val consumed: Int = 0,
    val burned: Int = 0,
    val remaining: Int = 0
) {
    constructor(goal: Int, consumed: Int, burned: Int) : this(
        goal = goal,
        consumed = consumed,
        burned = burned,
        remaining = goal - consumed + burned
    )
}

data class MacrosData(
    val protein: MacroNutrient = MacroNutrient("Protein", 0, 0),
    val carbs: MacroNutrient = MacroNutrient("Carbs", 0, 0),
    val fat: MacroNutrient = MacroNutrient("Fat", 0, 0)
)

data class MacroNutrient(
    val name: String,
    val consumed: Int,
    val goal: Int
)

data class FoodLog(
    val meal: String,
    val description: String,
    val calories: Int
)
