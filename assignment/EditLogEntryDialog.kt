package com.macroai.screens.log

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.macroai.data.local.LogEntry
import com.macroai.models.EntryType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditLogEntryDialog(
    entry: LogEntry?,
    onDismiss: () -> Unit,
    onConfirm: (LogEntry) -> Unit
) {
    if (entry == null) return

    var name by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }

    // Parse existing macros and populate fields
    LaunchedEffect(entry) {
        name = entry.name
        calories = entry.calories.toString()

        // Parse the macros string to extract individual values
        if (entry.type == EntryType.FOOD) {
            val macrosParts = entry.macros.split(", ")
            macrosParts.forEach { part ->
                when {
                    part.startsWith("Protein:") -> {
                        protein = part.substringAfter(":").replace("g", "").trim()
                    }
                    part.startsWith("Carbs:") -> {
                        carbs = part.substringAfter(":").replace("g", "").trim()
                    }
                    part.startsWith("Fat:") -> {
                        fat = part.substringAfter(":").replace("g", "").trim()
                    }
                }
            }
        } else {
            // For exercise entries, we don't edit macros
            protein = "0"
            carbs = "0"
            fat = "0"
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Entry") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = calories,
                    onValueChange = { calories = it },
                    label = { Text("Calories") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                if (entry.type == EntryType.FOOD) {
                    Text("Macronutrients")

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = protein,
                            onValueChange = { protein = it },
                            label = { Text("Protein (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = carbs,
                            onValueChange = { carbs = it },
                            label = { Text("Carbs (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = fat,
                            onValueChange = { fat = it },
                            label = { Text("Fat (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updatedEntry = entry.copy(
                        name = name.trim(),
                        calories = calories.toIntOrNull() ?: entry.calories,
                        macros = if (entry.type == EntryType.FOOD) {
                            "Protein: ${protein.toDoubleOrNull() ?: 0.0}g, Carbs: ${carbs.toDoubleOrNull() ?: 0.0}g, Fat: ${fat.toDoubleOrNull() ?: 0.0}g"
                        } else {
                            entry.macros // Keep original macros for exercise entries
                        }
                    )
                    onConfirm(updatedEntry)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
