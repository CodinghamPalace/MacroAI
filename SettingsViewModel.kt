package com.macroai.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.macroai.models.Gender
import com.macroai.models.GoalSpeed
import com.macroai.models.GoalType
import com.macroai.models.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    var state by mutableStateOf(SettingsScreenState())
        private set

    init {
        // In a real app, we would load from a repository or DataStore
        loadSampleUserProfile()
    }

    private fun loadSampleUserProfile() {
        // Sample profile similar to Scenario 1 in requirements
        val userProfile = UserProfile(
            age = 23,
            gender = Gender.MALE,
            heightCm = 177,
            weightKg = 77.0,
            goalType = GoalType.LOSE,
            goalSpeed = GoalSpeed.MODERATE
        )

        updateStateFromProfile(userProfile)
    }

    private fun updateStateFromProfile(profile: UserProfile) {
        // Calculate all the targets based on the profile
        val targetCalories = profile.calculateTargetCalories()
        val proteinTarget = profile.calculateProteinTarget()
        val fatTarget = profile.calculateFatTarget()
        val carbTarget = profile.calculateCarbTarget()

        state = state.copy(
            userProfile = profile,
            ageInput = profile.age.toString(),
            heightInput = profile.heightCm.toString(),
            weightInput = profile.weightKg.toString(),
            selectedGender = profile.gender,
            selectedGoalType = profile.goalType,
            selectedGoalSpeed = profile.goalSpeed,
            calculatedTargets = CalculatedTargets(
                calories = targetCalories,
                protein = proteinTarget,
                fat = fatTarget,
                carbs = carbTarget
            )
        )
    }

    fun updateAge(age: String) {
        state = state.copy(ageInput = age)

        // Update calculated values if we have valid input
        updateCalculatedValues()
    }

    fun updateHeight(height: String) {
        state = state.copy(heightInput = height)
        updateCalculatedValues()
    }

    fun updateWeight(weight: String) {
        state = state.copy(weightInput = weight)
        updateCalculatedValues()
    }

    fun updateGender(gender: Gender) {
        state = state.copy(selectedGender = gender)
        updateCalculatedValues()
    }

    fun updateGoalType(goalType: GoalType) {
        state = state.copy(selectedGoalType = goalType)
        updateCalculatedValues()
    }

    fun updateGoalSpeed(goalSpeed: GoalSpeed) {
        state = state.copy(selectedGoalSpeed = goalSpeed)
        updateCalculatedValues()
    }

    private fun updateCalculatedValues() {
        // Parse inputs, handling possible format errors
        val age = state.ageInput.toIntOrNull() ?: return
        val height = state.heightInput.toIntOrNull() ?: return
        val weight = state.weightInput.toDoubleOrNull() ?: return

        // Create updated profile
        val updatedProfile = UserProfile(
            age = age,
            gender = state.selectedGender,
            heightCm = height,
            weightKg = weight,
            goalType = state.selectedGoalType,
            goalSpeed = state.selectedGoalSpeed
        )

        // Update the profile and recalculate targets
        state = state.copy(userProfile = updatedProfile)

        // Calculate all the targets
        val targetCalories = updatedProfile.calculateTargetCalories()
        val proteinTarget = updatedProfile.calculateProteinTarget()
        val fatTarget = updatedProfile.calculateFatTarget()
        val carbTarget = updatedProfile.calculateCarbTarget()

        state = state.copy(
            calculatedTargets = CalculatedTargets(
                calories = targetCalories,
                protein = proteinTarget,
                fat = fatTarget,
                carbs = carbTarget
            )
        )
    }

    fun saveUserProfile() {
        // In a real app, we would save to a repository

        // Flag successful save
        state = state.copy(
            saveSuccess = true
        )

        // Reset the flag after a delay
        viewModelScope.launch {
            kotlinx.coroutines.delay(3000)
            state = state.copy(saveSuccess = false)
        }
    }
}

data class SettingsScreenState(
    val userProfile: UserProfile = UserProfile(),
    // Input fields
    val ageInput: String = "",
    val heightInput: String = "",
    val weightInput: String = "",
    val selectedGender: Gender = Gender.MALE,
    val selectedGoalType: GoalType = GoalType.MAINTAIN,
    val selectedGoalSpeed: GoalSpeed = GoalSpeed.MODERATE,
    // Calculation results
    val calculatedTargets: CalculatedTargets = CalculatedTargets(),
    // UI state
    val saveSuccess: Boolean = false,
    val inputErrors: Map<String, String> = emptyMap()
)

data class CalculatedTargets(
    val calories: Int = 0,
    val protein: Int = 0,
    val fat: Int = 0,
    val carbs: Int = 0
)
