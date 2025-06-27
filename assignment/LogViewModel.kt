package com.macroai.screens.log

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.macroai.data.local.LogEntry
import com.macroai.models.EntryType
import com.macroai.models.ExerciseData
import com.macroai.models.NutritionData
import com.macroai.repository.LogRepository
import com.macroai.repository.MacroRepository
import com.macroai.repository.ProcessingResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LogViewModel @Inject constructor(
    private val macroRepository: MacroRepository,
    private val logRepository: LogRepository
) : ViewModel() {

    var state by mutableStateOf(LogScreenState())
        private set

    init {
        loadLogEntries()
    }

    private fun loadLogEntries() {
        viewModelScope.launch {
            logRepository.getAllLogEntries().collect { entries ->
                state = state.copy(logEntries = entries)
            }
        }
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

                                // Save to database
                                logRepository.insertLogEntry(newEntry)

                                // Clear the input and stop loading
                                state = state.copy(
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

                                // Save to database
                                logRepository.insertLogEntry(newEntry)

                                // Clear the input and stop loading
                                state = state.copy(
                                    inputText = "",
                                    isLoading = false
                                )
                            }
                        }
                    }

                    is ProcessingResult.Error -> {
                        state = state.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun processFoodImage(bitmap: Bitmap) {
        // Image processing is not currently implemented in MacroRepository
        // This function is a placeholder for future image processing functionality
        state = state.copy(
            isLoading = false,
            errorMessage = "Image processing not yet implemented"
        )
    }

    fun editEntry(entry: LogEntry) {
        state = state.copy(editingEntry = entry)
    }

    fun updateEntry(updatedEntry: LogEntry) {
        viewModelScope.launch {
            logRepository.updateLogEntry(updatedEntry)
            state = state.copy(editingEntry = null)
        }
    }

    fun cancelEdit() {
        state = state.copy(editingEntry = null)
    }

    fun deleteEntry(entry: LogEntry) {
        viewModelScope.launch {
            logRepository.deleteLogEntry(entry)
        }
    }
}

data class LogScreenState(
    val inputText: String = "",
    val logEntries: List<LogEntry> = emptyList(),
    val editingEntry: LogEntry? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
