package com.macroai.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.macroai.models.Gender
import com.macroai.models.GoalSpeed
import com.macroai.models.GoalType
import java.util.Locale

private fun String.toTitleCase(): String =
    lowercase(Locale.getDefault()).replaceFirstChar { it.titlecase(Locale.getDefault()) }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val state = viewModel.state
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            snackbarHostState.showSnackbar("Profile saved successfully!")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Profile & Goals") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            PersonalInformationSection(
                age = state.ageInput,
                onAgeChange = { viewModel.updateAge(it) },
                height = state.heightInput,
                onHeightChange = { viewModel.updateHeight(it) },
                weight = state.weightInput,
                onWeightChange = { viewModel.updateWeight(it) }
            )

            GenderSelectionSection(
                selectedGender = state.selectedGender,
                onGenderSelected = { viewModel.updateGender(it) }
            )

            GoalSelectionSection(
                selectedGoalType = state.selectedGoalType,
                onGoalTypeSelected = { viewModel.updateGoalType(it) },
                selectedGoalSpeed = state.selectedGoalSpeed,
                onGoalSpeedSelected = { viewModel.updateGoalSpeed(it) }
            )

            CalculatedMacrosSection(
                calculatedTargets = state.calculatedTargets
            )

            Button(
                onClick = { viewModel.saveUserProfile() },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text("Save Profile")
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInformationSection(
    age: String,
    onAgeChange: (String) -> Unit,
    height: String,
    onHeightChange: (String) -> Unit,
    weight: String,
    onWeightChange: (String) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Personal Information",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = age,
                onValueChange = onAgeChange,
                label = { Text("Age") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = height,
                onValueChange = onHeightChange,
                label = { Text("Height (cm)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = weight,
                onValueChange = onWeightChange,
                label = { Text("Weight (kg)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}

@Composable
fun GenderSelectionSection(
    selectedGender: Gender,
    onGenderSelected: (Gender) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Gender",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(Modifier.selectableGroup()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .selectable(
                            selected = selectedGender == Gender.MALE,
                            onClick = { onGenderSelected(Gender.MALE) }
                        )
                        .padding(end = 16.dp)
                ) {
                    RadioButton(
                        selected = selectedGender == Gender.MALE,
                        onClick = null
                    )
                    Text(
                        text = "Male",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.selectable(
                        selected = selectedGender == Gender.FEMALE,
                        onClick = { onGenderSelected(Gender.FEMALE) }
                    )
                ) {
                    RadioButton(
                        selected = selectedGender == Gender.FEMALE,
                        onClick = null
                    )
                    Text(
                        text = "Female",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalSelectionSection(
    selectedGoalType: GoalType,
    onGoalTypeSelected: (GoalType) -> Unit,
    selectedGoalSpeed: GoalSpeed,
    onGoalSpeedSelected: (GoalSpeed) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Your Goal",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            var goalTypeExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = goalTypeExpanded,
                onExpandedChange = { goalTypeExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedGoalType.name.toTitleCase(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Goal Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = goalTypeExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = goalTypeExpanded,
                    onDismissRequest = { goalTypeExpanded = false }
                ) {
                    GoalType.values().forEach { goalType ->
                        DropdownMenuItem(
                            text = { Text(goalType.name.toTitleCase()) },
                            onClick = {
                                onGoalTypeSelected(goalType)
                                goalTypeExpanded = false
                            }
                        )
                    }
                }
            }

            if (selectedGoalType != GoalType.MAINTAIN) {
                var goalSpeedExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = goalSpeedExpanded,
                    onExpandedChange = { goalSpeedExpanded = it }
                ) {
                    OutlinedTextField(
                        value = "${selectedGoalSpeed.name.toTitleCase()} (${selectedGoalSpeed.description})",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Goal Speed") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = goalSpeedExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = goalSpeedExpanded,
                        onDismissRequest = { goalSpeedExpanded = false }
                    ) {
                        GoalSpeed.values().forEach { goalSpeed ->
                            DropdownMenuItem(
                                text = {
                                    Text("${goalSpeed.name.toTitleCase()} (${goalSpeed.description})")
                                },
                                onClick = {
                                    onGoalSpeedSelected(goalSpeed)
                                    goalSpeedExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatedMacrosSection(calculatedTargets: CalculatedTargets) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Your Daily Targets",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            HorizontalDivider()

            MacroRow("Calories", calculatedTargets.calories.toString() + " kcal")
            MacroRow("Protein", calculatedTargets.protein.toString() + "g")
            MacroRow("Carbs", calculatedTargets.carbs.toString() + "g")
            MacroRow("Fat", calculatedTargets.fat.toString() + "g")

            if (calculatedTargets.calories < 1200) {
                HorizontalDivider()
                Text(
                    text = "Warning: Calorie target is very low. Consider a slower weight loss pace.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun MacroRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}
