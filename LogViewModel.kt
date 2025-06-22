package com.macroai.screens.log

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.macroai.models.EntryType
import com.macroai.models.ExerciseData
import com.macroai.models.NutritionData
import com.macroai.repository.MacroRepository
import com.macroai.repository.ProcessingResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LogViewModel @Inject constructor(
    private val macroRepository: MacroRepository
) : ViewModel() {

    var state by mutableStateOf(LogScreenState())
        private set

    init {
        // In a real app, we would load data from a repository
        loadSampleData()
    }

    private fun loadSampleData() {
        // Just for demo purposes - would be loaded from repository in a real app
        state = state.copy(
            logEntries = listOf(
                LogEntry(
                    id = UUID.randomUUID().toString(),
                    name = "Boiled Egg",
                    calories = 70,
                    macros = "Protein: 6g, Fat: 5g, Carbs: 0g",
                    type = EntryType.FOOD
                ),
                LogEntry(
                    id = UUID.randomUUID().toString(),
                    name = "Basmati Rice",
                    calories = 130,
                    macros = "Protein: 3g, Fat: 0g, Carbs: 28g",
                    type = EntryType.FOOD
                ),
                LogEntry(
                    id = UUID.randomUUID().toString(),
                    name = "Morning Run",
                    calories = 320,
                    macros = "30 minutes, 5km",
                    type = EntryType.EXERCISE
                )
            )
        )
    }

    fun updateInputText(text: String) {
        state = state.copy(inputText = text)
    }

    fun processLogEntry() {
        val inputText = state.inputText.trim()
        if (inputText.isEmpty()) return

        // Set loading state and clear any previous error
        state = state.copy(
            isLoading = true,
            errorMessage = null
        )

        // Use the MacroRepository to process the input with Gemini AI
        viewModelScope.launch {
            macroRepository.processUserInput(inputText).collect { result ->
                when (result) {
                    is ProcessingResult.Loading -> {
                        // Already set the loading state above
                    }

                    is ProcessingResult.Success<*> -> {
                        when (val data = result.data) {
                            is NutritionData -> {
                                // Create a new food log entry from the nutrition data
                                val newEntry = LogEntry(
                                    id = UUID.randomUUID().toString(),
                                    name = data.name,
                                    calories = data.calories,
                                    macros = data.toMacrosString(),
                                    type = EntryType.FOOD
                                )

                                // Add the new entry to the list and clear the input
                                state = state.copy(
                                    logEntries = state.logEntries + newEntry,
                                    inputText = "",
                                    isLoading = false
                                )
                            }

                            is ExerciseData -> {
                                // Create a new exercise log entry from the exercise data
                                val newEntry = LogEntry(
                                    id = UUID.randomUUID().toString(),
                                    name = data.name,
                                    calories = data.calories,
                                    macros = data.toMacrosString(),
                                    type = EntryType.EXERCISE
                                )

                                // Add the new entry to the list and clear the input
                                state = state.copy(
                                    logEntries = state.logEntries + newEntry,
                                    inputText = "",
                                    isLoading = false
                                )
                            }

                            else -> {
                                // Handle unexpected data type
                                state = state.copy(
                                    errorMessage = "Unexpected data type received",
                                    isLoading = false
                                )
                            }
                        }
                    }

                    is ProcessingResult.Error -> {
                        // Update the state with the error message
                        state = state.copy(
                            errorMessage = result.message,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun processFoodImage(bitmap: Bitmap) {
        // Set loading state and clear any previous error
        state = state.copy(
            isLoading = true,
            errorMessage = null
        )

        // Use the MacroRepository to process the food image with Gemini AI
        viewModelScope.launch {
            macroRepository.processFoodImage(bitmap).collect { result ->
                when (result) {
                    is ProcessingResult.Loading -> {
                        // Already set the loading state above
                    }

                    is ProcessingResult.Success<*> -> {
                        val nutritionData = result.data as? NutritionData
                        if (nutritionData != null) {
                            // Create a new food log entry from the nutrition data
                            val newEntry = LogEntry(
                                id = UUID.randomUUID().toString(),
                                name = nutritionData.name,
                                calories = nutritionData.calories,
                                macros = nutritionData.toMacrosString(),
                                type = EntryType.FOOD
                            )

                            // Add the new entry to the list
                            state = state.copy(
                                logEntries = state.logEntries + newEntry,
                                isLoading = false
                            )
                        } else {
                            state = state.copy(
                                errorMessage = "Failed to process image data",
                                isLoading = false
                            )
                        }
                    }

                    is ProcessingResult.Error -> {
                        // Update the state with the error message
                        state = state.copy(
                            errorMessage = result.message,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun editEntry(entry: LogEntry) {
        // For now, we'll just populate the input field with the entry name
        // In a real app, we would set an editing state and handle updates
        state = state.copy(
            inputText = entry.name,
            editingEntryId = entry.id
        )
    }

    fun deleteEntry(entry: LogEntry) {
        state = state.copy(
            logEntries = state.logEntries.filter { it.id != entry.id }
        )
    }
}

data class LogScreenState(
    val inputText: String = "",
    val logEntries: List<LogEntry> = emptyList(),
    val editingEntryId: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class LogEntry(
    val id: String,
    val name: String,
    val calories: Int,
    val macros: String,
    val type: EntryType
)
