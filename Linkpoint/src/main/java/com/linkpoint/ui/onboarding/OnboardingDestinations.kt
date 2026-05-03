package com.linkpoint.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class OnboardingEntryScreenState(
    val title: String = "Onboarding",
    val firstName: String = "",
    val lastName: String = "",
    val password: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingEntryDestination(
    state: OnboardingEntryScreenState = OnboardingEntryScreenState(),
    onFirstNameChanged: (String) -> Unit = {},
    onLastNameChanged: (String) -> Unit = {},
    onPasswordChanged: (String) -> Unit = {},
    onContinue: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(topBar = { TopAppBar(title = { Text(state.title) }) }) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(state.firstName, onFirstNameChanged, label = { Text("First name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(state.lastName, onLastNameChanged, label = { Text("Last name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(state.password, onPasswordChanged, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = onContinue, enabled = state.firstName.isNotBlank() && state.password.isNotBlank(), modifier = Modifier.fillMaxWidth()) {
                Text("Continue")
            }
        }
    }
}

data class StartLocationScreenState(
    val title: String = "Start Location",
    val selectedLocation: String = "Home",
    val availableLocations: List<String> = listOf("Home", "Last", "WelcomeHub")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartLocationDestination(
    state: StartLocationScreenState = StartLocationScreenState(),
    onLocationSelected: (String) -> Unit = {},
    onJoinWorld: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Scaffold(topBar = { TopAppBar(title = { Text(state.title) }) }) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = state.selectedLocation,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Spawn location") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    state.availableLocations.forEach { location ->
                        DropdownMenuItem(
                            text = { Text(location) },
                            onClick = {
                                onLocationSelected(location)
                                expanded = false
                            }
                        )
                    }
                }
            }
            Button(onClick = onJoinWorld, modifier = Modifier.fillMaxWidth()) {
                Text("Join world")
            }
        }
    }
}
